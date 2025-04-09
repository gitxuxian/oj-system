package com.yupi.xuoj.model.dto.postthumb;

import java.io.Serializable;
import lombok.Data;


@Data
public class PostThumbAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}