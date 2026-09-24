package com.digisell.controller;

import com.digisell.dto.AuthResponse;
import com.digisell.dto.LoginRequest;
import com.digisell.dto.RegisterRequest;
import com.digisell.model.Seller;
import com.digisell.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentSeller(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Token otentikasi tidak disediakan."));
        }

        String token = authHeader.substring(7).trim();
        return authService.validateToken(token)
                .map(seller -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", seller.getId());
                    data.put("username", seller.getUsername());
                    data.put("email", seller.getEmail());
                    data.put("fullName", seller.getFullName());
                    data.put("bio", seller.getBio());
                    data.put("avatarUrl", seller.getAvatarUrl());
                    return ResponseEntity.ok(data);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Sesi telah berakhir. Silakan login kembali.")));
    }

    @GetMapping("/seller/{username}")
    public ResponseEntity<?> getPublicSellerProfile(@PathVariable String username) {
        return authService.getSellerByUsername(username)
                .map(seller -> {
                    Map<String, Object> publicData = new HashMap<>();
                    publicData.put("id", seller.getId());
                    publicData.put("username", seller.getUsername());
                    publicData.put("fullName", seller.getFullName());
                    publicData.put("bio", seller.getBio());
                    publicData.put("avatarUrl", seller.getAvatarUrl());
                    return ResponseEntity.ok(publicData);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
