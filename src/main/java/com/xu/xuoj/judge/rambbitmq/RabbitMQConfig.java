package com.xu.xuoj.judge.rambbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange codeExchange() {
        return new DirectExchange("code_exchange", true, false);
    }

    @Bean
    public Queue codeQueue() {
        return new Queue("code_queue", true);
    }

    @Bean
    public Queue gameCodeQueue() {
        return new Queue("gameCodeQueue", true);
    }

    @Bean
    public Binding codeBinding(Queue codeQueue, DirectExchange codeExchange) {
        return BindingBuilder.bind(codeQueue).to(codeExchange).with("my_routingKey");
    }

    @Bean
    public DirectExchange gameExchange() {
        return new DirectExchange("game_exchange", true, false);
    }

    @Bean
    Queue gameQueue() {
        return new Queue("game_queue", true);
    }

    @Bean
    public Binding gameBindding(Queue gameQueue, DirectExchange gameExchange) {
        return BindingBuilder.bind(gameQueue).to(gameExchange).with("game_routingKey");
    }
}