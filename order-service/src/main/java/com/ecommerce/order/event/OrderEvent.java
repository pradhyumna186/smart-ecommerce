package com.ecommerce.order.event;

import com.ecommerce.order.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String eventType;
    private Long orderId;
    private String orderNumber;
    private String status;
    private Long userId;
    private Instant timestamp;

    public static OrderEvent fromOrder(String type, Order order) {
        return new OrderEvent(
                type,
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getUserId(),
                Instant.now());
    }
}
