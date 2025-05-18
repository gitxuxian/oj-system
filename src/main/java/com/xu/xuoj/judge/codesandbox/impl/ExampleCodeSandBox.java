package com.xu.xuoj.judge.codesandbox.impl;

import com.xu.xuoj.judge.codesandbox.CodeSandBox;
import com.xu.xuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xu.xuoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

@Component("exampleCodeSandBox")
public class ExampleCodeSandBox implements CodeSandBox {


    @Override
    public ExecuteCodeResponse excuteCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("示例代码沙箱");
        return null;
    }
}
