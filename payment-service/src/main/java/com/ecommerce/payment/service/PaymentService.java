package com.ecommerce.payment.service;

import com.ecommerce.events.*;
import com.ecommerce.payment.event.PaymentEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.cloud.stream.function.StreamBridge;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentEventProducer producer;

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    public Mono<Void> processPayment(PaymentEvent event) {
        log.info("[AUDIT] Procesando Payment: {}", event);

        PaymentEvent response = PaymentEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType(PaymentEventType.PAYMENT_PROCESSED)
                .setPaymentId(UUID.randomUUID().toString())
                .setOrderId(event.getOrderId())
                .setAmount(event.getAmount())
                .setPaymentMethod(event.getPaymentMethod())
                .setTransactionId(UUID.randomUUID().toString())
                .setTimestamp(Instant.now().toEpochMilli())
                .setSagaId(event.getSagaId())
                .build();

        log.info("📤 Enviando evento de pago: {}", response.getEventType());
        return producer.send(response);
    }
}