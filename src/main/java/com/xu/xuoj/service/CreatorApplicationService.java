package com.xu.xuoj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.xuoj.model.dto.apply.CreatorApplyJudgeRequest;
import com.xu.xuoj.model.dto.apply.CreatorApplyQueryRequest;
import com.xu.xuoj.model.dto.apply.CreatorApplyRequest;
import com.xu.xuoj.model.entity.CreatorApplication;

/**
 * @author hy
 * @description 针对表【creator_application(出题者权限申请表)】的数据库操作Service
 * @createDate 2025-05-05 20:55:16
 */
public interface CreatorApplicationService extends IService<CreatorApplication> {

    /**
     * 提交申请
     */
    long submitApplication(CreatorApplyRequest request, Long userId);

    /**
     * 获取当前用户申请状态
     */
    CreatorApplication getUserApplication(Long userId);

    /**
     * 分页查询申请列表
     */
    Page<CreatorApplication> pageApplications(CreatorApplyQueryRequest request);

    /**
     * 审核申请
     */
    boolean judgeApplication(CreatorApplyJudgeRequest request, Long judgeUserId);
}
