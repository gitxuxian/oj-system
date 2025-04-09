package com.yupi.xuoj.judge;

import com.yupi.xuoj.judge.strategy.DefaultJudgeStratgy;
import com.yupi.xuoj.judge.strategy.JavaLanguageJudgeStragy;
import com.yupi.xuoj.judge.strategy.JudgeContext;
import com.yupi.xuoj.judge.strategy.JudgeStrategy;
import com.yupi.xuoj.judge.codesandbox.model.JudgeInfo;
import com.yupi.xuoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

@Service
public class JudgeMannger {

    /**
     * 根据提供的JudgeContext执行判断，并返回判断结果。
     *
     * @param judgeContext 判断上下文，包含题目提交信息和相关配置等
     * @return 判断结果信息
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStratgy = new DefaultJudgeStratgy();
        if ("java".equals(language)) {
            judgeStratgy = new JavaLanguageJudgeStragy();
        }
        return judgeStratgy.doJudge(judgeContext);
    }
}
