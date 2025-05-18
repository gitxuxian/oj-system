package com.xu.xuoj.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameQuestionDTO implements Serializable {
    /**
     * 比赛id
     */
    @NotNull
    private Long gameId;

    /**
     * 题目id
     */
    @NotNull
    private Long questionId;

    /**
     * 题目在比赛中的显示顺序或编号
     */
    @NotNull
    private Integer displayOrder;

    /**
     * 该题目在本次比赛中的分值 (适用于OI等赛制)
     */
    @NotNull
    private Integer score;

    /**
     * 题目在比赛中的别名或自定义标题 (例如 A, B, C)
     */
    private String titleAlias;
}
