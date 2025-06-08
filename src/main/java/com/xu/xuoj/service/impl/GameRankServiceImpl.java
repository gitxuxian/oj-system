package com.xu.xuoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.xuoj.mapper.GameRankMapper;
import com.xu.xuoj.model.entity.*;
import com.xu.xuoj.model.vo.GameRankDetail;
import com.xu.xuoj.service.*;
import cn.hutool.json.JSONUtil;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.model.enums.JudgeInfoMessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author hy
* @description 针对表【game_rank】的数据库操作Service实现
* @createDate 2025-05-17 14:56:44
*/
@Service
@Slf4j
public class GameRankServiceImpl extends ServiceImpl<GameRankMapper, GameRank>
    implements GameRankService{

    @Resource
    private UserService userService;
    
    @Resource
    private GameQuestionService gameQuestionService;
    
    @Resource
    private QuestionService questionService;

    @Override
    public boolean updateUserRank(Long gameId, Long userId, Integer score, Integer time, Integer memory, String gameDetail) {
        // 获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            return false;
        }
        
        // 查询是否已存在排名记录
        QueryWrapper<GameRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gameId", gameId).eq("userId", userId);
        GameRank existingRank = this.getOne(queryWrapper);
        
        if (existingRank != null) {
            // 更新现有记录
            existingRank.setTotalScore(existingRank.getTotalScore() + score);
            existingRank.setTotalTime(existingRank.getTotalTime() + time);
            existingRank.setTotalMemory(existingRank.getTotalMemory() + memory);
            existingRank.setGameDetail(gameDetail);
            existingRank.setUpdateTime(new Date());
            return this.updateById(existingRank);
        } else {
            // 创建新记录
            GameRank newRank = new GameRank();
            newRank.setGameId(gameId);
            newRank.setUserId(userId);
            newRank.setUserName(user.getUserName());
            newRank.setTotalScore(score);
            newRank.setTotalTime(time);
            newRank.setTotalMemory(memory);
            newRank.setGameDetail(gameDetail);
            newRank.setCreateTime(new Date());
            newRank.setUpdateTime(new Date());
            newRank.setIsDelete(0);
            return this.save(newRank);
        }
    }

    @Override
    public List<GameRank> getGameRankList(Long gameId, Integer limit) {
        QueryWrapper<GameRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gameId", gameId)
                   .eq("isDelete", 0)
                   .orderByDesc("totalScore")
                   .orderByAsc("totalTime")
                   .orderByAsc("totalMemory");
        
        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }
        
        return this.list(queryWrapper);
    }

    @Override
    public GameRank getUserRankInGame(Long gameId, Long userId) {
        QueryWrapper<GameRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gameId", gameId)
                   .eq("userId", userId)
                   .eq("isDelete", 0);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean calculateRankings(Long gameId) {
        try {
            // 获取所有该比赛的排名记录
            List<GameRank> ranks = getGameRankList(gameId, null);
            
            // 这里可以实现更复杂的排名计算逻辑
            // 比如根据得分、时间、内存等综合计算排名
            // 当前实现已经通过orderBy完成了基本排序
            
            return true;
        } catch (Exception e) {
            log.error("计算排行榜失败", e);
            return false;
        }
    }

    @Override
    public List<GameRankDetail> getRankDetailByGameId(Long gameId) {
        // 获得竞赛的题目信息
        QueryWrapper<GameQuestion> gameQuestionQueryWrapper = new QueryWrapper<>();
        gameQuestionQueryWrapper.eq("gameId", gameId).eq("isDelete", 0);
        List<GameQuestion> gameQuestions = gameQuestionService.list(gameQuestionQueryWrapper);
        List<Long> gameQuestionIds = gameQuestions.stream().map(GameQuestion::getQuestionId).collect(Collectors.toList());
        
        // 获得参加竞赛的所有用户排名记录
        QueryWrapper<GameRank> gameRankQueryWrapper = new QueryWrapper<>();
        gameRankQueryWrapper.eq("gameId", gameId).eq("isDelete", 0);
        List<GameRank> gameRanks = this.list(gameRankQueryWrapper);
        
        // 计算用户最优答题集合
        List<GameRankDetail> gameRankDetails = new ArrayList<>();
        
        for (GameRank gameRank : gameRanks) {
            GameRankDetail gameRankDetail = new GameRankDetail();
            gameRankDetail.setUserId(gameRank.getUserId());
            gameRankDetail.setUserName(gameRank.getUserName());
            gameRankDetail.setTotalScore(gameRank.getTotalScore());
            gameRankDetail.setTotalTime(gameRank.getTotalTime());
            gameRankDetail.setTotalMemory(gameRank.getTotalMemory());
            
            // 解析比赛详情
            List<GameDetailUnit> questionDetails = new ArrayList<>();
            if (gameRank.getGameDetail() != null && !gameRank.getGameDetail().isEmpty()) {
                try {
                    GameDetail gameDetail = JSONUtil.toBean(gameRank.getGameDetail(), GameDetail.class);
                    Map<Long, GameDetailUnit> submitDetail = gameDetail.getSubmitDetail();
                    
                    // 为每个题目创建详情单元
                    for (Long questionId : gameQuestionIds) {
                        GameDetailUnit unit = submitDetail.get(questionId);
                        if (unit == null) {
                            // 如果用户没有提交这道题，创建一个空的记录
                            Question question = questionService.getById(questionId);
                            unit = new GameDetailUnit();
                            unit.setId(questionId);
                            unit.setName(question != null ? question.getTitle() : "Unknown");
                            unit.setScore(0);
                            unit.setTimeCost(0);
                            unit.setMemoryCost(0);
                        }
                        questionDetails.add(unit);
                    }
                } catch (Exception e) {
                    log.error("解析比赛详情失败, gameId: {}, userId: {}", gameId, gameRank.getUserId(), e);
                }
            } else {
                // 如果没有详情记录，为每个题目创建空记录
                for (Long questionId : gameQuestionIds) {
                    Question question = questionService.getById(questionId);
                    GameDetailUnit unit = new GameDetailUnit();
                    unit.setId(questionId);
                    unit.setName(question != null ? question.getTitle() : "Unknown");
                    unit.setScore(0);
                    unit.setTimeCost(0);
                    unit.setMemoryCost(0);
                    questionDetails.add(unit);
                }
            }
            
            gameRankDetail.setQuestionDetails(questionDetails);
            gameRankDetails.add(gameRankDetail);
        }
        
        // 排序：总分降序 -> 总耗时升序 -> 总内存升序
        List<GameRankDetail> orderedGameRankDetails = gameRankDetails.stream()
                .sorted(Comparator.comparing(GameRankDetail::getTotalScore).reversed()
                        .thenComparing(GameRankDetail::getTotalTime)
                        .thenComparing(GameRankDetail::getTotalMemory))
                .collect(Collectors.toList());
        
        // 安排名次
        for (int i = 0; i < orderedGameRankDetails.size(); i++) {
            orderedGameRankDetails.get(i).setRankOrder(i + 1);
        }
        
        return orderedGameRankDetails;
    }

    @Override
    public boolean updateOptimalSubmission(Long gameId, Long userId, Long questionId, Integer score, Integer time, Integer memory, String judgeMessage) {
        try {
            // 获取用户信息
            User user = userService.getById(userId);
            if (user == null) {
                log.error("用户不存在, userId: {}", userId);
                return false;
            }
            
            // 获取题目信息
            Question question = questionService.getById(questionId);
            if (question == null) {
                log.error("题目不存在, questionId: {}", questionId);
                return false;
            }
            
            // 计算得分（根据判题结果）
            Integer finalScore = 0;
            if (JudgeInfoMessageEnum.ACCEPTED.getValue().equals(judgeMessage)) {
                finalScore = score; // 如果AC则使用传入的分数
            }
            
            // 查询是否已存在排名记录
            QueryWrapper<GameRank> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("gameId", gameId).eq("userId", userId).eq("isDelete", 0);
            GameRank existingRank = this.getOne(queryWrapper);
            
            if (existingRank == null) {
                // 创建新的排名记录
                existingRank = new GameRank();
                existingRank.setGameId(gameId);
                existingRank.setUserId(userId);
                existingRank.setUserName(user.getUserName());
                existingRank.setTotalScore(finalScore);
                existingRank.setTotalTime(time);
                existingRank.setTotalMemory(memory);
                
                // 创建游戏详情
                GameDetail gameDetail = new GameDetail();
                gameDetail.setGameId(gameId);
                gameDetail.setUserId(userId);
                Map<Long, GameDetailUnit> submitDetail = new HashMap<>();
                
                GameDetailUnit unit = new GameDetailUnit();
                unit.setId(questionId);
                unit.setName(question.getTitle());
                unit.setScore(finalScore);
                unit.setTimeCost(time);
                unit.setMemoryCost(memory);
                
                submitDetail.put(questionId, unit);
                gameDetail.setSubmitDetail(submitDetail);
                existingRank.setGameDetail(JSONUtil.toJsonStr(gameDetail));
                existingRank.setCreateTime(new Date());
                existingRank.setUpdateTime(new Date());
                existingRank.setIsDelete(0);
                
                return this.save(existingRank);
            } else {
                // 更新现有记录
                GameDetail gameDetail;
                if (existingRank.getGameDetail() == null || existingRank.getGameDetail().isEmpty()) {
                    gameDetail = new GameDetail();
                    gameDetail.setGameId(gameId);
                    gameDetail.setUserId(userId);
                    gameDetail.setSubmitDetail(new HashMap<>());
                } else {
                    gameDetail = JSONUtil.toBean(existingRank.getGameDetail(), GameDetail.class);
                    if (gameDetail.getSubmitDetail() == null) {
                        gameDetail.setSubmitDetail(new HashMap<>());
                    }
                }
                
                Map<Long, GameDetailUnit> submitDetail = gameDetail.getSubmitDetail();
                GameDetailUnit currentUnit = submitDetail.get(questionId);
                
                // 创建新的提交单元
                GameDetailUnit newUnit = new GameDetailUnit();
                newUnit.setId(questionId);
                newUnit.setName(question.getTitle());
                newUnit.setScore(finalScore);
                newUnit.setTimeCost(time);
                newUnit.setMemoryCost(memory);
                
                if (currentUnit == null || newUnit.isBetter(currentUnit)) {
                    // 如果是第一次提交或者新结果更好，则更新
                    if (currentUnit != null) {
                        // 减去旧的统计数据
                        existingRank.setTotalScore(existingRank.getTotalScore() - currentUnit.getScore());
                        existingRank.setTotalTime(existingRank.getTotalTime() - currentUnit.getTimeCost());
                        existingRank.setTotalMemory(existingRank.getTotalMemory() - currentUnit.getMemoryCost());
                    }
                    
                    // 添加新的统计数据
                    existingRank.setTotalScore(existingRank.getTotalScore() + newUnit.getScore());
                    existingRank.setTotalTime(existingRank.getTotalTime() + newUnit.getTimeCost());
                    existingRank.setTotalMemory(existingRank.getTotalMemory() + newUnit.getMemoryCost());
                    
                    // 更新详情
                    submitDetail.put(questionId, newUnit);
                    gameDetail.setSubmitDetail(submitDetail);
                    existingRank.setGameDetail(JSONUtil.toJsonStr(gameDetail));
                    existingRank.setUpdateTime(new Date());
                    
                    return this.updateById(existingRank);
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("更新最优提交记录失败, gameId: {}, userId: {}, questionId: {}", gameId, userId, questionId, e);
            return false;
        }
    }
}




