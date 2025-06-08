package com.ecommerce.product.service;

import com.ecommerce.events.OrderEvent;
import com.ecommerce.events.OrderItem;
import com.ecommerce.events.ProductEvent;
import com.ecommerce.events.ProductEventType;
import com.ecommerce.product.event.ProductEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
//@Slf4j
public class ProductService {

    private final ProductEventProducer producer;
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    public Mono<Void> processOrder(OrderEvent orderEvent) {
        log.info("Verificando stock para la orden: {}", orderEvent.getOrderId());
        boolean stockDisponible = verificarStock(orderEvent);
        OrderItem item = orderEvent.getItems().get(0); // Simplificamos al primer producto
        ProductEvent event = ProductEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOrderId(orderEvent.getOrderId())
                .setProductId(item.getProductId())
                .setQuantity(item.getQuantity())
                .setEventType(stockDisponible ? ProductEventType.STOCK_RESERVED : ProductEventType.STOCK_INSUFFICIENT)
                .setTimestamp(Instant.now().toEpochMilli())
                .setSagaId(orderEvent.getSagaId())
                .setReason(stockDisponible ? null : "Stock insuficiente")
                .build();

        log.info("Enviando evento de producto: {}", event.getEventType());

        return producer.send(event);
    }

    // Simulación simple: siempre hay stock
    private boolean verificarStock(OrderEvent orderEvent) {
        return orderEvent.getItems().stream()
                .allMatch(this::hayStockDisponible);
    }

    private boolean hayStockDisponible(OrderItem item) {
        return item.getQuantity() <= 10; // ejemplo: stock mínimo ficticio
    }
}