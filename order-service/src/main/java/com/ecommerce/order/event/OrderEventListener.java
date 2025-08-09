package com.ecommerce.order.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventListener {

    @EventListener
    public void handleOrderEvent(OrderEvent event) {
        log.info("Order Event: type={}, orderId={}, orderNumber={}, status={}, userId={}, ts={}",
                event.getEventType(), event.getOrderId(), event.getOrderNumber(),
                event.getStatus(), event.getUserId(), event.getTimestamp());
    }
}
