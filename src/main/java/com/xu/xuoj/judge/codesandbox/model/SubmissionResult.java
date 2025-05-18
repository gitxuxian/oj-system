package com.xu.xuoj.judge.codesandbox.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResult {

    /**
     * 输出
     */
    private String stdout;

    /**
     * 消耗时间
     */
    private String time;

    /**
     * 消耗内存
     */
    private Integer memory;

    /**
     * 错误
     */
    private String stderr;

    /**
     *
     */
    private String token;

    /**
     * 编译输出
     */
    private String compileOutput;

    /**
     * 信息
     */
    private String message;

    /**
     * 判题状态
     */
    private Status status;
}
