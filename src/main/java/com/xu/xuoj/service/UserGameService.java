package com.xu.xuoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.xuoj.model.entity.UserGame;

public interface UserGameService extends IService<UserGame> {
    /**
     * 用户报名参赛
     * @param userId 用户ID
     * @param gameId 竞赛ID
     * @return 是否成功
     */
    boolean joinGame(Long userId, Long gameId);

    /**
     * 获取用户参赛记录
     * @param userId 用户ID
     * @param gameId 竞赛ID
     * @return 参赛记录
     */
    UserGame getUserGameRecord(Long userId, Long gameId);

    /**
     * 更新参赛状态
     * @param userId 用户ID
     * @param gameId 竞赛ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateGameStatus(Long userId, Long gameId, Integer status);
}
