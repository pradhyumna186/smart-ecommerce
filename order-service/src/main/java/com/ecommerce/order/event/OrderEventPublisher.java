package com.ecommerce.order.event;

import com.ecommerce.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishOrderCreated(Order order) {
        publisher.publishEvent(OrderEvent.fromOrder("ORDER_CREATED", order));
    }

    public void publishOrderStatusChanged(Order order) {
        publisher.publishEvent(OrderEvent.fromOrder("ORDER_STATUS_CHANGED", order));
    }

    public void publishOrderCancelled(Order order) {
        publisher.publishEvent(OrderEvent.fromOrder("ORDER_CANCELLED", order));
    }
}
