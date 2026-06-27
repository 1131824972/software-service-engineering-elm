package com.ynu.elm.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.Map;

@RestController
public class FallbackController {
    @RequestMapping("/userFallBack")
    public ResponseEntity<Map<String, Object>> userFallBack(ServerWebExchange exchange) {
        return fallback(exchange, "用户服务当前异常，请稍后再试");
    }

    @RequestMapping("/businessFallBack")
    public ResponseEntity<Map<String, Object>> businessFallBack(ServerWebExchange exchange) {
        return fallback(exchange, "商家服务当前异常，请稍后再试");
    }

    @RequestMapping("/cartFallBack")
    public ResponseEntity<Map<String, Object>> cartFallBack(ServerWebExchange exchange) {
        return fallback(exchange, "购物车服务当前异常，请稍后再试");
    }

    @RequestMapping("/addressFallBack")
    public ResponseEntity<Map<String, Object>> addressFallBack(ServerWebExchange exchange) {
        return fallback(exchange, "地址服务当前异常，请稍后再试");
    }

    @RequestMapping("/orderFallBack")
    public ResponseEntity<Map<String, Object>> orderFallBack(ServerWebExchange exchange) {
        return fallback(exchange, "订单服务当前异常，请稍后再试");
    }

    private ResponseEntity<Map<String, Object>> fallback(ServerWebExchange exchange, String message) {
        String path = exchange.getRequest().getURI().getPath();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "code", 503,
                        "message", message,
                        "path", path,
                        "timestamp", Instant.now().toString()
                ));
    }
}
