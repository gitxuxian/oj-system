package com.xu.xuoj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum QuestionSubmitStatusEnum {
    // 0 - 待判题、1 - 判题中、2 - 成功、3 - 失败
    IN_QUEUE("排队中", 1),
    PROCESSING("处理中", 2),
    ACCEPTED("通过", 3),
    WRONG_ANSWER("答案错误", 4),
    TIME_LIMIT_EXCEEDED("时间超限", 5),
    COMPILATION_ERROR("编译错误", 6),
    RUNTIME_ERROR_SIGSEGV("运行时错误 (段错误)", 7),
    RUNTIME_ERROR_SIGXFSZ("运行时错误 (文件大小超限)", 8),
    RUNTIME_ERROR_SIGFPE("运行时错误 (浮点异常)", 9),
    RUNTIME_ERROR_SIGABRT("运行时错误 (中止)", 10),
    RUNTIME_ERROR_NZEC("运行时错误 (非零退出码)", 11),
    RUNTIME_ERROR_OTHER("运行时错误 (其他)", 12),
    INTERNAL_ERROR("内部错误", 13),
    EXEC_FORMAT_ERROR("执行格式错误", 14);

    private final String text;

    private final Integer value;

    QuestionSubmitStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static QuestionSubmitStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionSubmitStatusEnum anEnum : QuestionSubmitStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
