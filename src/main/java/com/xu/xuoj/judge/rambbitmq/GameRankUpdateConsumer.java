package com.xu.xuoj.judge.rambbitmq;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.xu.xuoj.model.entity.GameSubmissionMessage;
import com.xu.xuoj.service.GameRankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 比赛排行榜更新消息消费者
 * @author xu
 */
@Component
@Slf4j
public class GameRankUpdateConsumer {

    @Resource
    private GameRankService gameRankService;

    /**
     * 处理比赛提交消息，更新排行榜
     */
    @RabbitListener(queues = "game_queue")
    public void handleGameSubmissionMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("收到比赛提交消息: {}", message);
        
        try {
            // 解析消息
            GameSubmissionMessage submissionMessage = JSONUtil.toBean(message, GameSubmissionMessage.class);
            
            // 处理排行榜更新逻辑
            // 这里可以添加更复杂的业务逻辑，比如：
            // 1. 验证比赛状态
            // 2. 验证用户权限
            // 3. 更新排行榜缓存
            // 4. 发送通知等
            
            Long gameId = submissionMessage.getGameId();
            Long userId = submissionMessage.getUserId();
            
            log.info("处理比赛 {} 用户 {} 的提交，更新排行榜", gameId, userId);
            
            // 计算并更新排行榜
            boolean success = gameRankService.calculateRankings(gameId);
            
            if (success) {
                log.info("比赛排行榜更新成功, gameId: {}, userId: {}", gameId, userId);
                // 手动确认消息
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("比赛排行榜更新失败, gameId: {}, userId: {}", gameId, userId);
                // 拒绝消息并重新入队
                channel.basicNack(deliveryTag, false, true);
            }
            
        } catch (Exception e) {
            log.error("处理比赛提交消息失败: {}", message, e);
            try {
                // 拒绝消息并重新入队
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                log.error("拒绝消息失败", ex);
            }
        }
    }
} 