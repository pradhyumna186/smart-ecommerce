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
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${user.service.basic-username:admin}")
    private String basicUsername;

    @Value("${user.service.basic-password:admin123}")
    private String basicPassword;

    public Mono<Map<String, Object>> getUserById(Long userId) {
        return webClientBuilder.baseUrl(userServiceUrl).build()
                .get()
                .uri("/api/users/{id}", userId)
                .headers(h -> h.setBasicAuth(basicUsername, basicPassword))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class)
                .map(m -> (Map<String, Object>) m);
    }

    public Mono<Boolean> validateUserExists(Long userId) {
        return getUserById(userId)
                .map(u -> u != null && u.get("id") != null)
                .onErrorReturn(false);
    }
}
