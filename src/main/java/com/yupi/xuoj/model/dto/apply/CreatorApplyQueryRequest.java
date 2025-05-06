package com.yupi.xuoj.model.dto.apply;

import com.yupi.xuoj.common.PageRequest;
import lombok.Data;

@Data
public class CreatorApplyQueryRequest extends PageRequest {
    private Integer status;
    private String username;
}