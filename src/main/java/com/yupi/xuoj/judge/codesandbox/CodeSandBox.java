package com.yupi.xuoj.judge.codesandbox;

import com.yupi.xuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.xuoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;


@Component
public interface CodeSandBox {
    ExecuteCodeResponse excuteCode(ExecuteCodeRequest executeCodeRequest);
}
