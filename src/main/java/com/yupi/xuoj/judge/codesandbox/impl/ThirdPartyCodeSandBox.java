package com.yupi.xuoj.judge.codesandbox.impl;

import com.yupi.xuoj.judge.codesandbox.CodeSandBox;
import com.yupi.xuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.xuoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

@Component("thirdPartyCodeSandBox")
public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse excuteCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
