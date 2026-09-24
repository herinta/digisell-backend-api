package com.digisell.service;

import com.digisell.dto.AuthResponse;
import com.digisell.dto.LoginRequest;
import com.digisell.dto.RegisterRequest;
import com.digisell.model.Seller;
import com.digisell.repository.SellerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final SellerRepository sellerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String cleanUsername = request.getUsername().toLowerCase().trim();
        String cleanEmail = request.getEmail().toLowerCase().trim();

        if (sellerRepository.existsByUsername(cleanUsername)) {
            throw new IllegalArgumentException("Username '" + cleanUsername + "' sudah digunakan. Pilih username lain.");
        }

        if (sellerRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("Email '" + cleanEmail + "' sudah terdaftar. Silakan login.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        String defaultAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80";

        Seller seller = new Seller(
                cleanUsername,
                cleanEmail,
                hashedPassword,
                request.getFullName(),
                request.getBio() != null ? request.getBio() : "Creator & digital seller at DigiSell.",
                defaultAvatar
        );

        String token = generateToken();
        seller.setAuthToken(token);
        seller.setTokenExpiry(LocalDateTime.now().plusDays(30)); // 30-day session

        Seller savedSeller = sellerRepository.save(seller);
        return new AuthResponse(token, savedSeller);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getIdentifier().toLowerCase().trim();

        Seller seller = sellerRepository.findByUsername(identifier)
                .or(() -> sellerRepository.findByEmail(identifier))
                .orElseThrow(() -> new IllegalArgumentException("Akun dengan username atau email tersebut tidak ditemukan."));

        if (!passwordEncoder.matches(request.getPassword(), seller.getPassword())) {
            throw new IllegalArgumentException("Password yang Anda masukkan salah.");
        }

        String token = generateToken();
        seller.setAuthToken(token);
        seller.setTokenExpiry(LocalDateTime.now().plusDays(30));

        Seller updatedSeller = sellerRepository.save(seller);
        return new AuthResponse(token, updatedSeller);
    }

    public Optional<Seller> validateToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();

        return sellerRepository.findByAuthToken(token)
                .filter(s -> s.getTokenExpiry() != null && LocalDateTime.now().isBefore(s.getTokenExpiry()));
    }

    public Optional<Seller> getSellerByUsername(String username) {
        return sellerRepository.findByUsername(username.toLowerCase().trim());
    }

    private String generateToken() {
        return "dgs_" + UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis();
    }
}
