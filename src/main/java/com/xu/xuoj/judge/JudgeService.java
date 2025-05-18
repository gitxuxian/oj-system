package com.xu.xuoj.judge;

import com.xu.xuoj.model.entity.QuestionSubmit;

public interface JudgeService {
    QuestionSubmit doJudge(long questionSubmitId);
}
