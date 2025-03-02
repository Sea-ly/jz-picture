package com.jz.jzpicture.manager.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;

/**
 * @Description:RabbitMQ配置类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager.mq
 * @Project: jz-picture
 * @Date: 2025/3/1  23:42
 */
// config/RabbitMQConfig.java
@Configuration
public class RabbitMQConfig {
    @Lazy
    @Resource
    private RabbitTemplate rabbitTemplate;

    // 空间额度更新队列配置
    public static final String SPACE_UPDATE_QUEUE = "space.update.queue";
    public static final String SPACE_UPDATE_EXCHANGE = "space.update.exchange";
    public static final String SPACE_UPDATE_ROUTING_KEY = "space.update";
    // 死信队列配置
    public static final String DLX_EXCHANGE = "dlx.space.update.exchange";
    public static final String DLX_QUEUE = "dlx.space.update.queue";
    public static final String DLX_ROUTING_KEY = "dlx.space.update.key";

    // 定义JSON消息转换器（重要！）
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 声明队列
    @Bean
    public Queue spaceQuotaUpdateQueue() {
        return QueueBuilder.durable(SPACE_UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }

    // 声明直连交换机
    @Bean
    public DirectExchange spaceQuotaUpdateExchange() {
        return new DirectExchange(SPACE_UPDATE_EXCHANGE);
    }

    // 绑定队列到交换机
    @Bean
    public Binding spaceQuotaBinding() {
        return BindingBuilder.bind(spaceQuotaUpdateQueue())
                .to(spaceQuotaUpdateExchange())
                .with(SPACE_UPDATE_ROUTING_KEY);
    }

    // 死信队列
    @Bean
    public Queue dlxQueue() {
        return new Queue(DLX_QUEUE, true);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue())
                .to(dlxExchange())
                .with(DLX_ROUTING_KEY);
    }
    @Bean
    public MessageRecoverer customMessageRecoverer() {
        return new RepublishMessageRecoverer(
                rabbitTemplate, // 注入 RabbitTemplate
                DLX_EXCHANGE,    // 死信交换机
                DLX_ROUTING_KEY       // 死信路由键
        );
    }
}
