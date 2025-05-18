package com.xu.xuoj.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameVO {
    /**
     * 比赛id
     */
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
     * 比赛类型
     */
    private Integer type;

    /**
     * 比赛状态
     */
    private Integer status;

    /**
     * 比赛描述
     */
    private String description;
}
