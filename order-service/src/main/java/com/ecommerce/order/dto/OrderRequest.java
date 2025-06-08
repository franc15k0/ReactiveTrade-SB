package com.ecommerce.order.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String customerId;
    List<OrderItemDTO> items;
}