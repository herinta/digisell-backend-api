package com.digisell.controller;

import com.digisell.dto.CheckoutRequest;
import com.digisell.dto.CheckoutResponse;
import com.digisell.model.OrderTransaction;
import com.digisell.model.Product;
import com.digisell.model.TransactionStatus;
import com.digisell.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderStatus(@PathVariable String orderId) {
        try {
            OrderTransaction order = orderService.getOrder(orderId);
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", order.getOrderId());
            result.put("productId", order.getProductId());
            result.put("productTitle", order.getProductTitle());
            result.put("amount", order.getAmount());
            result.put("customerEmail", order.getCustomerEmail());
            result.put("customerPhone", order.getCustomerPhone());
            result.put("status", order.getStatus().name());
            result.put("paymentType", order.getPaymentType());
            result.put("createdAt", order.getCreatedAt());
            result.put("paidAt", order.getPaidAt());
            result.put("downloadExpiry", order.getDownloadExpiry());

            // CRITICAL SECURITY FIX:
            // Never leak raw product fileUrl here to prevent scraping without payment/token.
            // Digital download access is strictly gated behind /api/orders/access with a valid token.
            if (order.getStatus() == TransactionStatus.PAID) {
                result.put("isPaid", true);
            }

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint to validate secure expiring access tokens.
     */
    @GetMapping("/access")
    public ResponseEntity<Map<String, Object>> getSecureAccess(
            @RequestParam String orderId,
            @RequestParam String token) {
        try {
            Map<String, Object> access = orderService.validateAndGetAccess(orderId, token);
            return ResponseEntity.ok(access);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/simulate-pay")
    public ResponseEntity<Map<String, Object>> simulatePay(@PathVariable String orderId) {
        OrderTransaction order = orderService.simulatePaymentSuccess(orderId);
        Product product = orderService.getProductByOrder(order);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Pembayaran disimulasikan sukses! Status: PAID.");
        response.put("orderId", order.getOrderId());
        response.put("status", order.getStatus().name());
        response.put("downloadUrl", product.getFileUrl());
        response.put("downloadToken", order.getDownloadToken());
        response.put("downloadExpiry", order.getDownloadExpiry());
        return ResponseEntity.ok(response);
    }
}
