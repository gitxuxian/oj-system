package com.xu.xuoj.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.xuoj.model.dto.game.GameQueryDTO;
import com.xu.xuoj.model.dto.game.GameQuestionAddRequest;
import com.xu.xuoj.model.entity.Game;

import java.util.List;

/**
 * @author hy
 * @description 针对表【game(比赛信息表)】的数据库操作Service
 * @createDate 2025-05-17 14:51:46
 */
public interface GameService extends IService<Game> {
    List<Game> listGames(GameQueryDTO gameQueryDTO);

    Long questionSubmit(GameQuestionAddRequest gameQuestionSubmitRequest);
    
    /**
     * 检查比赛状态
     * @param gameId 比赛ID
     * @return 是否可以查看排行榜
     */
    boolean canViewRankings(Long gameId);
    
    /**
     * 检查比赛是否进行中
     * @param gameId 比赛ID
     * @return 是否进行中
     */
    boolean isGameRunning(Long gameId);
    
    /**
     * 更新比赛状态
     * @param gameId 比赛ID
     */
    void updateGameStatus(Long gameId);
}
