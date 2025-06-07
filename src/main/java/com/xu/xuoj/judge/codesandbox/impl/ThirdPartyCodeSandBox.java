package com.xu.xuoj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xu.xuoj.judge.codesandbox.model.SubmissionResult;
import com.xu.xuoj.model.enums.QuestionSubmitLanguageEnum;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.xu.xuoj.constant.ReConstant.AUTH_KEY;
import static com.xu.xuoj.constant.ReConstant.AUTH_TOKEN;

@Component("thirdPartyCodeSandBox")
@NoArgsConstructor
@Slf4j
public class ThirdPartyCodeSandBox {

    public List<SubmissionResult> excuteCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://115.190.45.182:2358/submissions/?base64_encoded=false&wait=true";
        String language = executeCodeRequest.getLanguage();
        List<SubmissionResult> submissionResults = new ArrayList<>();
        for (String input : executeCodeRequest.getInputList()) {
            SortedMap<Object, Object> sortedMap = new TreeMap<Object, Object>() {
                private static final long serialVersionUID = 1L;
                {
                    put("source_code", executeCodeRequest.getCode());
                    put("language_id", QuestionSubmitLanguageEnum.getEnumByValue(language).getId());
                    put("stdin", input);
                }
            };
            String json = JSONUtil.toJsonPrettyStr(sortedMap);
            String responseStr = HttpUtil.createPost(url)
                .header(AUTH_KEY, AUTH_TOKEN)
                .body(json)
                .execute()
                .body();
            SubmissionResult submissionResult = JSONUtil.toBean(responseStr, SubmissionResult.class);
            submissionResults.add(submissionResult);
        }
        if (submissionResults.isEmpty()) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error,message=" + submissionResults);
        }
        return submissionResults;
    }
}
