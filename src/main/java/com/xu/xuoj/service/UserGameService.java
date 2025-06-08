package com.xu.xuoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.xuoj.model.entity.UserGame;

import java.util.List;

/**
* @author hy
* @description 针对表【user_game】的数据库操作Service
* @createDate 2025-05-17 14:56:44
*/
public interface UserGameService extends IService<UserGame> {

    /**
     * 用户参加比赛
     * @param userId 用户ID
     * @param gameId 比赛ID
     * @return 是否成功
     */
    boolean joinGame(Long userId, Long gameId);

    /**
     * 获取用户比赛记录
     * @param userId 用户ID
     * @param gameId 比赛ID
     * @return 用户比赛记录
     */
    UserGame getUserGameRecord(Long userId, Long gameId);

    /**
     * 更新比赛状态
     * @param userId 用户ID
     * @param gameId 比赛ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateGameStatus(Long userId, Long gameId, Integer status);

    /**
     * 获取用户参加的所有比赛
     * @param userId 用户ID
     * @return 比赛列表
     */
    List<UserGame> getUserGames(Long userId);

    /**
     * 获取比赛的所有参赛者
     * @param gameId 比赛ID
     * @return 参赛者列表
     */
    List<UserGame> getGameParticipants(Long gameId);

    /**
     * 检查用户是否已参加比赛
     * @param userId 用户ID
     * @param gameId 比赛ID
     * @return 是否已参加
     */
    boolean hasJoinedGame(Long userId, Long gameId);

    /**
     * 退出比赛
     * @param userId 用户ID
     * @param gameId 比赛ID
     * @return 是否成功
     */
    boolean quitGame(Long userId, Long gameId);
}
