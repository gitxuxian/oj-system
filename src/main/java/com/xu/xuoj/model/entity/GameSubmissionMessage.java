package com.xu.xuoj.model.entity;// 你可以创建一个新的 DTO 类来承载消息内容
// package com.yupi.xuoj.model.dto.message; // 根据你的包结构调整

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSubmissionMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long submissionId;
    private Long gameId;
    private Long userId;
    private String userName; // 需要传递用户名
}