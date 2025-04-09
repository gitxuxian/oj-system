package com.yupi.xuoj.judge.strategy;

import com.yupi.xuoj.model.dto.question.JudgeCase;
import com.yupi.xuoj.judge.codesandbox.model.JudgeInfo;
import com.yupi.xuoj.model.entity.Question;
import com.yupi.xuoj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}
