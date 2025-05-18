package com.xu.xuoj.judge.strategy;

import com.xu.xuoj.judge.codesandbox.model.JudgeInfo;

public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);
}
