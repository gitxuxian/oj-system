package com.xu.xuoj.model.enums;

import org.apache.commons.lang3.ObjectUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum QuestionSubmitLanguageEnum {
    C_CLANG_7_0_1("C (Clang 7.0.1)", "C (Clang 7.0.1)", 75),
    CPP_CLANG_7_0_1("C++ (Clang 7.0.1)", "C++ (Clang 7.0.1)", 76),
    CPP_G_4_8_5("C++ (g++ 4.8.5)", "C++ (g++ 4.8.5)", 15),
    CPP_G_4_9_4("C++ (g++ 4.9.4)", "C++ (g++ 4.9.4)", 14),
    CPP_G_5_4_0("C++ (g++ 5.4.0)", "C++ (g++ 5.4.0)", 13),
    CPP_G_6_3_0("C++ (g++ 6.3.0)", "C++ (g++ 6.3.0)", 12),
    CPP_G_6_4_0("C++ (g++ 6.4.0)", "C++ (g++ 6.4.0)", 11),
    CPP_G_7_2_0("C++ (g++ 7.2.0)", "C++ (g++ 7.2.0)", 10),
    C_GCC_4_8_5("C (gcc 4.8.5)", "C (gcc 4.8.5)", 9),
    C_GCC_4_9_4("C (gcc 4.9.4)", "C (gcc 4.9.4)", 8),
    C_GCC_5_4_0("C (gcc 5.4.0)", "C (gcc 5.4.0)", 7),
    C_GCC_6_3_0("C (gcc 6.3.0)", "C (gcc 6.3.0)", 6),
    C_GCC_6_4_0("C (gcc 6.4.0)", "C (gcc 6.4.0)", 5),
    C_GCC_7_2_0("C (gcc 7.2.0)", "C (gcc 7.2.0)", 4),
    C_GCC_7_4_0("C (GCC 7.4.0)", "C (GCC 7.4.0)", 48),
    CPP_GCC_7_4_0("C++ (GCC 7.4.0)", "C++ (GCC 7.4.0)", 52),
    C_GCC_8_3_0("C (GCC 8.3.0)", "C (GCC 8.3.0)", 49),
    CPP_GCC_8_3_0("C++ (GCC 8.3.0)", "C++ (GCC 8.3.0)", 53),
    C_GCC_9_2_0("C (GCC 9.2.0)", "C (GCC 9.2.0)", 50),
    CPP_GCC_9_2_0("C++ (GCC 9.2.0)", "C++ (GCC 9.2.0)", 54),
    CSHARP_MONO_5_2_0_224("C# (mono 5.2.0.224)", "C# (mono 5.2.0.224)", 17),
    CSHARP_MONO_5_4_0_167("C# (mono 5.4.0.167)", "C# (mono 5.4.0.167)", 16),
    CSHARP_MONO_6_6_0_161("C# (Mono 6.6.0.161)", "C# (Mono 6.6.0.161)", 51),
    ERLANG_OTP_20_0("Erlang (OTP 20.0)", "Erlang (OTP 20.0)", 21),
    ERLANG_OTP_22_2("Erlang (OTP 22.2)", "Erlang (OTP 22.2)", 58),
    GO_1_13_5("Go (1.13.5)", "Go (1.13.5)", 60),
    GO_1_9("Go (1.9)", "Go (1.9)", 22),
    JAVA_OPENJDK_13_0_1("Java (OpenJDK 13.0.1)", "Java (OpenJDK 13.0.1)", 62),
    JAVA_OPENJDK_7("Java (OpenJDK 7)", "Java (OpenJDK 7)", 28),
    JAVA_OPENJDK_8("Java (OpenJDK 8)", "Java (OpenJDK 8)", 27),
    JAVA_OPENJDK_9_WITH_ECLIPSE_OPENJ9("Java (OpenJDK 9 with Eclipse OpenJ9)", "Java (OpenJDK 9 with Eclipse OpenJ9)", 26),
    JAVASCRIPT_NODE_JS_12_14_0("JavaScript (Node.js 12.14.0)", "JavaScript (Node.js 12.14.0)", 63),
    JAVASCRIPT_NODEJS_7_10_1("JavaScript (nodejs 7.10.1)", "JavaScript (nodejs 7.10.1)", 30),
    JAVASCRIPT_NODEJS_8_5_0("JavaScript (nodejs 8.5.0)", "JavaScript (nodejs 8.5.0)", 29),
    KOTLIN_1_3_70("Kotlin (1.3.70)", "Kotlin (1.3.70)", 78),
    LUA_5_3_5("Lua (5.3.5)", "Lua (5.3.5)", 64),
    PHP_7_4_1("PHP (7.4.1)", "PHP (7.4.1)", 68),
    PYTHON_2_6_9("Python (2.6.9)", "Python (2.6.9)", 37),
    PYTHON_2_7_17("Python (2.7.17)", "Python (2.7.17)", 70),
    PYTHON_2_7_9("Python (2.7.9)", "Python (2.7.9)", 36),
    PYTHON_3_5_3("Python (3.5.3)", "Python (3.5.3)", 35),
    PYTHON_3_6_0("Python (3.6.0)", "Python (3.6.0)", 34),
    PYTHON_3_8_1("Python (3.8.1)", "Python (3.8.1)", 71),
    R_4_0_0("R (4.0.0)", "R (4.0.0)", 80),
    RUBY_2_1_9("Ruby (2.1.9)", "Ruby (2.1.9)", 41),
    RUBY_2_2_6("Ruby (2.2.6)", "Ruby (2.2.6)", 40),
    RUBY_2_3_3("Ruby (2.3.3)", "Ruby (2.3.3)", 39),
    RUBY_2_4_0("Ruby (2.4.0)", "Ruby (2.4.0)", 38),
    RUBY_2_7_0("Ruby (2.7.0)", "Ruby (2.7.0)", 72),
    RUST_1_20_0("Rust (1.20.0)", "Rust (1.20.0)", 42),
    RUST_1_40_0("Rust (1.40.0)", "Rust (1.40.0)", 73),
    SCALA_2_13_2("Scala (2.13.2)", "Scala (2.13.2)", 81),
    SQL_SQLITE_3_27_2("SQL (SQLite 3.27.2)", "SQL (SQLite 3.27.2)", 82),
    SWIFT_5_2_3("Swift (5.2.3)", "Swift (5.2.3)", 83),
    TYPESCRIPT_3_7_4("TypeScript (3.7.4)", "TypeScript (3.7.4)", 74);


    private final String text;

    private final String value;

    private final int Id;

    QuestionSubmitLanguageEnum(String text, String value, int id) {
        this.text = text;
        this.value = value;
        this.Id = id;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static QuestionSubmitLanguageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionSubmitLanguageEnum anEnum : QuestionSubmitLanguageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return Id;
    }
}
