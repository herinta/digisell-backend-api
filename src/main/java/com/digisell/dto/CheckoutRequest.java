package com.digisell.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Email is required")
    @Email(message = "Format email tidak valid")
    private String customerEmail;

    @NotBlank(message = "Nomor WhatsApp wajib diisi")
    private String customerPhone;

    private String customerName;
    private String promoCode;

    public CheckoutRequest() {}

    public CheckoutRequest(Long productId, String customerEmail, String customerPhone, String customerName, String promoCode) {
        this.productId = productId;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerName = customerName;
        this.promoCode = promoCode;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }
}
