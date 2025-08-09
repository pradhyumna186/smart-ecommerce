package com.ecommerce.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
@CrossOrigin
public class GatewayController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @GetMapping("/services")
    public ResponseEntity<List<String>> services() {
        return ResponseEntity.ok(List.of("product-service", "user-service", "order-service"));
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "name", "API Gateway",
                "routes", 3));
    }
}
