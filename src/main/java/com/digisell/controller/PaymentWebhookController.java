package com.digisell.controller;

import com.digisell.dto.MidtransNotificationDto;
import com.digisell.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentWebhookController {

    private final OrderService orderService;

    public PaymentWebhookController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/notification")
    public ResponseEntity<Map<String, String>> handleMidtransNotification(@RequestBody MidtransNotificationDto notification) {
        System.out.println(">>> Incoming Midtrans webhook notification for order: " + notification.getOrderId());
        boolean success = orderService.processNotification(notification);

        if (success) {
            return ResponseEntity.ok(Map.of("status", "OK", "message", "Notification processed successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Failed to process notification"));
        }
    }
}
