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
}
