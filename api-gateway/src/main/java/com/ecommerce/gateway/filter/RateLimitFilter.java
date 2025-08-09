package com.ecommerce.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final int LIMIT = 100;
    private static final long WINDOW_MS = 60_000;

    private final Map<String, Window> counters = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        Window w = counters.computeIfAbsent(ip, k -> new Window(Instant.now().toEpochMilli(), 0));
        long now = Instant.now().toEpochMilli();
        if (now - w.start >= WINDOW_MS) {
            w.start = now;
            w.count = 0;
        }
        w.count++;
        log.debug("Request count for IP {}: {}/{}", ip, w.count, LIMIT);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -2;
    }

    private static class Window {
        volatile long start;
        volatile int count;

        Window(long s, int c) {
            start = s;
            count = c;
        }
    }
}
