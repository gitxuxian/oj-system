package com.xu.xuoj.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.xuoj.common.BaseResponse;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.common.ResultUtils;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xu.xuoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xu.xuoj.model.entity.QuestionSubmit;
import com.xu.xuoj.model.vo.QuestionSubmitVO;
import com.xu.xuoj.service.QuestionSubmitService;
import com.xu.xuoj.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;


@RestController
@RequestMapping("/question_submit")
@Slf4j
@Api(tags = "题目提交模块")
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/submit")
    @SaCheckLogin
    @ApiOperation("题目提交")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, StpUtil.getLoginIdAsLong());
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @ApiOperation("分页提交题目信息")
    @SaCheckLogin
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
            questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, StpUtil.getLoginIdAsLong()));
    }
}
