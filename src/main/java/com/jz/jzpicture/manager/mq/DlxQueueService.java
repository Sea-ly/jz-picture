package com.jz.jzpicture.manager.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description:
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager.mq
 * @Project: jz-picture
 * @Date: 2025/3/2  0:17
 */
@Component
@Slf4j
public class DlxQueueService {

    @RabbitListener(queues = RabbitMQConfig.DLX_QUEUE)
    public void handleDlxMessage(SpaceQuotaUpdateMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.error("处理死信消息: {}", message);
        channel.basicAck(tag, false); // 确认消息1
    }
}