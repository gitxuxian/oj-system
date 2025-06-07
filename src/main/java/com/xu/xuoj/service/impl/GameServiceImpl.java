package com.xu.xuoj.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.judge.codesandbox.model.JudgeInfo;
import com.xu.xuoj.judge.rambbitmq.GameMessageProducer;
import com.xu.xuoj.mapper.GameMapper;

import com.xu.xuoj.mapper.GameQuestionMapper;
import com.xu.xuoj.mapper.GameRankMapper;
import com.xu.xuoj.model.dto.game.GameQueryDTO;
import com.xu.xuoj.model.dto.game.GameQuestionAddRequest;
import com.xu.xuoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xu.xuoj.model.entity.*;
import com.xu.xuoj.model.enums.JudgeInfoMessageEnum;
import com.xu.xuoj.service.GameService;

import com.xu.xuoj.service.QuestionSubmitService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hy
 * @description 针对表【game(比赛信息表)】的数据库操作Service实现
 * @createDate 2025-05-17 14:51:46
 */
@Service
public class GameServiceImpl extends ServiceImpl<GameMapper, Game>
    implements GameService {

    @Resource
    private final GameMapper gameMapper;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private GameMessageProducer gameMessageProducer;

    public GameServiceImpl(GameMapper gameMapper) {
        this.gameMapper = gameMapper;
    }

    @Override
    public List<Game> listGames(GameQueryDTO gameQueryDTO) {
        QueryWrapper<Game> queryWrapper = new QueryWrapper<>();
        if (gameQueryDTO == null) {
            return this.list(queryWrapper);
        }
        // 处理查询条件
        if (StringUtils.isNotBlank(gameQueryDTO.getGameName())) {
            queryWrapper.like("game_name", gameQueryDTO.getGameName());
        }
        if (gameQueryDTO.getType() != null) {
            queryWrapper.eq("type", gameQueryDTO.getType());
        }
        if (gameQueryDTO.getStatus() != null) {
            queryWrapper.eq("status", gameQueryDTO.getStatus());
        }
        if (gameQueryDTO.getGameDateStart() != null && gameQueryDTO.getGameDateEnd() != null) {
            queryWrapper.between("game_date", gameQueryDTO.getGameDateStart(), gameQueryDTO.getGameDateEnd());
        }

        // 处理分页
        if (gameQueryDTO.getPageSize() != 0 && gameQueryDTO.getCurrent() != 0) {
            Page<Game> page = new Page<>(gameQueryDTO.getCurrent(), gameQueryDTO.getPageSize());
            return this.page(page, queryWrapper).getRecords();
        }
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long questionSubmit(GameQuestionAddRequest gameQuestionSubmitRequest) {
        Long gameId = gameQuestionSubmitRequest.getGameId();
        QuestionSubmitAddRequest questionSubmitAddRequest = gameQuestionSubmitRequest.getQuestionSubmitAddRequest();

        if (gameId == null || questionSubmitAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Game game = gameMapper.selectById(gameId);
        if (game == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "竞赛不存在");
        }

        Date currentDate = new Date();
        Date gameStartDate = game.getGameDate();
        Integer durationMinutes = game.getDurationMinutes(); // 确保 Game 实体有这个字段

        if (durationMinutes == null || durationMinutes <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "竞赛时长配置错误");
        }

        if (gameStartDate.after(currentDate)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "竞赛未开始");
        }

        Date gameEndDate = new Date(gameStartDate.getTime() + durationMinutes * 60 * 1000L);
        if (currentDate.after(gameEndDate)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "竞赛已结束");
        }

        Long currentUserId = StpUtil.getLoginIdAsLong();
        String currentUserName = StpUtil.getTokenName();

        if (currentUserName == null || currentUserName.isEmpty()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "无法获取当前用户信息");
        }
        Long submitId = questionSubmitService.initiateSubmissionAndSendToJudgeQueue(
            questionSubmitAddRequest,
            currentUserId,
            gameId
        );
        return submitId;
    }

}

