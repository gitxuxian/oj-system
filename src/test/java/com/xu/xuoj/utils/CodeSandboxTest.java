package com.xu.xuoj.utils;

import com.xu.xuoj.judge.codesandbox.CodeSandBox;
import com.xu.xuoj.judge.codesandbox.CodeSandBoxFactory;
import com.xu.xuoj.judge.codesandbox.impl.RemoteCodeSandBox;
import com.xu.xuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xu.xuoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.xu.xuoj.model.enums.QuestionSubmitLanguageEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
class CodeSandboxTest {

    @Autowired
    CodeSandBoxFactory codeSandBoxFactory;

    @Test
    void executeCode() {
        CodeSandBox codeSandBox = codeSandBoxFactory.getCodeSandBox();
        String code = "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        int a=Integer.parseInt(args[0]);\n" +
            "        int b=Integer.parseInt(args[1]);\n" +
            "        System.out.println(\"结果\"+(a+b));\n" +
            "    }\n" +
            "}\n";
        String language = QuestionSubmitLanguageEnum.JAVA_OPENJDK_8.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
            .code(code)
            .language(language)
            .inputList(inputList)
            .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.excuteCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
        Assertions.assertNotNull(executeCodeResponse);
    }

}
