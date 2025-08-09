package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.client.UserServiceClient;
import com.ecommerce.order.event.OrderEventPublisher;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public Order createOrder(Order order) {
        boolean userExists = userServiceClient.validateUserExists(order.getUserId()).blockOptional().orElse(false);
        if (!userExists) {
            throw new RuntimeException("User not found");
        }

        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.ZERO);
        Order saved = orderRepository.save(order);

        eventPublisher.publishOrderCreated(saved);
        return saved;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order updateOrder(Long id, Order patch) {
        Order order = getOrderById(id);

        if (patch.getStatus() != null)
            order.setStatus(patch.getStatus());
        if (patch.getShippingAddress() != null)
            order.setShippingAddress(patch.getShippingAddress());
        if (patch.getCustomerName() != null)
            order.setCustomerName(patch.getCustomerName());
        if (patch.getCustomerEmail() != null)
            order.setCustomerEmail(patch.getCustomerEmail());
        if (patch.getCustomerPhone() != null)
            order.setCustomerPhone(patch.getCustomerPhone());

        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order addOrderItem(Long orderId, OrderItem item) {
        Order order = getOrderById(orderId);

        boolean inStock = productServiceClient.validateProductStock(item.getProductId(), item.getQuantity())
                .blockOptional().orElse(false);
        if (!inStock) {
            throw new RuntimeException("Insufficient stock for productId=" + item.getProductId());
        }

        if (item.getTotalPrice() == null && item.getUnitPrice() != null && item.getQuantity() != null) {
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        item.setOrder(order);
        orderItemRepository.save(item);

        BigDecimal newTotal = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(newTotal);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = getOrderById(id);
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        eventPublisher.publishOrderStatusChanged(updated);
        return updated;
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
