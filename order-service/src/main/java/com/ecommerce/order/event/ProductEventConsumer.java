package com.ecommerce.order.event;

import com.ecommerce.events.*;
import com.ecommerce.order.repository.OrderSagaStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import reactor.core.publisher.Mono;
@Configuration
@RequiredArgsConstructor
public class ProductEventConsumer {
    private final OrderEventProducer producer;
    private final OrderSagaStateRepository repository;
    private static final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);
    private final PaymentEventProducer paymentEventProducer;
    private final OrderEventProducer orderEventProducer;
    @Bean
    public Consumer<ProductEvent> productEvents() {
        return productEvent -> {
            log.info("📦 [AUDIT] ProductEvent recibido: {} para orderId {}", productEvent.getEventType(), productEvent.getOrderId());
            if (productEvent.getEventType() == ProductEventType.STOCK_RESERVED) {
                repository.findById(UUID.fromString(productEvent.getOrderId().toString()))
                        .flatMap(order -> {
                            order.setStatus("CONFIRMED");
                            return repository.save(order);
                        })
                        .doOnSuccess(order -> {
                                    log.info("✅ Estado actualizado a {}", order.getStatus());

                                        PaymentEvent paymentEvent = PaymentEvent.newBuilder()
                                                .setEventId(UUID.randomUUID().toString())
                                                .setPaymentId(UUID.randomUUID().toString())
                                                .setEventType(PaymentEventType.PAYMENT_REQUESTED)
                                                .setOrderId(productEvent.getOrderId().toString())
                                                .setAmount(order.getTotalAmount())
                                                .setPaymentMethod("CARD") // o extraer del contexto
                                                .setTimestamp(System.currentTimeMillis())
                                                .setSagaId(order.getSagaId().toString())
                                                .build();
                                        paymentEventProducer.send(paymentEvent)
                                                .subscribe();

                                }
                        )
                        .switchIfEmpty(Mono.fromRunnable(() ->
                                log.warn("⚠️ No se encontró la orden con ID: {}. No se puede actualizar.", productEvent.getOrderId())
                        ))
                        .doOnError(e -> log.error("❌ Error actualizando estado", e))
                        .subscribe();
            }
            else if (productEvent.getEventType() == ProductEventType.STOCK_INSUFFICIENT) {
                repository.findById(UUID.fromString(productEvent.getOrderId().toString()))
                        .flatMap(order -> {
                            order.setStatus("CANCELLED");
                            OrderEvent cancelEvent = OrderEvent.newBuilder()
                                    .setEventId(UUID.randomUUID().toString())
                                    .setEventType(OrderEventType.ORDER_CANCELLED)
                                    .setOrderId(order.getOrderId().toString())
                                    .setCustomerId(order.getCustomerId())
                                    .setTotalAmount(order.getTotalAmount())
                                    .setItems(List.of()) // puedes incluirlo si es necesario
                                    .setTimestamp(System.currentTimeMillis())
                                    .setSagaId(order.getSagaId())
                                    .build();
                            return repository.save(order)
                                   // .then(orderEventProducer.send(cancelEvent));
                                    .flatMap(savedOrder -> orderEventProducer.send(cancelEvent).thenReturn(savedOrder));
                        })
                        .doOnSuccess(saveOrder -> log.info("🛑 Orden cancelada por falta de stock: {}", saveOrder.getOrderId()))
                        .doOnError(err -> log.error("❌ Error cancelando la orden", err))
                        .subscribe();
            } else {
                log.debug("🔁 Ignorado ProductEvent tipo {}", productEvent.getEventType());
            }
        };
    }
}