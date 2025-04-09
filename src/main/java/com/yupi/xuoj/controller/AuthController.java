package com.yupi.xuoj.controller;

import cn.hutool.core.util.PhoneUtil;
import com.yupi.xuoj.common.BaseResponse;
import com.yupi.xuoj.common.ErrorCode;
import com.yupi.xuoj.common.ResultUtils;
import com.yupi.xuoj.utils.SmsUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Api("短信登录")
public class AuthController {
    @Autowired
    private SmsUtils smsUtils;

    // 发送短信验证码接口
    @GetMapping("/sendCode")
    @ApiOperation("发送验证码")
    public BaseResponse<String> sendCode(@RequestParam String phone) {
        // 简单校验手机号格式
        if (!PhoneUtil.isPhone(phone)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "手机号格式错误");
        }

        // 发送短信
        smsUtils.sendCode(phone);
        return ResultUtils.success("验证码已发送");
    }
}