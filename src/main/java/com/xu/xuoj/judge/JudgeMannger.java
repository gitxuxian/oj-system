package com.xu.xuoj.judge;

import cn.hutool.json.JSONUtil;
import com.xu.xuoj.judge.codesandbox.model.Status;
import com.xu.xuoj.judge.codesandbox.model.SubmissionResult;
import com.xu.xuoj.judge.strategy.DefaultJudgeStratgy;
import com.xu.xuoj.judge.strategy.JavaLanguageJudgeStragy;
import com.xu.xuoj.judge.strategy.JudgeContext;
import com.xu.xuoj.judge.strategy.JudgeStrategy;
import com.xu.xuoj.judge.codesandbox.model.JudgeInfo;
import com.xu.xuoj.model.dto.question.JudgeCase;
import com.xu.xuoj.model.dto.question.JudgeConfig;
import com.xu.xuoj.model.entity.Question;
import com.xu.xuoj.model.entity.QuestionSubmit;
import com.xu.xuoj.model.enums.JudgeInfoMessageEnum;
import com.xu.xuoj.model.enums.QuestionSubmitLanguageEnum;
import com.xu.xuoj.model.enums.QuestionSubmitStatusEnum;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if (QuestionSubmitLanguageEnum.JAVA_OPENJDK_8.getValue().equals(language)) {
            judgeStratgy = new JavaLanguageJudgeStragy();
        }
        return judgeStratgy.doJudge(judgeContext);
    }

    JudgeInfo doJudgeAcm(JudgeContext judgeContext) {
        JudgeInfo finalJudgeInfo = new JudgeInfo();
        List<SubmissionResult> submissionResults = judgeContext.getSubmissionResult();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();
        JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);

        // 初始化默认值
        JudgeInfoMessageEnum finalStatusEnum = JudgeInfoMessageEnum.ACCEPTED; // 默认为通过
        long maxTimeAcrossCases = 0L;    // 所有测试用例中单个用例消耗的最大时间 (ms)
        long maxMemoryAcrossCases = 0L; // 所有测试用例中单个用例消耗的最大内存 (KB)
        String detailMessage = "";       // 用于存储特定的错误细节，例如第一个WA的测试点

        // 基本校验
        if (submissionResults == null || submissionResults.isEmpty() || judgeCaseList == null || submissionResults.size() != judgeCaseList.size()) {
            finalJudgeInfo.setMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
            finalJudgeInfo.setDetail("提交结果与测试用例数量不匹配，或代码沙箱未提供结果给ACM判题器。");
            // 确保设置默认的 time 和 memory，避免空指针
            finalJudgeInfo.setTime(0L);
            finalJudgeInfo.setMemory(0L);
            return finalJudgeInfo;
        }

        // 1. 全局检查：编译错误 (CE) 或其他严重的代码沙箱错误
        // Judge0 通常会将CE作为第一个（或所有）结果的状态返回（如果编译失败）。
        SubmissionResult firstResultForGlobalCheck = submissionResults.get(0);
        if (firstResultForGlobalCheck.getStatus() != null) {
            int globalStatusId = firstResultForGlobalCheck.getStatus().getId();
            String globalStatusDescription = firstResultForGlobalCheck.getStatus().getDescription();
            if (globalStatusId == QuestionSubmitStatusEnum.COMPILATION_ERROR.getValue()) { // Judge0: Compilation Error
                finalJudgeInfo.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
                finalJudgeInfo.setDetail(firstResultForGlobalCheck.getCompileOutput() != null ? firstResultForGlobalCheck.getCompileOutput() : firstResultForGlobalCheck.getStderr());
                finalJudgeInfo.setTime(0L);
                finalJudgeInfo.setMemory(0L);
                return finalJudgeInfo;
            }
            // 例如：13 = Internal Error, 14 = Exec Format Error
            if (globalStatusId == QuestionSubmitStatusEnum.INTERNAL_ERROR.getValue()) { // Judge0: Internal Error
                finalJudgeInfo.setMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
                finalJudgeInfo.setDetail("代码沙箱内部错误: " + (firstResultForGlobalCheck.getMessage() != null ? firstResultForGlobalCheck.getMessage() : globalStatusDescription));
                finalJudgeInfo.setTime(0L);
                finalJudgeInfo.setMemory(0L);
                return finalJudgeInfo;
            }
            if (globalStatusId == QuestionSubmitStatusEnum.EXEC_FORMAT_ERROR.getValue()) { // Judge0: Exec Format Error
                finalJudgeInfo.setMessage(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue()); // 或者一个更具体的 "执行环境错误"
                finalJudgeInfo.setDetail("执行格式错误: " + (firstResultForGlobalCheck.getMessage() != null ? firstResultForGlobalCheck.getMessage() : globalStatusDescription));
                finalJudgeInfo.setTime(0L);
                finalJudgeInfo.setMemory(0L);
                return finalJudgeInfo;
            }
        }


        // 2. 遍历每一个测试用例的结果，以确定最终状态 (ACM风格：全部运行，然后根据优先级确定结果)
        boolean encounteredError = false; // 标记是否已经遇到过比AC更差的结果

        for (int i = 0; i < submissionResults.size(); i++) {
            SubmissionResult currentRunResult = submissionResults.get(i);
            JudgeCase currentExpectedCase = judgeCaseList.get(i);
            Status currentStatus = currentRunResult.getStatus();
            // 更新当前测试用例消耗的最大时间和内存
            try {
                if (currentRunResult.getTime() != null) {
                    // Judge0 的时间是秒（浮点数字符串），转换为毫秒
                    long caseTimeMs = (long) (Double.parseDouble(currentRunResult.getTime()) * 1000);
                    if (caseTimeMs > maxTimeAcrossCases) {
                        maxTimeAcrossCases = caseTimeMs;
                    }
                }
                if (currentRunResult.getMemory() != null) { // Judge0 返回的是 KB
                    if (currentRunResult.getMemory() > maxMemoryAcrossCases) {
                        maxMemoryAcrossCases = currentRunResult.getMemory();
                    }
                }
            } catch (NumberFormatException e) {
                // 处理时间或内存格式错误，可以记录日志
                System.err.println("ACM Judger: Error parsing time/memory for case " + i + ": " + e.getMessage());
                // 可以选择将此视为一个系统错误
                if (!encounteredError || finalStatusEnum == JudgeInfoMessageEnum.ACCEPTED) {
                    finalStatusEnum = JudgeInfoMessageEnum.SYSTEM_ERROR;
                    detailMessage = "解析测试点 " + (i + 1) + " 的资源消耗时出错。";
                    encounteredError = true;
                }
            }
            // 如果当前最终状态仍然是ACCEPTED，或者遇到的错误没有当前这个严重，则更新状态
            // Judge0 状态ID: 3=Accepted, 4=WA, 5=TLE, 7-12=RE, 6=CE(已在前面处理), 13=InternalError(已处理)
            if (currentStatus != null) {
                int statusId = currentStatus.getId();
                String statusDescription = currentStatus.getDescription(); // Judge0返回的描述

                JudgeInfoMessageEnum currentCaseEnum = JudgeInfoMessageEnum.ACCEPTED; // 当前测试点的状态
                String currentCaseDetail = "";

                switch (statusId) {
                    case 3: // Accepted (by Judge0 for this test case)
                        // 需要进一步比较输出
                        if (currentExpectedCase.getOutput() == null && (currentRunResult.getStdout() == null || currentRunResult.getStdout().trim().isEmpty())) {
                            // 预期输出为空，实际输出也为空或只有空白，算作正确
                            currentCaseEnum = JudgeInfoMessageEnum.ACCEPTED;
                        } else if (currentExpectedCase.getOutput() != null && currentRunResult.getStdout() != null &&
                            currentExpectedCase.getOutput().trim().equals(currentRunResult.getStdout().trim())) {
                            currentCaseEnum = JudgeInfoMessageEnum.ACCEPTED;
                        } else {
                            currentCaseEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                            currentCaseDetail = "测试点 " + (i + 1) + "：" + JudgeInfoMessageEnum.WRONG_ANSWER.getText();
                        }
                        break;
                    case 4: // Wrong Answer (by Judge0)
                        currentCaseEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                        currentCaseDetail = "测试点 " + (i + 1) + "：" + (statusDescription != null ? statusDescription : JudgeInfoMessageEnum.WRONG_ANSWER.getText());
                        break;
                    case 5: // Time Limit Exceeded (by Judge0)
                        currentCaseEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
                        currentCaseDetail = "测试点 " + (i + 1) + "：" + (statusDescription != null ? statusDescription : JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getText());
                        break;
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        currentCaseEnum = JudgeInfoMessageEnum.RUNTIME_ERROR;
                        currentCaseDetail = "测试点 " + (i + 1) + "：" + (statusDescription != null ? statusDescription : JudgeInfoMessageEnum.RUNTIME_ERROR.getText());
                        if (currentRunResult.getStderr() != null && !currentRunResult.getStderr().isEmpty()) {
                            currentCaseDetail += " (" + currentRunResult.getStderr().trim() + ")";
                        }
                        break;
                    default:
                        // 如果Judge0为某个测试点返回了未明确处理的“失败”状态
                        if (statusId != 1 && statusId != 2) { // 不是 In Queue 或 Processing
                            currentCaseEnum = JudgeInfoMessageEnum.SYSTEM_ERROR; // 或者根据statusId映射到其他错误
                            currentCaseDetail = "测试点 " + (i + 1) + "：未知的沙箱执行状态 (" + statusDescription + ")";
                        }
                        break;
                }

                // ACM 错误优先级逻辑：RE > TLE > MLE > WA > AC
                // (CE 已在最前面处理)
                // 如果当前测试点的错误比目前记录的 `finalStatusEnum` 更严重，则更新
                if (currentCaseEnum != JudgeInfoMessageEnum.ACCEPTED) {
                    if (!encounteredError) { // 第一个非AC的测试点
                        finalStatusEnum = currentCaseEnum;
                        detailMessage = currentCaseDetail;
                        encounteredError = true;
                    } else {
                        // 如果已记录WA，新的是RE/TLE/MLE，则更新
                        if (finalStatusEnum == JudgeInfoMessageEnum.WRONG_ANSWER &&
                            (currentCaseEnum == JudgeInfoMessageEnum.RUNTIME_ERROR || currentCaseEnum == JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED || currentCaseEnum == JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED)) {
                            finalStatusEnum = currentCaseEnum;
                            detailMessage = currentCaseDetail; // 更新为更严重错误的详情
                        }
                        // 如果已记录MLE，新的是RE/TLE，则更新
                        else if (finalStatusEnum == JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED &&
                            (currentCaseEnum == JudgeInfoMessageEnum.RUNTIME_ERROR || currentCaseEnum == JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED)) {
                            finalStatusEnum = currentCaseEnum;
                            detailMessage = currentCaseDetail;
                        }
                        // 如果已记录TLE，新的是RE，则更新
                        else if (finalStatusEnum == JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED &&
                            currentCaseEnum == JudgeInfoMessageEnum.RUNTIME_ERROR) {
                            finalStatusEnum = currentCaseEnum;
                            detailMessage = currentCaseDetail;
                        }
                    }
                }
            } else {
                // 如果某个测试用例的 SubmissionResult.Status 为 null，这可能是一个系统问题
                if (!encounteredError || finalStatusEnum == JudgeInfoMessageEnum.ACCEPTED) {
                    finalStatusEnum = JudgeInfoMessageEnum.SYSTEM_ERROR;
                    detailMessage = "测试点 " + (i + 1) + " 的沙箱返回结果中缺少状态信息。";
                    encounteredError = true;
                }
            }
        } // 遍历所有测试用例结束

        // 3. 如果所有测试用例都通过了初步检查 (即 finalStatusEnum 仍然是 ACCEPTED)
        if (finalStatusEnum == JudgeInfoMessageEnum.ACCEPTED) {
            if (judgeConfig.getMemoryLimit() != null && maxMemoryAcrossCases > judgeConfig.getMemoryLimit()) {
                finalStatusEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
                detailMessage = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getText();
            }
            if (finalStatusEnum == JudgeInfoMessageEnum.ACCEPTED && // 只有在前面没超内存的情况下才检查时间
                judgeConfig.getTimeLimit() != null && maxTimeAcrossCases > judgeConfig.getTimeLimit()) {
                finalStatusEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
                detailMessage = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getText();
            }
        }
        // 4. 设置最终的判题结果
        finalJudgeInfo.setMessage(finalStatusEnum.getValue()); // 使用枚举的 value 获取对应的中文/英文描述
        finalJudgeInfo.setDetail(detailMessage);
        finalJudgeInfo.setTime(maxTimeAcrossCases);
        finalJudgeInfo.setMemory(maxMemoryAcrossCases);
        return finalJudgeInfo;
    }

}
