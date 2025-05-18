package com.xu.xuoj.judge.codesandbox;

import com.xu.xuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xu.xuoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;


@Component
public interface CodeSandBox {
    ExecuteCodeResponse excuteCode(ExecuteCodeRequest executeCodeRequest);
}
