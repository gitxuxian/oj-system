package com.yupi.xuoj.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.xuoj.common.BaseResponse;
import com.yupi.xuoj.common.ErrorCode;
import com.yupi.xuoj.common.ResultUtils;
import com.yupi.xuoj.exception.BusinessException;
import com.yupi.xuoj.model.dto.apply.CreatorApplyJudgeRequest;
import com.yupi.xuoj.model.dto.apply.CreatorApplyQueryRequest;
import com.yupi.xuoj.model.dto.apply.CreatorApplyRequest;
import com.yupi.xuoj.model.entity.CreatorApplication;
import com.yupi.xuoj.service.CreatorApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/creator")
@Slf4j
public class CreatorApplicationController {

    @Resource
    private CreatorApplicationService creatorApplicationService;
    
    /**
     * 提交出题权限申请
     */
    @PostMapping("/apply")
    @SaCheckLogin
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
    public BaseResponse<Boolean> judgeApplication(@RequestBody CreatorApplyJudgeRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long judgeUserId = StpUtil.getLoginIdAsLong();
        boolean result = creatorApplicationService.judgeApplication(request, judgeUserId);
        return ResultUtils.success(result);
    }
}