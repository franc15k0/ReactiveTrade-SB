package com.ecommerce.reporting.events;
import com.ecommerce.events.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class ProductEventConsumer {
    @Bean
    public Consumer<ProductEvent> productReport() {
        return event -> {
            log.info("📦 [Product Report]");
            log.info("🔹 Event ID: {}", event.getEventId());
            log.info("🔹 Event Type: {}", event.getEventType());
            log.info("🔹 Product ID: {}", event.getProductId());
            log.info("🔹 Quantity: {}", event.getQuantity());
            log.info("🔹 Order ID: {}", event.getOrderId() != null ? event.getOrderId() : "N/A");
            log.info("🔹 Timestamp: {}", event.getTimestamp());
            log.info("🔹 Saga ID: {}", event.getSagaId() != null ? event.getSagaId() : "N/A");
            log.info("🔹 Available Stock: {}", event.getAvailableStock() != null ? event.getAvailableStock() : "N/A");
            log.info("🔹 Reason: {}", event.getReason() != null ? event.getReason() : "N/A");
            log.info("--------------------------------------------------");
        };
    }
}
