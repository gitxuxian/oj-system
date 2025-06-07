package com.xu.xuoj.judge;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.judge.codesandbox.CodeSandBox;
import com.xu.xuoj.judge.codesandbox.CodeSandBoxFactory;
import com.xu.xuoj.judge.codesandbox.impl.ThirdPartyCodeSandBox;
import com.xu.xuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xu.xuoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.xu.xuoj.judge.codesandbox.model.SubmissionResult;
import com.xu.xuoj.judge.rambbitmq.GameMessageProducer;
import com.xu.xuoj.judge.strategy.JudgeContext;
import com.xu.xuoj.model.dto.question.JudgeCase;
import com.xu.xuoj.judge.codesandbox.model.JudgeInfo;
import com.xu.xuoj.model.entity.GameSubmissionMessage;
import com.xu.xuoj.model.entity.Question;
import com.xu.xuoj.model.entity.QuestionSubmit;
import com.xu.xuoj.model.enums.JudgeInfoMessageEnum;
import com.xu.xuoj.model.enums.QuestionSubmitStatusEnum;
import com.xu.xuoj.service.QuestionService;
import com.xu.xuoj.service.QuestionSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Wrapper;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {


    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeMannger judgeMannger;

    @Resource
    private CodeSandBoxFactory codeSandBoxFactory;

    @Resource
    private ThirdPartyCodeSandBox thirdPartyCodeSandBox;

    @Resource
    private GameMessageProducer gameMessageProducer;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId, long gameId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交信息不存在");
        }
        Long id = questionSubmit.getQuestionId();
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.IN_QUEUE.getValue())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.PROCESSING.getValue());
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
        JudgeInfo judgeInfo = new JudgeInfo();
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
            .code(code)
            .language(language).inputList(inputList).build();
        if (question.isAcm()) {
            List<SubmissionResult> submissionResult = thirdPartyCodeSandBox.excuteCode(executeCodeRequest);
            JudgeContext judgeContext = new JudgeContext();
            judgeContext.setSubmissionResult(submissionResult);
            judgeContext.setJudgeCaseList(judgeCaseList);
            judgeContext.setQuestion(question);
            judgeContext.setQuestionSubmit(questionSubmit);
            judgeInfo = judgeMannger.doJudgeAcm(judgeContext);
        }
        if (!question.isAcm()) {
            ExecuteCodeResponse executeCodeResponse = codeSandBox.excuteCode(executeCodeRequest);
            List<String> ouputList = executeCodeResponse.getOutputList();
            // 5）根据沙箱的执行结果，设置题目的判题状态和信息
            JudgeContext judgeContext = new JudgeContext();
            judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
            judgeContext.setOutputList(ouputList);
            judgeContext.setJudgeCaseList(judgeCaseList);
            judgeContext.setQuestion(question);
            judgeContext.setQuestionSubmit(questionSubmit);
            //包含内存溢出，时间超时
            judgeInfo = judgeMannger.doJudge(judgeContext);
        }
        // 6）修改数据库中的判题结果
        if (JudgeInfoMessageEnum.ACCEPTED.getValue().trim().equals(judgeInfo.getMessage().trim())) {
            UpdateWrapper<Question> questionUpdateWrapper = new UpdateWrapper<>();
            questionUpdateWrapper.lambda().eq(Question::getId, question.getId());
            Integer acceptedNum = question.getAcceptedNum();
            questionUpdateWrapper.lambda().set(Question::getAcceptedNum, ++acceptedNum);
        }
        UpdateWrapper<QuestionSubmit> questionSubmitUpdateWrapper = new UpdateWrapper<>();
        questionSubmitUpdateWrapper.lambda()
            .eq(QuestionSubmit::getId, questionSubmitId); // WHERE condition: id = questionSubmitId
        // 使用显式的 .set() 方法来指定要更新的字段和值
        questionSubmitUpdateWrapper.lambda()
            .set(QuestionSubmit::getStatus, QuestionSubmitStatusEnum.ACCEPTED.getValue())
            .set(QuestionSubmit::getJudgeInfo, JSONUtil.toJsonStr(judgeInfo));
        boolean updated = questionSubmitService.update(questionSubmitUpdateWrapper); // 注意：这里传递的是wrapper
        if (!updated) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新提交记录失败或记录不存在");
        }
        if (gameId != 0) {
            GameSubmissionMessage rankUpdateMessage = new GameSubmissionMessage(
                questionSubmitId,
                gameId,
                StpUtil.getLoginIdAsLong(),
                StpUtil.getTokenName()
            );
            gameMessageProducer.sendMessage("game_routingKey", "game_exchange", rankUpdateMessage.toString());
        } else {
            log.error("判题服务：更新提交记录失败 for submissionId: {}", questionSubmit);
        }
        // 获取更新后的提交记录，应使用 questionSubmitId
        return questionSubmitService.getById(questionSubmitId);
    }
}
