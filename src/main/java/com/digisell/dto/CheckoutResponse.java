package com.digisell.dto;

import java.math.BigDecimal;

public class CheckoutResponse {

    private String orderId;
    private Long productId;
    private String productTitle;
    private BigDecimal amount;
    private String snapToken;
    private String snapRedirectUrl;
    private String status;

    public CheckoutResponse() {}

    public CheckoutResponse(String orderId, Long productId, String productTitle, BigDecimal amount, String snapToken, String snapRedirectUrl, String status) {
        this.orderId = orderId;
        this.productId = productId;
        this.productTitle = productTitle;
        this.amount = amount;
        this.snapToken = snapToken;
        this.snapRedirectUrl = snapRedirectUrl;
        this.status = status;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
