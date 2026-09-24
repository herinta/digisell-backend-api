package com.digisell.controller;

import com.digisell.dto.ProductRequest;
import com.digisell.model.OrderTransaction;
import com.digisell.model.Product;
import com.digisell.model.SellerWallet;
import com.digisell.model.TransactionStatus;
import com.digisell.model.Withdrawal;
import com.digisell.model.WithdrawalStatus;
import com.digisell.repository.OrderTransactionRepository;
import com.digisell.repository.ProductRepository;
import com.digisell.repository.SellerWalletRepository;
import com.digisell.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    private final ProductRepository productRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final SellerWalletRepository sellerWalletRepository;
    private final WithdrawalService withdrawalService;

    public SellerController(ProductRepository productRepository,
                            OrderTransactionRepository orderTransactionRepository,
                            SellerWalletRepository sellerWalletRepository,
                            WithdrawalService withdrawalService) {
        this.productRepository = productRepository;
        this.orderTransactionRepository = orderTransactionRepository;
        this.sellerWalletRepository = sellerWalletRepository;
        this.withdrawalService = withdrawalService;
    }

    private String getCurrentSellerUsername() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equalsIgnoreCase("anonymousUser")) {
            return auth.getName().toLowerCase().trim();
        }
        return "herindev"; // Fallback to default seller
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        String currentSeller = getCurrentSellerUsername();
        SellerWallet wallet = withdrawalService.getWallet();
        List<Product> products = productRepository.findBySellerUsername(currentSeller);
        List<OrderTransaction> orders = orderTransactionRepository.findBySellerUsername(currentSeller);

        // 1. Calculate REAL metrics strictly from actual PAID orders of THIS seller
        List<OrderTransaction> paidOrders = orders.stream()
                .filter(o -> o.getStatus() == TransactionStatus.PAID)
                .toList();

        BigDecimal actualTotalRevenue = paidOrders.stream()
                .map(OrderTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Real total withdrawn
        List<Withdrawal> withdrawals = withdrawalService.getAllWithdrawals();
        BigDecimal actualTotalWithdrawn = withdrawals.stream()
                .filter(w -> w.getStatus() == WithdrawalStatus.PROCESSED)
                .map(Withdrawal::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal availableBalance = actualTotalRevenue.subtract(actualTotalWithdrawn);
        if (availableBalance.compareTo(BigDecimal.ZERO) < 0) {
            availableBalance = BigDecimal.ZERO;
        }

        // Sync wallet state with actual sales
        wallet.setTotalRevenue(actualTotalRevenue);
        wallet.setAvailableBalance(availableBalance);
        wallet.setTotalWithdrawn(actualTotalWithdrawn);
        wallet.setUpdatedAt(LocalDateTime.now());
        sellerWalletRepository.save(wallet);

        long lowStockCount = products.stream()
                .filter(p -> !p.getUnlimitedStock() && p.getStock() <= 3)
                .count();

        long totalSoldUnits = products.stream()
                .mapToLong(p -> p.getSoldCount() != null ? p.getSoldCount() : 0)
                .sum();

        Map<String, Object> data = new HashMap<>();
        data.put("wallet", wallet);
        data.put("sellerUsername", currentSeller);
        data.put("totalProducts", products.size());
        data.put("totalOrders", paidOrders.size()); // Count only PAID orders
        data.put("totalSoldUnits", totalSoldUnits);
        data.put("lowStockCount", lowStockCount);
        data.put("recentOrders", orders.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(15)
                .toList());

        return ResponseEntity.ok(data);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getSellerProducts() {
        String currentSeller = getCurrentSellerUsername();
        return ResponseEntity.ok(productRepository.findBySellerUsername(currentSeller));
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        String currentSeller = getCurrentSellerUsername();
        Product product = new Product(
                currentSeller,
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getOriginalPrice(),
                request.getImageUrl(),
                request.getFileUrl(),
                request.getCategory() != null ? request.getCategory() : "Digital Product",
                request.getBadge(),
                request.getStock() != null ? request.getStock() : 10,
                request.getUnlimitedStock() != null ? request.getUnlimitedStock() : false,
                0
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan"));

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setImageUrl(request.getImageUrl());
        product.setFileUrl(request.getFileUrl());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        product.setBadge(request.getBadge());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getUnlimitedStock() != null) product.setUnlimitedStock(request.getUnlimitedStock());

        return ResponseEntity.ok(productRepository.save(product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan"));
        productRepository.delete(product);
        return ResponseEntity.ok(Map.of("success", true, "message", "Produk berhasil dihapus"));
    }

    @PutMapping("/products/{id}/stock")
    public ResponseEntity<Product> updateProductStock(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (payload.containsKey("stock")) {
            product.setStock(((Number) payload.get("stock")).intValue());
        }
        if (payload.containsKey("unlimitedStock")) {
            product.setUnlimitedStock((Boolean) payload.get("unlimitedStock"));
        }

        return ResponseEntity.ok(productRepository.save(product));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderTransaction>> getAllOrders() {
        String currentSeller = getCurrentSellerUsername();
        return ResponseEntity.ok(orderTransactionRepository.findBySellerUsernameOrderByCreatedAtDesc(currentSeller));
    }

    @GetMapping("/withdrawals")
    public ResponseEntity<List<Withdrawal>> getWithdrawals() {
        return ResponseEntity.ok(withdrawalService.getAllWithdrawals());
    }

    @PostMapping("/withdrawals")
    public ResponseEntity<Withdrawal> requestWithdrawal(@RequestBody Map<String, String> request) {
        BigDecimal amount = new BigDecimal(request.get("amount"));
        String bankName = request.get("bankName");
        String accountNumber = request.get("accountNumber");
        String accountHolder = request.get("accountHolder");

        Withdrawal withdrawal = withdrawalService.requestWithdrawal(amount, bankName, accountNumber, accountHolder);
        return ResponseEntity.ok(withdrawal);
    }
}
