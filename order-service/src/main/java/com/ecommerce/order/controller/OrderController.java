package com.ecommerce.order.controller;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderStatusResponse;
import com.ecommerce.order.repository.OrderSagaStateRepository;
import com.ecommerce.order.service.OrderOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderOrchestratorService orderService;
    private final OrderSagaStateRepository repository;
    @PostMapping
    public Mono<Void> createOrder(@RequestBody OrderRequest request) {
        return orderService.processOrder(request);
    }
    @GetMapping("/{orderId}")
    public Mono<OrderStatusResponse> getOrderStatus(@PathVariable String orderId) {
        return repository.findById(UUID.fromString(orderId))
                .map(order -> new OrderStatusResponse(
                        order.getOrderId().toString(),
                        order.getStatus(),
                        order.getSagaId()
                ));
    }
}
