package com.xu.xuoj.model.dto.apply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatorApplyJudgeRequest implements Serializable {
    @NotNull
    private Long id;

    // 1-通过 2-拒绝
    private Integer status;

    //审核备注
    private String judgeComment;
}
