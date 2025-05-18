package com.xu.xuoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.model.dto.apply.CreatorApplyJudgeRequest;
import com.xu.xuoj.model.dto.apply.CreatorApplyQueryRequest;
import com.xu.xuoj.model.dto.apply.CreatorApplyRequest;
import com.xu.xuoj.model.entity.CreatorApplication;
import com.xu.xuoj.mapper.CreatorApplicationMapper;
import com.xu.xuoj.model.entity.User;
import com.xu.xuoj.service.CreatorApplicationService;
import com.xu.xuoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Date;

/**
 * @author hy
 * @description 针对表【creator_application(出题者权限申请表)】的数据库操作Service实现
 * @createDate 2025-05-05 20:55:16
 */
@Service
@Slf4j
public class CreatorApplicationServiceImpl extends ServiceImpl<CreatorApplicationMapper, CreatorApplication> implements CreatorApplicationService {

    @Resource
    private CreatorApplicationMapper creatorApplicationMapper;

    @Resource
    private UserService userService;

    @Override
    public long submitApplication(CreatorApplyRequest request, Long userId) {
        // 检查用户是否已有申请记录
        LambdaQueryWrapper<CreatorApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreatorApplication::getUser_id, userId)
            .orderByDesc(CreatorApplication::getCreate_time)
            .last("LIMIT 1");
        CreatorApplication existApplication = creatorApplicationMapper.selectOne(queryWrapper);

        // 如果有待审核或已通过的申请，直接返回
        if (existApplication != null) {
            if (existApplication.getStatus() == 0) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "您已有待审核的申请，请耐心等待");
            }
            if (existApplication.getStatus() == 1) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "您已是出题者，无需重复申请");
            }
        }

        // 获取用户名
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 构建申请记录
        CreatorApplication application = CreatorApplication.builder()
            .user_id(userId)
            .username(user.getUserName())
            .reason(request.getReason())
            .status(0)
            .create_time(new Date())
            .build();

        // 保存申请
        creatorApplicationMapper.insert(application);
        return application.getId();
    }

    @Override
    public CreatorApplication getUserApplication(Long userId) {
        LambdaQueryWrapper<CreatorApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreatorApplication::getUser_id, userId)
            .orderByDesc(CreatorApplication::getCreate_time)
            .last("LIMIT 1");
        return creatorApplicationMapper.selectOne(queryWrapper);
    }

    @Override
    public Page<CreatorApplication> pageApplications(CreatorApplyQueryRequest request) {
        long current = request.getCurrent();
        long size = request.getPageSize();

        Page<CreatorApplication> page = new Page<>(current, size);

        LambdaQueryWrapper<CreatorApplication> queryWrapper = new LambdaQueryWrapper<>();

        // 设置查询条件
        if (request.getStatus() != null) {
            queryWrapper.eq(CreatorApplication::getStatus, request.getStatus());
        }
        if (StringUtils.isNotBlank(request.getUsername())) {
            queryWrapper.like(CreatorApplication::getUsername, request.getUsername());
        }

        // 按申请时间倒序
        queryWrapper.orderByDesc(CreatorApplication::getCreate_time);

        return creatorApplicationMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean judgeApplication(CreatorApplyJudgeRequest request, Long judgeUserId) {
        // 获取申请记录
        CreatorApplication application = creatorApplicationMapper.selectById(request.getId());
        if (application == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "申请记录不存在");
        }

        // 判断是否已审核
        if (application.getStatus() != 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该申请已被审核");
        }

        // 获取审核人信息
        User judgeUser = userService.getById(judgeUserId);
        if (judgeUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "审核人不存在");
        }

        // 更新申请状态
        application.setStatus(request.getStatus());
        application.setJudge_time(new Date());
        application.setJudge_user_id(judgeUserId);
        application.setJudge_username(judgeUser.getUserName());
        application.setJudge_comment(request.getJudgeComment());

        // 如果通过申请，为用户添加出题者角色
        if (request.getStatus() == 1) {
            try {
                userService.update(
                    new LambdaUpdateWrapper<User>()
                        .eq(User::getId, application.getUser_id())
                        .set(User::getUserRole, "problem_creator")
                );
                log.info("用户 {} 被授予出题者角色", application.getUsername());
            } catch (Exception e) {
                log.error("为用户添加出题者角色失败", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "角色授予失败");
            }
        }
        // 保存审核结果
        return creatorApplicationMapper.updateById(application) > 0;
    }
}




