package com.ecommerce.payment.event;

import com.ecommerce.events.OrderEvent;
import com.ecommerce.events.PaymentEvent;
import com.ecommerce.events.PaymentEventType;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentService paymentService;
    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);
    @Bean
    public Consumer<PaymentEvent> paymentRequestEvents() {
        return event -> {
            log.info("📨 [AUDIT] Recibido OrderEvent: {}", event.getEventType());
            if (event.getEventType() == PaymentEventType.PAYMENT_REQUESTED) {
                log.info("💰 [AUDIT] Procesando pago para orderId: {}", event.getOrderId());
                paymentService.processPayment(event).subscribe();
            }else {
                log.debug("🔁 Ignorado PaymentEvent tipo {}", event.getEventType());
            }
        };
    }
}
