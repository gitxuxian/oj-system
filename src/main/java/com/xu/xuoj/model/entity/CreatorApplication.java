package com.xu.xuoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 出题者权限申请表
 *
 * @TableName creator_application
 */
@TableName(value = "creator_application")
@Builder
@Data
public class CreatorApplication {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 申请用户ID
     */
    private Long user_id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 状态 0-待审核 1-已通过 2-已拒绝
     */
    private Integer status;

    /**
     * 申请时间
     */
    private Date create_time;

    /**
     * 审核时间
     */
    private Date judge_time;

    /**
     * 审核人ID
     */
    private Long judge_user_id;

    /**
     * 审核人用户名
     */
    private String judge_username;

    /**
     * 审核备注
     */
    private String judge_comment;
}