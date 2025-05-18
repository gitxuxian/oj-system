package com.xu.xuoj.judge.strategy;

import com.xu.xuoj.judge.codesandbox.model.SubmissionResult;
import com.xu.xuoj.model.dto.question.JudgeCase;
import com.xu.xuoj.judge.codesandbox.model.JudgeInfo;
import com.xu.xuoj.model.entity.Question;
import com.xu.xuoj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

@Data
public class JudgeContext {

    private List<JudgeInfo> judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

    private List<SubmissionResult> submissionResult;
}
