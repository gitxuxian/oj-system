package com.xu.xuoj.model.dto.apply;

import com.xu.xuoj.common.PageRequest;
import lombok.Data;

@Data
public class CreatorApplyQueryRequest extends PageRequest {
    private Integer status;
}