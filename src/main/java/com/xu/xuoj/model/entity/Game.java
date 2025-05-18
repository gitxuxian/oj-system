package com.xu.xuoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 比赛信息表
 * @TableName game
 */
@TableName(value ="game")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game implements Serializable {
    /**
     * 比赛id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 比赛名称
     */
    private String gameName;

    /**
     * 比赛开始日期和时间
     */
    private Date gameDate;

    /**
     * 比赛总时间（分钟）
     */
    private Integer durationMinutes;

    /**
     * 比赛规则
     */
    private String rules;

    /**
     * 比赛奖励
     */
    private String awards;

    /**
     * 创建比赛的用户id
     */
    private Long creatorUserId;

    /**
     * 比赛类型（例如 0: ACM, 1: OI, 2: 其他）
     */
    private Integer type;

    /**
     * 比赛状态（例如 0: 未开始, 1: 进行中, 2: 已结束, 3: 已归档）
     */
    private Integer status;

    /**
     * 比赛密码（如有）
     */
    private String password;

    /**
     * 比赛描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}