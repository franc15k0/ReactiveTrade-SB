package com.ecommerce.order.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class OrderStatusResponse {
    private String orderId;
    private String status;
    private String sagaId;
}