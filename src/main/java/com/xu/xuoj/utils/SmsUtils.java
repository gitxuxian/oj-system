package com.xu.xuoj.utils;

import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SmsUtils {

    @Autowired
    private JavaMailSender mailSender;

    // 获取发件人邮箱
    @Value("${spring.mail.username}")
    private String sender;

    // 获取发件人昵称
    @Value("${spring.mail.nickname}")
    private String nickname;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 发送短信验证码
    public void sendCode(String email) {
        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);
        // 存入Redis（5分钟过期）
        redisTemplate.opsForValue().set(
            "email_code:" + email,
            code,
            1, TimeUnit.MINUTES
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(nickname + '<' + sender + '>');
        message.setTo(email);
        message.setSubject("欢迎访问我的OJ网站");


        String content = "【验证码】尊敬的用户，您的验证码为：" + code + "\n\n"
            + "验证码有效期为5分钟，请勿泄露给他人。\n\n"
            + "───────────────────────────\n"
            + "此为系统自动发送，请勿回复";

        message.setText(content);

        mailSender.send(message);

    }
}