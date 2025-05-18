package com.xu.xuoj.controller;

import cn.hutool.core.lang.Validator;
import com.xu.xuoj.common.BaseResponse;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.common.ResultUtils;
import com.xu.xuoj.utils.SmsUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;


@RestController
@RequestMapping("/auth")
@Api("邮箱验证码")
public class AuthController {


    @Resource
    private SmsUtils smsUtils;

    // 发送短信验证码接口
    @GetMapping("/sendCode")
    @ApiOperation("发送验证码")
    public BaseResponse<String> sendCode(@RequestParam String email) {
        // 简单校验手机号格式
        if (!Validator.isEmail(email)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }
        // 发送短信
        smsUtils.sendCode(email);
        return ResultUtils.success("验证码已发送");
    }
}