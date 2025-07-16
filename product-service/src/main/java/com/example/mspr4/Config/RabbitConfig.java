package com.example.mspr4.Config;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String ORDER_QUEUE = "order_events";
    public static final String STOCK_QUEUE = "stock_events";

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true); // durable = true
    }

    @Bean
    public Queue stockQueue() {
        return new Queue(STOCK_QUEUE, true);
    }
}