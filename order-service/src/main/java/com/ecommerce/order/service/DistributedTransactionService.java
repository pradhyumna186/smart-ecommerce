package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.client.UserServiceClient;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DistributedTransactionService {

    private final OrderService orderService;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;

    @Transactional
    public Order createOrderWithValidation(Order order, List<OrderItem> items) {

        CompletableFuture<Boolean> userValidFut = userServiceClient.validateUserExists(order.getUserId())
                .defaultIfEmpty(false).toFuture();

        CompletableFuture<Boolean> productsValidFut = CompletableFuture.supplyAsync(() -> items.stream()
                .allMatch(item -> productServiceClient.validateProductStock(item.getProductId(), item.getQuantity())
                        .onErrorReturn(false)
                        .blockOptional().orElse(false)));

        boolean userOk = userValidFut.join();
        boolean productsOk = productsValidFut.join();

        if (!userOk)
            throw new RuntimeException("User not found");
        if (!productsOk)
            throw new RuntimeException("One or more products have insufficient stock");

        Order created = orderService.createOrder(order);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem it : items) {
            Order updated = orderService.addOrderItem(created.getId(), it);
            total = updated.getTotalAmount();
        }

        created.setTotalAmount(total);
        return created;
    }
}
