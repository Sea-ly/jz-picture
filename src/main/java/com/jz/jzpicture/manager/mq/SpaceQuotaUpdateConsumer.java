package com.jz.jzpicture.manager.mq;

import com.jz.jzpicture.service.SpaceService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 消息消费者
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager.mq
 * @Project: jz-picture
 * @Date: 2025/3/1  23:51
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpaceQuotaUpdateConsumer {

    private final SpaceService spaceService;
    private final RedisTemplate<String, String> redisTemplate;

    @RabbitListener(queues = RabbitMQConfig.SPACE_UPDATE_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void handleQuotaUpdate(SpaceQuotaUpdateMessage message,
                                  Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        String redisKey = "space:quota:op:" + message.getOperationId();
        try {
            // 幂等性校验
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 1, TimeUnit.HOURS))) {
                // 执行带乐观锁的更新
                boolean success = spaceService.updateQuotaWithLock(
                        message.getSpaceId(),
                        message.getSizeDelta(),
                        message.getCountDelta()
                );

                if (!success) {
                    throw new RuntimeException("空间额度更新失败");
                }
                channel.basicAck(tag, false); // 确认消息
            } else {
                log.warn("重复消息已忽略，operationId: {}", message.getOperationId());
                channel.basicAck(tag, false); // 确认但不处理
            }
        } catch (Exception e) {
            // 异常时删除 Redis 中的 operationId，确保重试时能重新处理
            redisTemplate.delete(redisKey);
            log.error("处理额度更新异常，operationId: {}", message.getOperationId(), e);
            throw e; // 抛出异常，触发重试
        }
        }
    }