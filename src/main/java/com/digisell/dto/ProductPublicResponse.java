package com.digisell.dto;

import com.digisell.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductPublicResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String imageUrl;
    private String category;
    private String badge;
    private Integer stock;
    private Boolean unlimitedStock;
    private Integer soldCount;
    private String sellerUsername;
    private LocalDateTime createdAt;

    public ProductPublicResponse() {}

    public ProductPublicResponse(Product p) {
        this.id = p.getId();
        this.title = p.getTitle();
        this.description = p.getDescription();
        this.price = p.getPrice();
        this.originalPrice = p.getOriginalPrice();
        this.imageUrl = p.getImageUrl();
        this.category = p.getCategory();
        this.badge = p.getBadge();
        this.stock = p.getStock();
        this.unlimitedStock = p.getUnlimitedStock();
        this.soldCount = p.getSoldCount();
        this.sellerUsername = p.getSellerUsername();
        this.createdAt = p.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public String getBadge() { return badge; }
    public Integer getStock() { return stock; }
    public Boolean getUnlimitedStock() { return unlimitedStock; }
    public Integer getSoldCount() { return soldCount; }
    public String getSellerUsername() { return sellerUsername; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
