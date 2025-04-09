package com.yupi.xuoj.judge;

import com.yupi.xuoj.model.entity.QuestionSubmit;

public interface JudgeService {
    QuestionSubmit doJudge(long questionSubmitId);
}
