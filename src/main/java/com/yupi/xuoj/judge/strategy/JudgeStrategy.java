package com.yupi.xuoj.judge.strategy;

import com.yupi.xuoj.judge.codesandbox.model.JudgeInfo;

public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);
}
