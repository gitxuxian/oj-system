package com.xu.xuoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.xuoj.model.dto.game.GameQuestionDTO;
import com.xu.xuoj.model.entity.GameQuestion;
import com.xu.xuoj.model.entity.Question;
import com.xu.xuoj.model.vo.QuestionVO;

import java.util.List;

public interface GameQuestionService extends IService<GameQuestion> {
    /**
     * 添加题目到竞赛
     *
     * @return 是否成功
     */
    boolean addQuestionToGame(GameQuestionDTO gameQuestionDTO);

    /**
     * 从竞赛移除题目
     *
     * @param gameId     竞赛ID
     * @param questionId 题目ID
     * @return 是否成功
     */
    boolean removeQuestionFromGame(Long gameId, Long questionId);

    /**
     * 获取竞赛题目列表
     *
     * @param gameId 竞赛ID
     * @return 题目列表
     */
    List<QuestionVO> getGameQuestions(Long gameId);
}
