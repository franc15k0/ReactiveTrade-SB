package com.ecommerce.reporting.events;

import com.ecommerce.events.OrderEvent;
import com.ecommerce.events.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
@Slf4j
@Component
public class OrderEventConsumer {

    @Bean
    public Consumer<OrderEvent> orderReport() {
        return event -> {
            log.info("🧾 [Order Report]");
            log.info("🔹 Event ID: {}", event.getEventId());
            log.info("🔹 Event Type: {}", event.getEventType());
            log.info("🔹 Order ID: {}", event.getOrderId());
            log.info("🔹 Customer ID: {}", event.getCustomerId() != null ? event.getCustomerId() : "N/A");
            log.info("🔹 Total Amount: {}", event.getTotalAmount());
            log.info("🔹 Timestamp: {}", event.getTimestamp());
            log.info("🔹 Saga ID: {}", event.getSagaId() != null ? event.getSagaId() : "N/A");
            log.info("📦 Items:");
            for (OrderItem item : event.getItems()) {
                log.info("   • Product: {} | Qty: {} | Unit: {} | Total: {}",
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice());
            }
            log.info("--------------------------------------------------");
        };
    }
}