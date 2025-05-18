package com.xu.xuoj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.xu.xuoj.model.dto.question.JudgeCase;
import com.xu.xuoj.model.dto.question.JudgeConfig;
import com.xu.xuoj.judge.codesandbox.model.JudgeInfo;
import com.xu.xuoj.model.entity.Question;
import com.xu.xuoj.model.enums.JudgeInfoMessageEnum;

import java.util.List;

/**
 * 默认判题策略
 */
public class DefaultJudgeStratgy implements JudgeStrategy {


    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED; // 默认为“通过”

        List<String> actualOutputList = judgeContext.getOutputList(); // 代码沙箱的实际输出列表
        List<JudgeCase> expectedJudgeCases = judgeContext.getJudgeCaseList(); // 题目定义的预期测试用例列表
        // 假设 judgeContext.getJudgeInfo() 返回的是一个列表，
        // 其中每个元素包含了对应测试用例的执行信息（如内存和时间）。
        // 原代码中类型为 List<JudgeInfo>，这里沿用此类型名，
        // 假设 JudgeInfo 类有 getMemory() 和 getTime() 方法。
        List<JudgeInfo> executionDetailsPerCase = judgeContext.getJudgeInfo();

        // 根据您的说明，actualOutputList, expectedJudgeCases, 和 executionDetailsPerCase 的长度应该是一致的。
        // 因此，我们直接以 expectedJudgeCases.size() 作为循环边界。

        long overallMaxMemory = 0L; // 记录所有已通过测试用例中的最大内存消耗
        long overallMaxTime = 0L;   // 记录所有已通过测试用例中的最大时间消耗

        // 1. 逐个比对测试用例的输出，并记录资源消耗峰值
        for (int i = 0; i < expectedJudgeCases.size(); i++) {
            JudgeCase currentExpectedCase = expectedJudgeCases.get(i);
            String actualOutput = actualOutputList.get(i);

            // 获取当前测试用例的执行详情
            JudgeInfo currentCaseExecutionDetails = executionDetailsPerCase.get(i);
            long currentCaseMemory = currentCaseExecutionDetails.getMemory();
            long currentCaseTime = currentCaseExecutionDetails.getTime();

            // 更新观察到的最大内存和时间
            overallMaxMemory = Math.max(overallMaxMemory, currentCaseMemory);
            overallMaxTime = Math.max(overallMaxTime, currentCaseTime);

            // 比对输出 (使用 trim() 处理前后空格，使比对更鲁棒)
            if (!currentExpectedCase.getOutput().trim().equals(actualOutput.trim())) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                // 报告到目前为止的最大资源消耗
                judgeInfoResponse.setMemory(overallMaxMemory);
                judgeInfoResponse.setTime(overallMaxTime);
                return judgeInfoResponse; // 一旦答案错误，立即返回
            }
        }

        // 2. 如果所有输出都正确，再检查整体资源限制
        // 首先设置已累积的最大资源消耗
        judgeInfoResponse.setMemory(overallMaxMemory);
        judgeInfoResponse.setTime(overallMaxTime);

        Question question = judgeContext.getQuestion();
        JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class); // 使用Hutool JSONUtil
        Long memoryLimit = judgeConfig.getMemoryLimit(); // 假设单位为 Byte
        Long timeLimit = judgeConfig.getTimeLimit();     // 假设单位为 ms

        if (overallMaxMemory > memoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            // Memory 和 Time 已经设置为 overallMax
            return judgeInfoResponse;
        }

        if (overallMaxTime > timeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            // Memory 和 Time 已经设置为 overallMax
            return judgeInfoResponse;
        }

        // 3. 如果所有检查都通过
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue()); // 此时应为 ACCEPTED
        return judgeInfoResponse;
    }
}
