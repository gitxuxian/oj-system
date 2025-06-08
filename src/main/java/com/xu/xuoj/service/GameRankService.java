package com.xu.xuoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.xuoj.model.entity.GameRank;
import com.xu.xuoj.model.vo.GameRankDetail;

import java.util.List;

/**
* @author hy
* @description 针对表【game_rank】的数据库操作Service
* @createDate 2025-05-17 14:56:44
*/
public interface GameRankService extends IService<GameRank> {
    
    /**
     * 更新用户在比赛中的排名信息
     * @param gameId 比赛ID
     * @param userId 用户ID
     * @param score 得分
     * @param time 耗时
     * @param memory 内存使用
     * @param gameDetail 比赛详情
     * @return 是否更新成功
     */
    boolean updateUserRank(Long gameId, Long userId, Integer score, Integer time, Integer memory, String gameDetail);
    
    /**
     * 获取比赛排行榜
     * @param gameId 比赛ID
     * @param limit 限制数量
     * @return 排行榜列表
     */
    List<GameRank> getGameRankList(Long gameId, Integer limit);
    
    /**
     * 获取用户在比赛中的排名
     * @param gameId 比赛ID
     * @param userId 用户ID
     * @return 用户排名信息
     */
    GameRank getUserRankInGame(Long gameId, Long userId);
    
    /**
     * 计算并更新排行榜
     * @param gameId 比赛ID
     * @return 是否计算成功
     */
    boolean calculateRankings(Long gameId);
    
    /**
     * 获取比赛排行榜详情（带题目详情）
     * @param gameId 比赛ID
     * @return 排行榜详情列表
     */
    List<GameRankDetail> getRankDetailByGameId(Long gameId);
    
    /**
     * 更新用户在比赛中的最优答题情况
     * @param gameId 比赛ID
     * @param userId 用户ID
     * @param questionId 题目ID
     * @param score 得分
     * @param time 耗时
     * @param memory 内存使用
     * @param judgeMessage 判题信息
     * @return 是否更新成功
     */
    boolean updateOptimalSubmission(Long gameId, Long userId, Long questionId, Integer score, Integer time, Integer memory, String judgeMessage);
}
