package com.digisell.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_transactions")
public class OrderTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productTitle;

    @Column(nullable = false)
    private String customerEmail;

    private String customerPhone;

    private String customerName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private String sellerUsername = "herindev";

    private String snapToken;
    private String snapRedirectUrl;
    private String paymentType;
    private String downloadToken;

    // Expiration date for secure digital downloads (e.g. 24 hours / 3 days)
    private LocalDateTime downloadExpiry;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public OrderTransaction() {
        this.createdAt = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }

    public OrderTransaction(String orderId, Long productId, String productTitle,
                            String customerEmail, String customerPhone, String customerName, BigDecimal amount) {
        this(orderId, productId, productTitle, "herindev", customerEmail, customerPhone, customerName, amount);
    }

    public OrderTransaction(String orderId, Long productId, String productTitle, String sellerUsername,
                            String customerEmail, String customerPhone, String customerName, BigDecimal amount) {
        this.orderId = orderId;
        this.productId = productId;
        this.productTitle = productTitle;
        this.sellerUsername = (sellerUsername != null && !sellerUsername.isBlank()) ? sellerUsername.toLowerCase().trim() : "herindev";
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerName = customerName;
        this.amount = amount;
        this.status = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getSnapToken() {
        return snapToken;
    }

    public void setSnapToken(String snapToken) {
        this.snapToken = snapToken;
    }

    public String getSnapRedirectUrl() {
        return snapRedirectUrl;
    }

    public void setSnapRedirectUrl(String snapRedirectUrl) {
        this.snapRedirectUrl = snapRedirectUrl;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getDownloadToken() {
        return downloadToken;
    }

    public void setDownloadToken(String downloadToken) {
        this.downloadToken = downloadToken;
    }

    public LocalDateTime getDownloadExpiry() {
        return downloadExpiry;
    }

    public void setDownloadExpiry(LocalDateTime downloadExpiry) {
        this.downloadExpiry = downloadExpiry;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = (sellerUsername != null && !sellerUsername.isBlank()) ? sellerUsername.toLowerCase().trim() : "herindev";
    }
}
