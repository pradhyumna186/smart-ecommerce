package com.ecommerce.order.dto;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private Order order;
    private List<OrderItem> items;
}
