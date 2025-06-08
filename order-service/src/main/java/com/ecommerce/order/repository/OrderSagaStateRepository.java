package com.ecommerce.order.repository;

import com.ecommerce.order.entity.OrderSagaState;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface OrderSagaStateRepository extends ReactiveCrudRepository<OrderSagaState, UUID> {
}
