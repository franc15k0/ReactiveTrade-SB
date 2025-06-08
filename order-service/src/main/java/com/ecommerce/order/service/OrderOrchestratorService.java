package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.entity.OrderSagaState;
import com.ecommerce.order.event.OrderEventProducer;
import com.ecommerce.events.OrderEvent;
import com.ecommerce.events.OrderEventType;
import com.ecommerce.events.OrderItem;
import lombok.RequiredArgsConstructor;
import com.ecommerce.order.repository.OrderSagaStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(OrderOrchestratorService.class);
    private final OrderEventProducer producer;
    private final OrderSagaStateRepository repository;
    public Mono<Void> processOrder(OrderRequest request) {
        String eventId = UUID.randomUUID().toString();
        String sagaId  = UUID.randomUUID().toString();
        double totalAmount = request.getItems().stream()
                .mapToDouble(i -> i.getQuantity() * i.getUnitPrice())
                .sum();

        List<OrderItem> items = request.getItems().stream()
                .map(i -> OrderItem.newBuilder()
                        .setProductId(i.getProductId())
                        .setQuantity(i.getQuantity())
                        .setUnitPrice(i.getUnitPrice())
                        .setTotalPrice(i.getQuantity() * i.getUnitPrice())
                        .build())
                .toList();
        OrderSagaState state = OrderSagaState.builder()
                .customerId(request.getCustomerId())
                .totalAmount(totalAmount)
                .status("CREATED")
                .sagaId(sagaId)
                .createdAt(Instant.now().toEpochMilli())
                .build();
        return repository.save(state)
                .doOnSuccess(saved -> log.info("📝 [AUDIT] Registro persistido: {}", saved))
                .flatMap(saved -> {
                    OrderEvent event = OrderEvent.newBuilder()
                            .setEventId(eventId)
                            .setEventType(OrderEventType.ORDER_CREATED)
                            .setOrderId(saved.getOrderId().toString())
                            .setCustomerId(request.getCustomerId())
                            .setTotalAmount(totalAmount)
                            .setItems(items)
                            .setTimestamp(System.currentTimeMillis())
                            .setSagaId(sagaId)
                            .build();
                    log.info("📤 [AUDIT] Enviando OrderEvent: {}", event);
                    return producer.send(event);
                });
    }
}