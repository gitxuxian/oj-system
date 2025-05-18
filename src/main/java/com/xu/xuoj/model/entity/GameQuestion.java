package com.xu.xuoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 比赛与题目关联表
 * @TableName game_question
 */
@TableName(value ="game_question")
@Data
public class GameQuestion implements Serializable {
    /**
     * 关联id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 比赛id
     */
    private Long gameId;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 题目在比赛中的显示顺序或编号
     */
    private Integer displayOrder;

    /**
     * 该题目在本次比赛中的分值 (适用于OI等赛制)
     */
    private Integer score;

    /**
     * 题目在比赛中的别名或自定义标题 (例如 A, B, C)
     */
    private String titleAlias;

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