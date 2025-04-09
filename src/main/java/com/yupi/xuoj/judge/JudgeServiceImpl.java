package com.yupi.xuoj.judge;

import cn.hutool.json.JSONUtil;
import com.yupi.xuoj.common.ErrorCode;
import com.yupi.xuoj.exception.BusinessException;
import com.yupi.xuoj.judge.codesandbox.CodeSandBox;
import com.yupi.xuoj.judge.codesandbox.CodeSandBoxFactory;
import com.yupi.xuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.xuoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.yupi.xuoj.judge.strategy.JudgeContext;
import com.yupi.xuoj.model.dto.question.JudgeCase;
import com.yupi.xuoj.judge.codesandbox.model.JudgeInfo;
import com.yupi.xuoj.model.entity.Question;
import com.yupi.xuoj.model.entity.QuestionSubmit;
import com.yupi.xuoj.model.enums.JudgeInfoMessageEnum;
import com.yupi.xuoj.model.enums.QuestionSubmitStatusEnum;
import com.yupi.xuoj.service.QuestionService;
import com.yupi.xuoj.service.QuestionSubmitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {


    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeMannger judgeMannger;

    @Resource
    private CodeSandBoxFactory codeSandBoxFactory;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交信息不存在");
        }
        Long id = questionSubmit.getId();
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(JudgeInfoMessageEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目状态更新失败");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandBox codeSandBox = codeSandBoxFactory.getCodeSandBox();
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
            .code(code)
            .language(language).inputList(inputList).build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.excuteCode(executeCodeRequest);
        List<String> ouputList = executeCodeResponse.getOuputList();
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(ouputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeMannger.doJudge(judgeContext);
        // 6）修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setQuestionId(questionSubmitId);
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        boolean updated = questionSubmitService.updateById(questionSubmitUpdate);
        if (!updated) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新失败");
        }
        return questionSubmitService.getById(id);
    }
}
