package com.yupi.xuoj.judge.codesandbox.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.xuoj.common.ErrorCode;
import com.yupi.xuoj.exception.BusinessException;
import com.yupi.xuoj.judge.codesandbox.CodeSandBox;
import com.yupi.xuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.xuoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.yupi.xuoj.judge.codesandbox.model.JudgeInfo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import java.util.*;

@Component("thirdPartyCodeSandBox")
@NoArgsConstructor
@Slf4j
public class ThirdPartyCodeSandBox implements CodeSandBox {

    private static final JSONArray COMPILE_FILES = new JSONArray();
    private static final String SANDBOX_BASE_URL = "http://115.190.45.182:5051";
    private static final int maxProcessNumber = 128;
    private static final int STDIO_SIZE_MB = 32;

    static {
        JSONObject content = new JSONObject();
        content.set("content", "");

        JSONObject stdout = new JSONObject();
        stdout.set("name", "stdout");
        stdout.set("max", 1024 * 1024 * STDIO_SIZE_MB);

        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * STDIO_SIZE_MB);
        COMPILE_FILES.put(content);
        COMPILE_FILES.put(stdout);
        COMPILE_FILES.put(stderr);
    }

    // ==== API方法 ====
    public JSONArray run(String uri, JSONObject param) {
        try {
            String jsonStr = JSONUtil.toJsonStr(param);
            HttpResponse response = HttpRequest.post(SANDBOX_BASE_URL + uri)
                .header("Content-Type", "application/json")
                .body(jsonStr)
                .execute();

            String responseStr = response.body();

            if (StringUtils.isBlank(responseStr)) {
                throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "沙箱请求异常，返回为空");
            }

            if (response.getStatus() != 200) {
                throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "沙箱请求异常，状态码：" + response.getStatus());
            }

            return JSONUtil.parseArray(responseStr);
        } catch (Exception e) {
            log.error("沙箱请求异常", e);
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "沙箱请求异常：" + e.getMessage());
        }
    }

    /**
     * 删除沙箱中的文件
     */
    public static void delFile(String fileId) {
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        try {
            HttpRequest.delete(SANDBOX_BASE_URL + "/file/" + fileId)
                .execute();
        } catch (Exception e) {
            log.error("删除沙箱文件异常", e);
        }
    }

    /**
     * 编译用户代码
     */
    public JSONArray compile(Long maxCpuTime,
                             Long maxRealTime,
                             Long maxMemory,
                             Long maxStack,
                             String srcName,
                             String exeName,
                             List<String> args,
                             List<String> envs,
                             String code,
                             HashMap<String, String> extraFiles,
                             Boolean needCopyOutCached,
                             Boolean needCopyOutExe,
                             String copyOutDir) {
        JSONObject cmd = new JSONObject();
        cmd.set("args", args);
        cmd.set("env", envs);
        cmd.set("files", COMPILE_FILES);
        // ms-->ns
        cmd.set("cpuLimit", maxCpuTime * 1000 * 1000L);
        cmd.set("clockLimit", maxRealTime * 1000 * 1000L);
        // byte
        cmd.set("memoryLimit", maxMemory);
        cmd.set("procLimit", maxProcessNumber);
        cmd.set("stackLimit", maxStack);

        JSONObject fileContent = new JSONObject();
        fileContent.set("content", code);

        JSONObject copyIn = new JSONObject();
        copyIn.set(srcName, fileContent);

        if (extraFiles != null) {
            for (Map.Entry<String, String> entry : extraFiles.entrySet()) {
                if (!StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue())) {
                    JSONObject content = new JSONObject();
                    content.set("content", entry.getValue());
                    copyIn.set(entry.getKey(), content);
                }
            }
        }

        cmd.set("copyIn", copyIn);
        cmd.set("copyOut", new JSONArray().put("stdout").put("stderr"));

        if (needCopyOutCached) {
            cmd.set("copyOutCached", new JSONArray().put(exeName));
        }

        if (needCopyOutExe) {
            cmd.set("copyOutDir", copyOutDir);
        }

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        JSONArray result = this.run("/run", param);
        if (result != null && !result.isEmpty()) {
            JSONObject compileRes = (JSONObject) result.get(0);
            compileRes.set("originalStatus", compileRes.getStr("status"));
        }
        return result;
    }

    /**
     * 运行用户代码
     */
    public JSONArray testCase(List<String> args,
                              List<String> envs,
                              String testCasePath,
                              String testCaseContent,
                              Long maxTime,
                              Long maxMemory,
                              Long maxOutputSize,
                              Integer maxStack,
                              String exeName,
                              String fileId,
                              String fileContent,
                              Boolean isFileIO,
                              String ioReadFileName,
                              String ioWriteFileName) {
        JSONObject cmd = new JSONObject();
        cmd.set("args", args);
        cmd.set("env", envs);

        JSONArray files = new JSONArray();

        JSONObject testCaseInput = new JSONObject();
        if (StringUtils.isEmpty(testCasePath)) {
            testCaseInput.set("content", testCaseContent);
        } else {
            testCaseInput.set("src", testCasePath);
        }

        if (BooleanUtils.isFalse(isFileIO)) {
            JSONObject stdout = new JSONObject();
            stdout.set("name", "stdout");
            stdout.set("max", maxOutputSize);
            files.put(testCaseInput);
            files.put(stdout);
        }

        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * 16);
        files.put(stderr);

        cmd.set("files", files);

        // ms-->ns
        cmd.set("cpuLimit", maxTime * 1000 * 1000L);
        cmd.set("clockLimit", maxTime * 1000 * 1000L * 3);
        // byte
        cmd.set("memoryLimit", (maxMemory + 100) * 1024 * 1024L);
        cmd.set("procLimit", maxProcessNumber);
        cmd.set("stackLimit", maxStack * 1024 * 1024L);

        JSONObject exeFile = new JSONObject();
        if (!StringUtils.isEmpty(fileId)) {
            exeFile.set("fileId", fileId);
        } else {
            exeFile.set("content", fileContent);
        }
        JSONObject copyIn = new JSONObject();
        copyIn.set(exeName, exeFile);

        JSONArray copyOut = new JSONArray();
        copyOut.put("stderr");
        if (BooleanUtils.isFalse(isFileIO)) {
            copyOut.put("stdout");
        } else {
            copyIn.set(ioReadFileName, testCaseInput);
            // 在文件名之后加入 '?' 来使文件变为可选，可选文件不存在的情况不会触发 FileError
            copyOut.put(ioWriteFileName + "?");
        }

        cmd.set("copyIn", copyIn);
        cmd.set("copyOut", copyOut);

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        // 调用判题安全沙箱
        JSONArray result = this.run("/run", param);

        if (result != null && !result.isEmpty()) {
            JSONObject testcaseRes = (JSONObject) result.get(0);
            testcaseRes.set("originalStatus", testcaseRes.getStr("status"));
        }
        return result;
    }

    /**
     * 执行代码接口
     */
    @Override
    public ExecuteCodeResponse excuteCode(ExecuteCodeRequest executeCodeRequest) {
        // 1. 提取请求参数
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        List<String> inputList = executeCodeRequest.getInputList();

        try {
            //2. 根据语言设置编译和运行参数
            String srcName = getSrcFileName(language);
            String exeName = getExeName(language);
            List<String> compileArgs = getCompileArgs(language, srcName, exeName);
            List<String> runArgs = getRunArgs(language, exeName);
            List<String> envs = getEnvs(language);

            //3. 编译代码
            String fileId = null;
            JudgeInfo compileMessage = new JudgeInfo();

            if (needCompile(language)) {
                JSONArray compileResult = compile(
                    10000L, // 最大CPU时间(ms)
                    30000L, // 最大实际时间(ms)
                    256 * 1024 * 1024L, // 最大内存(byte)
                    128 * 1024 * 1024L, // 最大栈(byte)
                    srcName, // 源文件名
                    exeName, // 编译后文件名
                    compileArgs, // 编译命令
                    envs, // 环境变量
                    code, // 用户代码
                    null, // 额外文件
                    true, // 是否需要缓存编译文件
                    false, // 是否需要复制输出的可执行文件
                    null // 复制输出目录
                );

                // 处理编译结果
                if (compileResult == null || compileResult.isEmpty()) {
                    throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "编译请求返回为空");
                }

                JSONObject result = (JSONObject) compileResult.get(0);
                String status = result.getStr("status");

                if (!"Accepted".equals(status)) { // 编译错误
                    String stderr = ((JSONObject) result.get("files")).getStr("stderr");
                    compileMessage.setMessage(stderr);

                    // 返回编译错误响应
                    ExecuteCodeResponse response = new ExecuteCodeResponse();
                    response.setStatus("1"); // 编译错误状态码
                    response.setMessage("编译失败");
                    response.setOuputList(new ArrayList<>());
                    response.setJudgeInfo(compileMessage);
                    return response;
                }

                // 获取文件ID
                JSONObject fileIds = result.getJSONObject("fileIds");
                if (fileIds != null) {
                    fileId = fileIds.getStr(exeName);
                }
            }

            // 4. 运行测试用例
            List<JudgeInfo> executeMessageList = new ArrayList<>();
            List<String> outputList = new ArrayList<>();
            int status = 0; // 默认成功

            for (String inputData : inputList) {
                JSONArray runResult = testCase(
                    runArgs, // 运行命令
                    envs, // 环境变量
                    null, // 测试用例文件路径
                    inputData, // 测试用例输入内容
                    5000L, // 最大运行时间(ms)
                    128L, // 最大内存(MB)
                    10240L, // 最大输出大小(KB)
                    128, // 最大栈(MB)
                    exeName, // 可执行文件名
                    fileId, // 文件ID
                    needCompile(language) ? null : code, // 如果是解释型语言，则直接传代码
                    false, // 是否文件IO
                    null, // 输入文件名
                    null  // 输出文件名
                );

                // 处理运行结果
                if (runResult == null || runResult.isEmpty()) {
                    throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "运行请求返回为空");
                }

                JSONObject result = (JSONObject) runResult.get(0);
                String runStatus = result.getStr("status");
                boolean isAccepted = "Accepted".equals(runStatus);

                JudgeInfo judgeInfo = new JudgeInfo();
                // 转换为ms
                judgeInfo.setTime(result.getLong("time") / 1000000);
                // 转换为KB
                judgeInfo.setMemory(result.getLong("memory") / 1024);

                judgeInfo.setMessage(runStatus);

                // 如果执行成功
                if (isAccepted) {
                    JSONObject files = result.getJSONObject("files");
                    if (files != null) {
                        String stdout = files.getStr("stdout");
                        outputList.add(stdout);

                    }
                } else {
                    status = 2; // 运行错误
                    JSONObject files = result.getJSONObject("files");
                    if (files != null) {
                        String stderr = files.getStr("stderr");
                        judgeInfo.setMessage(stderr);
                    }
                }

                executeMessageList.add(judgeInfo);
            }

            // 5. 清理资源
            if (fileId != null) {
                delFile(fileId);
            }

            // 6. 组装返回结果
            ExecuteCodeResponse response = new ExecuteCodeResponse();
            response.setStatus(String.valueOf(status));
            response.setMessage(status == 0 ? "执行成功" : "执行失败");
            response.setOuputList(outputList);
            response.setJudgeInfoList(executeMessageList);

            return response;

        } catch (BusinessException e) {
            log.error("业务异常", e);
            // 返回业务错误响应
            ExecuteCodeResponse response = new ExecuteCodeResponse();
            response.setStatus("3"); // 系统错误状态码
            response.setMessage("业务异常：" + e.getMessage());
            return response;
        } catch (Exception e) {
            log.error("代码执行异常", e);
            // 返回系统错误响应
            ExecuteCodeResponse response = new ExecuteCodeResponse();
            response.setStatus("3"); // 系统错误状态码
            response.setMessage("系统错误：" + e.getMessage());
            return response;
        }
    }

    /**
     * 根据语言判断是否需要编译
     */
    private boolean needCompile(String language) {
        return "c".equals(language) || "cpp".equals(language) || "java".equals(language);
    }

    /**
     * 获取源文件名
     */
    private String getSrcFileName(String language) {
        switch (language) {
            case "c":
                return "main.c";
            case "cpp":
                return "main.cpp";
            case "java":
                return "Main.java";
            case "python":
                return "main.py";
            case "javascript":
                return "main.js";
            default:
                return "main";
        }
    }

    /**
     * 获取可执行文件名
     */
    private String getExeName(String language) {
        switch (language) {
            case "c":
            case "cpp":
                return "main";
            case "java":
                return "Main";
            case "python":
                return "main.py";
            case "javascript":
                return "main.js";
            default:
                return "main";
        }
    }

    /**
     * 获取编译参数
     */
    private List<String> getCompileArgs(String language, String srcName, String exeName) {
        switch (language) {
            case "c":
                return Arrays.asList("/usr/bin/gcc", srcName, "-o", exeName, "-O2", "-std=c11");
            case "cpp":
                return Arrays.asList("/usr/bin/g++", srcName, "-o", exeName, "-O2", "-std=c++14");
            case "java":
                return Arrays.asList("/usr/bin/javac", srcName);
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 获取运行参数
     */
    private List<String> getRunArgs(String language, String exeName) {
        switch (language) {
            case "c":
            case "cpp":
                return Arrays.asList("./" + exeName);
            case "java":
                return Arrays.asList("/usr/bin/java", exeName);
            case "python":
                return Arrays.asList("/usr/bin/python3", exeName);
            case "javascript":
                return Arrays.asList("/usr/bin/node", exeName);
            default:
                return Arrays.asList("./" + exeName);
        }
    }

    /**
     * 获取环境变量
     */
    private List<String> getEnvs(String language) {
        List<String> envs = new ArrayList<>();
        envs.add("PATH=/usr/bin:/bin");

        if ("java".equals(language)) {
            envs.add("CLASSPATH=/");
        }

        // 添加通用环境变量
        envs.add("LANG=en_US.UTF-8");
        envs.add("LC_ALL=en_US.UTF-8");
        envs.add("LANGUAGE=en_US:en");

        return envs;
    }
}
