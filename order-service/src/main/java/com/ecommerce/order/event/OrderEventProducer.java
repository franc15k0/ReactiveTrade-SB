package com.ecommerce.order.event;

import com.ecommerce.events.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {


    private final StreamBridge streamBridge;

    public Mono<Void> send(OrderEvent event) {
        Message<OrderEvent> message = MessageBuilder.withPayload(event)
                .setHeader("eventType", event.getEventType().toString())
                .setHeader("orderId", event.getOrderId().toString())
                .build();
        return Mono.fromCallable(() -> streamBridge.send("orderEvents-out-0", message))
                .flatMap(success -> success
                        ? Mono.empty()
                        : Mono.error(new RuntimeException("Failed to send OrderEvent to Kafka")));
    }
}