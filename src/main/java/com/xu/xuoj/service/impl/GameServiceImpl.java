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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GameServiceImpl extends ServiceImpl<GameMapper, Game>
    implements GameService {

    @Resource
    private  GameMapper gameMapper;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private GameMessageProducer gameMessageProducer;


    @Override
    public List<Game> listGames(GameQueryDTO gameQueryDTO) {
        QueryWrapper<Game> queryWrapper = new QueryWrapper<>();
        if (gameQueryDTO == null) {
            return this.list(queryWrapper);
        }
        // 处理查询条件
        if (StringUtils.isNotBlank(gameQueryDTO.getGameName())) {
            queryWrapper.like("gameName", gameQueryDTO.getGameName());
        }
        if (gameQueryDTO.getType() != null) {
            queryWrapper.eq("type", gameQueryDTO.getType());
        }
        if (gameQueryDTO.getStatus() != null) {
            queryWrapper.eq("status", gameQueryDTO.getStatus());
        }

        try {
            // 处理分页
            if (gameQueryDTO.getPageSize() != 0 && gameQueryDTO.getCurrent() != 0) {
                Page<Game> page = new Page<>(gameQueryDTO.getCurrent(), gameQueryDTO.getPageSize());
                return this.page(page, queryWrapper).getRecords();
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
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

    @Override
    public boolean canViewRankings(Long gameId) {
        Game game = this.getById(gameId);
        if (game == null) {
            return false;
        }
        
        Date currentDate = new Date();
        Date gameStartDate = game.getGameDate();
        
        // 只有比赛开始后才能查看排行榜
        return !gameStartDate.after(currentDate);
    }

    @Override
    public boolean isGameRunning(Long gameId) {
        Game game = this.getById(gameId);
        if (game == null) {
            return false;
        }
        
        Date currentDate = new Date();
        Date gameStartDate = game.getGameDate();
        Integer durationMinutes = game.getDurationMinutes();
        
        if (durationMinutes == null || durationMinutes <= 0) {
            return false;
        }
        
        Date gameEndDate = new Date(gameStartDate.getTime() + durationMinutes * 60 * 1000L);
        
        // 比赛进行中：当前时间在开始时间之后且在结束时间之前
        return !gameStartDate.after(currentDate) && currentDate.before(gameEndDate);
    }

    @Override
    public void updateGameStatus(Long gameId) {
        Game game = this.getById(gameId);
        if (game == null) {
            return;
        }
        
        Date currentDate = new Date();
        Date gameStartDate = game.getGameDate();
        Integer durationMinutes = game.getDurationMinutes();
        
        if (durationMinutes == null || durationMinutes <= 0) {
            return;
        }
        
        Date gameEndDate = new Date(gameStartDate.getTime() + durationMinutes * 60 * 1000L);
        
        Integer newStatus;
        if (gameStartDate.after(currentDate)) {
            // 未开始
            newStatus = 0;
        } else if (currentDate.before(gameEndDate)) {
            // 进行中
            newStatus = 1;
        } else {
            // 已结束
            newStatus = 2;
        }
        
        // 如果状态发生变化，则更新
        if (!newStatus.equals(game.getStatus())) {
            game.setStatus(newStatus);
            game.setUpdateTime(currentDate);
            this.updateById(game);
            log.info("比赛状态已更新, gameId: {}, newStatus: {}", gameId, newStatus);
        }
    }

}

