package com.ecommerce.product.event;

import com.ecommerce.events.OrderEvent;
import com.ecommerce.events.OrderEventType;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductService productService;
    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    @Bean
    public Consumer<OrderEvent> orderEvents() {
        return event -> {
            log.info("📨 [AUDIT] Recibido OrderEvent: {}", event.getOrderId());
            if (event.getEventType() == OrderEventType.ORDER_CREATED) {
                productService.processOrder(event)
                        .doOnSuccess(v -> log.info("✅ [AUDIT] ProductEvent enviado"))
                        .doOnError(e -> log.error("❌ Error al procesar la orden", e))
                        .subscribe();
            } else {
                log.debug("🔁 Ignorado OrderEvent tipo {}", event.getEventType());
            }
        };
    }
}