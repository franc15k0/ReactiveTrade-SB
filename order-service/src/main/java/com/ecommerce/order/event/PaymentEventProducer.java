package com.ecommerce.order.event;

import com.ecommerce.events.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {
    private final StreamBridge streamBridge;

    public Mono<Void> send(PaymentEvent event) {
        Message<PaymentEvent> message = MessageBuilder.withPayload(event).build();
        return Mono.fromCallable(() -> streamBridge.send("paymentEvents-out-0", message))
                .flatMap(success -> {
                    if (success) {
                        log.info("📤 PAYMENT_REQUESTED enviado: {}", event.getPaymentId());
                        return Mono.empty();
                    } else {
                        return Mono.error(new RuntimeException("❌ No se pudo enviar el PaymentEvent"));
                    }
                });
    }
}
