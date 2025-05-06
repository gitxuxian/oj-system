package com.yupi.xuoj.model.dto.apply;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class CreatorApplyJudgeRequest {
    @NotNull
    private Long id;

    // 1-通过 2-拒绝
    private Integer status;

    //审核备注
    private String judgeComment;
}
