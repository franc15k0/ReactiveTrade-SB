package com.ecommerce.order.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;


@Table("order_saga_state")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSagaState {
    @Id
    private UUID orderId;
    private String customerId;
    private Double totalAmount;
    private String status;
    private String sagaId;
    private Long createdAt;
}
