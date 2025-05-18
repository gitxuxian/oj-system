package com.xu.xuoj.judge.codesandbox;

import com.xu.xuoj.config.CodeSandBoxConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CodeSandBoxFactory {

    @Autowired
    private CodeSandBoxConfig codeSandBoxConfig;

    @Autowired
    private Map<String, CodeSandBox> codeSandBoxMap;

    public CodeSandBox getCodeSandBox() {
        String type = codeSandBoxConfig.getType();
        return codeSandBoxMap.getOrDefault(type + "CodeSandBox", codeSandBoxMap.get("exampleCodeSandBox"));
    }

}
