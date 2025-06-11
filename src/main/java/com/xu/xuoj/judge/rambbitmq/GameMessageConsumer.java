package com.xu.xuoj.judge.rambbitmq;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import com.xu.xuoj.judge.codesandbox.model.JudgeInfo;
import com.xu.xuoj.mapper.GameQuestionMapper;
import com.xu.xuoj.mapper.GameRankMapper;
import com.xu.xuoj.model.entity.*;
import com.xu.xuoj.model.enums.JudgeInfoMessageEnum;
import com.xu.xuoj.service.QuestionService;
import com.xu.xuoj.service.QuestionSubmitService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GameMessageConsumer {

    @Autowired
    private QuestionSubmitService questionSubmitService;

    @Autowired
    private GameQuestionMapper gameQuestionMapper;

    @Autowired
    private GameRankMapper gameRankMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 接收消息的方法
     *
     * @param message     接收到的消息内容，是一个字符串类型
     * @param channel     消息所在的通道，可以通过该通道与 RabbitMQ 进行交互，例如手动确认消息、拒绝消息等
     * @param deliveryTag 消息的投递标签，用于唯一标识一条消息
     */
    @SneakyThrows
    @RabbitListener(queues = {"game_queue"}, ackMode = "MANUAL")
    @Transactional(rollbackFor = Exception.class)
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        // 使用日志记录器打印接收到的消息内容
        log.info("receiveMessage message = {}", message);
        GameSubmissionMessage gameSubmissionMessage = JSONUtil.toBean(message, GameSubmissionMessage.class);
        Long gameId = gameSubmissionMessage.getGameId();
        Long userId = gameSubmissionMessage.getUserId();
        String userName = gameSubmissionMessage.getUserName();
        Long submissionId = gameSubmissionMessage.getSubmissionId();

        // 获取提交的题目信息
        QuestionSubmit nowSubmit = questionSubmitService.getById(submissionId);
        if (nowSubmit == null) {
            log.error("未找到提交记录: submissionId={}", submissionId);
            // 可能需要重试逻辑，或者此消息确实无效
            return;
        }
        //获取题目的满分
        QueryWrapper<GameQuestion> gameQuestionQueryWrapper = new QueryWrapper<>();
        gameQuestionQueryWrapper.eq("gameId", gameId);
        List<GameQuestion> gameQuestionList = gameQuestionMapper.selectList(gameQuestionQueryWrapper);
        Map<Long, Integer> questionIdToFullScore = gameQuestionList.stream()
            .collect(Collectors.toMap(GameQuestion::getQuestionId, GameQuestion::getScore, (s1, s2) -> s1));
        //获取比赛的排行
        QueryWrapper<GameRank> gameRankQueryWrapper = new QueryWrapper<>();
        gameRankQueryWrapper.eq("userId", userId).eq("gameId", gameId);
        GameRank gameRank = gameRankMapper.selectOne(gameRankQueryWrapper);

        GameDetailUnit gameDetailUnit = getGameDetailUnit(nowSubmit, questionIdToFullScore);
        if (gameRank == null) {
            gameRank = new GameRank();
            gameRank.setUserId(userId);
            gameRank.setUserName(userName); // 使用从消息中获取的用户名
            gameRank.setGameId(gameId);

            GameDetail gameDetail = new GameDetail();
            gameDetail.setGameId(gameId);
            gameDetail.setUserId(userId);
            Map<Long, GameDetailUnit> gameDetailUnitMap = new HashMap<>();
            gameDetailUnitMap.put(nowSubmit.getQuestionId(), gameDetailUnit);
            gameDetail.setSubmitDetail(gameDetailUnitMap);

            gameRank.setGameDetail(JSONUtil.toJsonStr(gameDetail));
            gameRank.setTotalScore(gameDetailUnit.getScore());
            gameRank.setTotalMemory(gameDetailUnit.getMemoryCost());
            gameRank.setTotalTime(gameDetailUnit.getTimeCost());
            redisTemplate.opsForZSet().add("game:gameId:" + gameId, gameRank.getUserName().toString(), gameRank.getTotalScore());
            redisTemplate.opsForHash().put("game_user_detail:" + ":" + gameRank.getGameId() + ":", gameRank.getUserName(), gameDetailUnit.toString());
        } else {
            GameDetail dbGameDetail;
            if (StringUtils.isBlank(gameRank.getGameDetail())) {
                dbGameDetail = new GameDetail();
                dbGameDetail.setGameId(gameId);
                dbGameDetail.setUserId(userId);
                dbGameDetail.setSubmitDetail(new HashMap<>()); // 初始化
            } else {
                dbGameDetail = JSONUtil.toBean(gameRank.getGameDetail(), GameDetail.class);
                if (dbGameDetail.getSubmitDetail() == null) { // 防御空指针
                    dbGameDetail.setSubmitDetail(new HashMap<>());
                }
            }
            Map<Long, GameDetailUnit> dbSubmitDetail = dbGameDetail.getSubmitDetail();
            GameDetailUnit dbExistingUnitForQuestion = dbSubmitDetail.get(nowSubmit.getQuestionId());

            boolean updateRank = false;
            int scoreDelta = 0;
            int memoryDelta = 0;
            int timeDelta = 0;

            if (dbExistingUnitForQuestion == null) {
                dbSubmitDetail.put(nowSubmit.getQuestionId(), gameDetailUnit);
                scoreDelta = gameDetailUnit.getScore();
                memoryDelta = gameDetailUnit.getMemoryCost(); // 首次提交，累加资源消耗
                timeDelta = gameDetailUnit.getTimeCost();
                updateRank = true;
            } else {
                if (gameDetailUnit.isBetter(dbExistingUnitForQuestion)) {
                    dbSubmitDetail.put(nowSubmit.getQuestionId(), gameDetailUnit);
                    scoreDelta = gameDetailUnit.getScore() - dbExistingUnitForQuestion.getScore();
                    // 对于时间和内存，ACM通常记录的是“所有通过题目中，单题最高消耗”或“总和”，这里是累加总和，如果逻辑是取最优，则需要调整
                    // 当前逻辑是累加总分，替换掉旧题目的分，并累加新的时间和内存，减去旧的时间和内存
                    memoryDelta = gameDetailUnit.getMemoryCost() - dbExistingUnitForQuestion.getMemoryCost();
                    timeDelta = gameDetailUnit.getTimeCost() - dbExistingUnitForQuestion.getTimeCost();
                    updateRank = true;
                }
            }
            if (updateRank) {
                dbGameDetail.setSubmitDetail(dbSubmitDetail);
                gameRank.setGameDetail(JSONUtil.toJsonStr(dbGameDetail));
                gameRank.setTotalScore((gameRank.getTotalScore() == null ? 0 : gameRank.getTotalScore()) + scoreDelta);
                gameRank.setTotalMemory((gameRank.getTotalMemory() == null ? 0 : gameRank.getTotalMemory()) + memoryDelta);
                gameRank.setTotalTime((gameRank.getTotalTime() == null ? 0 : gameRank.getTotalTime()) + timeDelta);
                redisTemplate.opsForZSet().add("game:gameId:" + gameId, gameRank.getUserName().toString(), gameRank.getTotalScore());
            }
        }
        try {
            // 手动确认消息的接收，向RabbitMQ发送确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }

    public GameDetailUnit getGameDetailUnit(QuestionSubmit nowSubmit, Map<Long, Integer> questionIdToFullScore) {
        GameDetailUnit gameDetailUnit = new GameDetailUnit();
        gameDetailUnit.setId(nowSubmit.getQuestionId());
        Question question = questionService.getById(nowSubmit.getQuestionId());
        gameDetailUnit.setName(question != null ? question.getTitle() : "未知题目");

        String judgeInfoStr = nowSubmit.getJudgeInfo();

        if (StringUtils.isBlank(judgeInfoStr) || "{}".equals(judgeInfoStr)) {
            log.warn("排行榜更新：submissionId {} 的 JudgeInfo 为空或无效。可能上游判题未正确记录。", nowSubmit.getId());
            gameDetailUnit.setTimeCost(0);
            gameDetailUnit.setMemoryCost(0);
            gameDetailUnit.setScore(0);
            return gameDetailUnit;
        }

        JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class); // 确保 JudgeInfo 类正确
        gameDetailUnit.setTimeCost(judgeInfo.getTime() != null ? Math.toIntExact(judgeInfo.getTime()) : 0);
        gameDetailUnit.setMemoryCost(judgeInfo.getMemory() != null ? Math.toIntExact(judgeInfo.getMemory()) : 0);

        String judgeInfoMessage = judgeInfo.getMessage();
        if (JudgeInfoMessageEnum.ACCEPTED.getValue().equals(judgeInfoMessage)) {
            gameDetailUnit.setScore(questionIdToFullScore.getOrDefault(nowSubmit.getQuestionId(), 0));
        } else {
            gameDetailUnit.setScore(0);
        }
        return gameDetailUnit;
    }
}
