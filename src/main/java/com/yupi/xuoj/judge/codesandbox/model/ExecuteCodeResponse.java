package com.yupi.xuoj.judge.codesandbox.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {

    /**
     * 输出
     */
    private List<String> ouputList;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 执行状态
     */
    private String status;

    /**
     * 接口信息
     */
    private String message;
}
