package com.yupi.xuoj.utils;

import cn.hutool.core.util.RandomUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.yupi.xuoj.config.AliyunSmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SmsUtils {
    @Autowired
    private IAcsClient acsClient;
    @Autowired
    private AliyunSmsConfig smsConfig;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 发送短信验证码
    public void sendCode(String phone) {
        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(4);

        // 存入Redis（5分钟过期）
        redisTemplate.opsForValue().set(
            "sms_code:" + phone,
            code,
            1, TimeUnit.MINUTES
        );

        // 构建短信请求
        CommonRequest request = new CommonRequest();
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", smsConfig.getSignName());
        request.putQueryParameter("TemplateCode", smsConfig.getTemplateCode());
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");

        try {
            CommonResponse response = acsClient.getCommonResponse(request);
            System.out.println("短信发送结果：" + response.getData());
        } catch (ClientException e) {
            throw new RuntimeException("短信发送失败", e);
        }
    }

    // 验证短信码
    public boolean verifyCode(String phone, String code) {
        String storedCode = redisTemplate.opsForValue().get("sms_code:" + phone);
        return code.equals(storedCode);
    }
}