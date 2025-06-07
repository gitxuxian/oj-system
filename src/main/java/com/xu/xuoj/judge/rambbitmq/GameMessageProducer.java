package com.xu.xuoj.judge.rambbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class GameMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String routingKey, String exchange, String message) {
        //发送消息到指定的交换机，再到指定的路由键
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
