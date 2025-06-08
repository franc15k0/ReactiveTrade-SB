package com.ecommerce.product.event;

import com.ecommerce.events.OrderEvent;
import com.ecommerce.events.ProductEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final StreamBridge streamBridge;
    private static final Logger log = LoggerFactory.getLogger(ProductEventProducer.class);
    public Mono<Void> send(ProductEvent event) {
        Message<ProductEvent> message = MessageBuilder.withPayload(event)
                .setHeader("eventType", event.getEventType().toString())
                .setHeader("orderId", event.getOrderId().toString())
                .build();
        return Mono.fromCallable(() -> {
            boolean ok = streamBridge.send("productEvents-out-0", message);
            log.info("📤 Producto enviado al topic: {}, éxito={}", event.getEventType(), ok);
            return ok;
        }).then();
    }
}