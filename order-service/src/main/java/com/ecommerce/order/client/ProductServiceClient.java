package com.ecommerce.order.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${product.service.basic-username:admin}")
    private String basicUsername;

    @Value("${product.service.basic-password:admin123}")
    private String basicPassword;

    public Mono<Map<String, Object>> getProductById(String productId) {
        return webClientBuilder.baseUrl(productServiceUrl).build()
                .get()
                .uri("/api/products/{id}", productId)
                .headers(h -> h.setBasicAuth(basicUsername, basicPassword))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class)
                .map(m -> (Map<String, Object>) m);
    }

    public Mono<Boolean> validateProductStock(String productId, Integer requestedQty) {
        return getProductById(productId)
                .map(p -> {
                    Object stock = p.get("stockQuantity");
                    if (stock instanceof Number n) {
                        return n.intValue() >= requestedQty;
                    }
                    return false;
                })
                .onErrorReturn(false);
    }
}
