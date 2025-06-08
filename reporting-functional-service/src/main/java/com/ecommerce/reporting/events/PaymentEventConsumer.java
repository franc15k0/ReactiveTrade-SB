package com.ecommerce.reporting.events;
import com.ecommerce.events.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class PaymentEventConsumer {

    private static void accept(PaymentEvent event) {
        log.info("🔹 Event ID: {}", event.getEventId());
        log.info("🔹 Event Type: {}", event.getEventType());
        log.info("🔹 Payment ID: {}", event.getPaymentId());
        log.info("🔹 Order ID: {}", event.getOrderId());
        log.info("🔹 Amount: {}", event.getAmount());
        log.info("🔹 Method: {}", event.getPaymentMethod());
        log.info("🔹 Transaction ID: {}", event.getTransactionId() != null ? event.getTransactionId() : "N/A");
        log.info("🔹 Timestamp: {}", event.getTimestamp());
        log.info("🔹 Saga ID: {}", event.getSagaId() != null ? event.getSagaId() : "N/A");
        log.info("🔹 Failure Reason: {}", event.getFailureReason() != null ? event.getFailureReason() : "N/A");
        log.info("--------------------------------------------------");
    }

    @Bean
    public Consumer<PaymentEvent> paymentRequestReport() {
        log.info("💳 [paymentRequest Report]");
        return PaymentEventConsumer::accept;
    }
    @Bean
    public Consumer<PaymentEvent> paymentResponseReport() {
        log.info("💳 [paymentResponseReport Report]");
        return PaymentEventConsumer::accept;
    }
}