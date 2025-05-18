package com.xu.xuoj.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameAddDTO {
    /**
     * 比赛名称
     */
    @NotBlank(message = "比赛名称不能为空")
    @Size(max = 100, message = "比赛名称长度不能超过100")
    private String gameName;

    /**
     * 比赛开始日期和时间
     */
    @Future(message = "比赛时间必须是将来的时间")
    private Date gameDate;

    /**
     * 比赛总时间（分钟）
     */
    @Min(value = 1, message = "比赛时长不能小于1分钟")
    @Max(value = 1440, message = "比赛时长不能超过24小时")
    private Integer durationMinutes;

    /**
     * 比赛规则
     */
    @NotBlank(message = "比赛规则不能为空")
    private String rules;

    /**
     * 比赛奖励
     */
    private String awards;

    /**
     * 比赛类型
     */
    @NotNull(message = "比赛类型不能为空")
    private Integer type;

    /**
     * 比赛密码（如有）
     */
    private String password;

    /**
     * 比赛描述
     */
    private String description;
}
