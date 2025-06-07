package com.xu.xuoj.model.dto.game;

import com.xu.xuoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameQuestionAddRequest {
    /**
     * 竞赛ID
     */
    private Long gameId;

    /**
     * 题目提交
     */
    private QuestionSubmitAddRequest questionSubmitAddRequest;
}
