package com.xu.xuoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户参赛记录表
 * @TableName user_game
 */
@TableName(value ="user_game")
@Data
public class UserGame implements Serializable {
    /**
     * 关联id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 比赛id
     */
    private Long gameId;

    /**
     * 报名或参赛时间
     */
    private Date registrationTime;

    /**
     * 参赛状态（例如 0: 已报名, 1: 已参加, 2: 已结束, 3: 缺席）
     */
    private Integer status;

    /**
     * 是否为正式参赛者 (区分打星/打铁用户, 1: 是, 0: 否)
     */
    private Integer isOfficial;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}