package com.xu.xuoj.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.xuoj.common.BaseResponse;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.common.ResultUtils;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.model.dto.apply.CreatorApplyJudgeRequest;
import com.xu.xuoj.model.dto.apply.CreatorApplyQueryRequest;
import com.xu.xuoj.model.dto.apply.CreatorApplyRequest;
import com.xu.xuoj.model.entity.CreatorApplication;
import com.xu.xuoj.service.CreatorApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/creator")
@Api(tags = "申请者模块")
@Slf4j
public class CreatorApplicationController {

    @Resource
    private CreatorApplicationService creatorApplicationService;

    /**
     * 提交出题权限申请
     */
    @PostMapping("/apply")
    @SaCheckLogin
    @ApiOperation("提交出题权限申请")
    public BaseResponse<Long> applyCreator(@RequestBody CreatorApplyRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = StpUtil.getLoginIdAsLong();
        long applicationId = creatorApplicationService.submitApplication(request, userId);
        return ResultUtils.success(applicationId);
    }

    /**
     * 获取当前用户申请状态
     */
    @GetMapping("/status")
    @SaCheckLogin
    @ApiOperation("获取当前用户申请状态")
    public BaseResponse<CreatorApplication> getApplicationStatus() {
        Long userId = StpUtil.getLoginIdAsLong();
        CreatorApplication application = creatorApplicationService.getUserApplication(userId);
        return ResultUtils.success(application);
    }

    /**
     * 分页获取申请列表 (管理员接口)
     */
    @PostMapping("/list/page")
    @SaCheckRole("admin")
    @ApiOperation("分页获取申请列表 (管理员接口)")
    public BaseResponse<Page<CreatorApplication>> listApplications(@RequestBody CreatorApplyQueryRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Page<CreatorApplication> page = creatorApplicationService.pageApplications(request);
        return ResultUtils.success(page);
    }

    /**
     * 审核申请 (管理员接口)
     */
    @PostMapping("/judge")
    @SaCheckRole("admin")
    @ApiOperation("审核申请 (管理员接口)")
    public BaseResponse<Boolean> judgeApplication(@RequestBody CreatorApplyJudgeRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long judgeUserId = StpUtil.getLoginIdAsLong();
        boolean result = creatorApplicationService.judgeApplication(request, judgeUserId);
        return ResultUtils.success(result);
    }
}