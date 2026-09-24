package com.digisell.service;

import com.digisell.dto.CheckoutRequest;
import com.digisell.dto.CheckoutResponse;
import com.digisell.dto.MidtransNotificationDto;
import com.digisell.model.OrderTransaction;
import com.digisell.model.Product;
import com.digisell.model.SellerWallet;
import com.digisell.model.TransactionStatus;
import com.digisell.repository.OrderTransactionRepository;
import com.digisell.repository.ProductRepository;
import com.digisell.repository.SellerWalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final SellerWalletRepository sellerWalletRepository;
    private final MidtransService midtransService;
    private final EmailNotificationService emailNotificationService;

    public OrderService(ProductRepository productRepository,
                        OrderTransactionRepository orderTransactionRepository,
                        SellerWalletRepository sellerWalletRepository,
                        MidtransService midtransService,
                        EmailNotificationService emailNotificationService) {
        this.productRepository = productRepository;
        this.orderTransactionRepository = orderTransactionRepository;
        this.sellerWalletRepository = sellerWalletRepository;
        this.midtransService = midtransService;
        this.emailNotificationService = emailNotificationService;
    }

    /**
     * Creates an order with pessimistic locking to safely verify stock availability
     * without race condition conflicts.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CheckoutResponse createOrder(CheckoutRequest request) {
        // Lock product row to prevent race conditions during high-volume checkouts
        Product product = productRepository.findByIdWithLock(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan dengan ID: " + request.getProductId()));

        if (!product.getUnlimitedStock() && product.getStock() <= 0) {
            throw new IllegalStateException("Maaf, kuota/stok produk digital ini sudah habis!");
        }

        String orderId = "DIGI-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 900 + 100);

        BigDecimal finalAmount = product.getPrice();
        if (request.getPromoCode() != null && (request.getPromoCode().equalsIgnoreCase("HEMAT10") || request.getPromoCode().equalsIgnoreCase("DISKON10"))) {
            finalAmount = finalAmount.multiply(new BigDecimal("0.90")).setScale(0, java.math.RoundingMode.HALF_UP);
        }

        OrderTransaction order = new OrderTransaction(
                orderId,
                product.getId(),
                product.getTitle(),
                product.getSellerUsername(),
                request.getCustomerEmail(),
                request.getCustomerPhone(),
                request.getCustomerName() != null && !request.getCustomerName().isBlank() ? request.getCustomerName() : "Pembeli",
                finalAmount
        );

        Map<String, String> snapResult = midtransService.createSnapTransaction(order);
        order.setSnapToken(snapResult.get("token"));
        order.setSnapRedirectUrl(snapResult.get("redirect_url"));

        orderTransactionRepository.save(order);

        return new CheckoutResponse(
                order.getOrderId(),
                order.getProductId(),
                order.getProductTitle(),
                order.getAmount(),
                order.getSnapToken(),
                order.getSnapRedirectUrl(),
                order.getStatus().name()
        );
    }

    @Transactional
    public boolean processNotification(MidtransNotificationDto dto) {
        boolean validSignature = midtransService.verifySignature(
                dto.getOrderId(),
                dto.getStatusCode(),
                dto.getGrossAmount(),
                dto.getSignatureKey()
        );

        if (!validSignature) {
            System.err.println("Invalid signature received for order: " + dto.getOrderId());
            return false;
        }

        OrderTransaction order = orderTransactionRepository.findByOrderId(dto.getOrderId())
                .orElse(null);

        if (order == null) {
            System.err.println("Order not found: " + dto.getOrderId());
            return false;
        }

        String transactionStatus = dto.getTransactionStatus();
        String fraudStatus = dto.getFraudStatus();

        if (transactionStatus.equalsIgnoreCase("capture")) {
            if (fraudStatus == null || fraudStatus.equalsIgnoreCase("accept")) {
                markAsPaid(order, dto.getPaymentType());
            }
        } else if (transactionStatus.equalsIgnoreCase("settlement")) {
            markAsPaid(order, dto.getPaymentType());
        } else if (transactionStatus.equalsIgnoreCase("cancel") ||
                   transactionStatus.equalsIgnoreCase("deny") ||
                   transactionStatus.equalsIgnoreCase("failure")) {
            order.setStatus(TransactionStatus.FAILED);
        } else if (transactionStatus.equalsIgnoreCase("expire")) {
            order.setStatus(TransactionStatus.EXPIRED);
        }

        orderTransactionRepository.save(order);
        return true;
    }

    @Transactional
    public OrderTransaction simulatePaymentSuccess(String orderId) {
        OrderTransaction order = orderTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        markAsPaid(order, "qris_sandbox_simulation");
        return orderTransactionRepository.save(order);
    }

    /**
     * Atomically decrements stock, credits wallet balance, generates 24-hour expiring download token,
     * and triggers automated delivery email.
     */
    private void markAsPaid(OrderTransaction order, String paymentType) {
        if (order.getStatus() == TransactionStatus.PAID) {
            return; // Idempotency check: prevent double credit if webhook called multiple times
        }

        order.setStatus(TransactionStatus.PAID);
        order.setPaymentType(paymentType);
        order.setPaidAt(LocalDateTime.now());
        if (order.getDownloadToken() == null) {
            order.setDownloadToken(UUID.randomUUID().toString());
        }

        // Expiry set to 24 hours from payment time for security
        order.setDownloadExpiry(LocalDateTime.now().plusHours(24));

        // Concurrency-safe stock decrement
        Product product = productRepository.findByIdWithLock(order.getProductId()).orElse(null);
        if (product != null) {
            if (!product.getUnlimitedStock() && product.getStock() > 0) {
                product.setStock(product.getStock() - 1);
            }
            product.setSoldCount(product.getSoldCount() + 1);
            productRepository.save(product);
        }

        // Credit to Seller In-App Wallet
        SellerWallet wallet = sellerWalletRepository.findAll().stream().findFirst().orElseGet(() -> {
            SellerWallet newWallet = new SellerWallet();
            return sellerWalletRepository.save(newWallet);
        });

        wallet.setAvailableBalance(wallet.getAvailableBalance().add(order.getAmount()));
        wallet.setTotalRevenue(wallet.getTotalRevenue().add(order.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now());
        sellerWalletRepository.save(wallet);

        // Automated Email Dispatch
        String accessPortalUrl = "http://localhost:5173/?view=access&orderId=" + order.getOrderId() + "&token=" + order.getDownloadToken();
        String directFileUrl = product != null ? product.getFileUrl() : "#";
        emailNotificationService.sendOrderDeliveryEmail(order, accessPortalUrl, directFileUrl);
    }

    /**
     * Validates download token and expiration date for secure product access.
     */
    public Map<String, Object> validateAndGetAccess(String orderId, String token) {
        OrderTransaction order = orderTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order tidak ditemukan"));

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getOrderId());
        result.put("productTitle", order.getProductTitle());
        result.put("customerEmail", order.getCustomerEmail());
        result.put("customerPhone", order.getCustomerPhone());
        result.put("amount", order.getAmount());
        result.put("paidAt", order.getPaidAt());
        result.put("downloadExpiry", order.getDownloadExpiry());

        if (order.getStatus() != TransactionStatus.PAID) {
            result.put("valid", false);
            result.put("message", "Status pembayaran belum selesai.");
            return result;
        }

        if (order.getDownloadToken() == null || !order.getDownloadToken().equalsIgnoreCase(token)) {
            result.put("valid", false);
            result.put("message", "Token akses unduhan tidak valid.");
            return result;
        }

        boolean isExpired = order.getDownloadExpiry() != null && LocalDateTime.now().isAfter(order.getDownloadExpiry());
        result.put("isExpired", isExpired);

        if (isExpired) {
            result.put("valid", false);
            result.put("message", "Tautan akses unduhan telah kadaluarsa demi alasan keamanan (Batas: 24 Jam).");
        } else {
            result.put("valid", true);
            Product product = getProductByOrder(order);
            result.put("downloadUrl", product.getFileUrl());
            result.put("productCategory", product.getCategory());
        }

        return result;
    }

    public OrderTransaction getOrder(String orderId) {
        return orderTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    public Product getProductByOrder(OrderTransaction order) {
        return productRepository.findById(order.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
}
