package com.ecommerce.order.event;

import com.ecommerce.events.*;
import com.ecommerce.order.repository.OrderSagaStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;
import reactor.core.publisher.Mono;
@Configuration
@RequiredArgsConstructor
public class PaymentEventConsumer {
    private final OrderSagaStateRepository repository;
    private final OrderEventProducer orderEventProducer;
    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    @Bean
    public Consumer<PaymentEvent> paymentResponseEvents() {
        return event -> {
            log.info("💳 [AUDIT] Recibido PaymentEvent: {} para orderId {}", event.getEventType(), event.getOrderId());
            if (event.getEventType() == PaymentEventType.PAYMENT_PROCESSED) {
                repository.findById(UUID.fromString(event.getOrderId().toString()))
                        .flatMap(order -> {
                            order.setStatus("COMPLETED");
                            return repository.save(order)
                                    .doOnSuccess(saved -> {
                                        log.info("✅ Estado actualizado a {}", saved.getStatus());

                                            OrderEvent followUp = OrderEvent.newBuilder()
                                                    .setEventId(UUID.randomUUID().toString())
                                                    .setEventType(OrderEventType.ORDER_COMPLETED)
                                                    .setOrderId(order.getOrderId().toString())
                                                    .setCustomerId(order.getCustomerId())
                                                    .setTotalAmount(order.getTotalAmount())
                                                    .setItems(Collections.emptyList()) // opcional
                                                    .setTimestamp(Instant.now().toEpochMilli())
                                                    .setSagaId(order.getSagaId())
                                                    .build();
                                            orderEventProducer.send(followUp).subscribe();
                                    });
                        })
                        .doOnError(err -> log.error("❌ Error al manejar PaymentEvent", err))
                        .subscribe();
            } else if (event.getEventType() == PaymentEventType.PAYMENT_FAILED) {
                repository.findById(UUID.fromString(event.getOrderId().toString()))
                        .flatMap(order -> {
                            order.setStatus("CANCELLED");
                            OrderEvent cancelledEvent = OrderEvent.newBuilder()
                                    .setEventId(UUID.randomUUID().toString())
                                    .setEventType(OrderEventType.ORDER_CANCELLED)
                                    .setOrderId(order.getOrderId().toString())
                                    .setCustomerId(order.getCustomerId())
                                    .setTotalAmount(order.getTotalAmount())
                                    .setItems(null)
                                    .setTimestamp(System.currentTimeMillis())
                                    .setSagaId(order.getSagaId())
                                    .build();
                            return repository.save(order)
                                    .then(orderEventProducer.send(cancelledEvent));
                        })
                        .doOnError(e -> log.error("❌ Error cancelando la orden", e))
                        .subscribe();
            } else {
                log.debug("🔁 Ignorado PaymentEvent tipo {}", event.getEventType());
            }
        };
    }
}
