package com.ecommerce.payment.event;

import com.ecommerce.events.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {
    private final StreamBridge streamBridge;

    public Mono<Void> send(PaymentEvent event) {
        return Mono.fromCallable(() -> streamBridge.send("paymentEvents-out-0", MessageBuilder.withPayload(event).build()))
                .flatMap(success -> success
                        ? Mono.empty()
                        : Mono.error(new RuntimeException("❌ Error al enviar PaymentEvent")));
    }
}
