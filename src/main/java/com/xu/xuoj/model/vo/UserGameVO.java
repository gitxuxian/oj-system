package com.xu.xuoj.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGameVO {
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
}
