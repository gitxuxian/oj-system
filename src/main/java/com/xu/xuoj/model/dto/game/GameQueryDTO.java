package com.xu.xuoj.model.dto.game;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xu.xuoj.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GameQueryDTO extends PageRequest {
    /**
     * 比赛名称
     */
    @TableField(value = "gameName")
    private String gameName;

    /**
     * 比赛类型
     */
    private Integer type;

    /**
     * 比赛状态
     */
    private Integer status;
}
