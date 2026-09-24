package com.digisell.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal originalPrice; // Strikethrough discount price

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String fileUrl;

    private String category;
    private String badge;

    // Stock Management Fields
    @Column(nullable = false)
    private Integer stock = 10;

    @Column(nullable = false)
    private Boolean unlimitedStock = false;

    @Column(nullable = false)
    private Integer soldCount = 0;

    // Seller Association
    @Column(nullable = false)
    private String sellerUsername = "herindev";

    private LocalDateTime createdAt;

    public Product() {
        this.createdAt = LocalDateTime.now();
    }

    public Product(String title, String description, BigDecimal price, BigDecimal originalPrice,
                   String imageUrl, String fileUrl, String category, String badge,
                   Integer stock, Boolean unlimitedStock, Integer soldCount) {
        this("herindev", title, description, price, originalPrice, imageUrl, fileUrl, category, badge, stock, unlimitedStock, soldCount);
    }

    public Product(String sellerUsername, String title, String description, BigDecimal price, BigDecimal originalPrice,
                   String imageUrl, String fileUrl, String category, String badge,
                   Integer stock, Boolean unlimitedStock, Integer soldCount) {
        this.sellerUsername = (sellerUsername != null && !sellerUsername.isBlank()) ? sellerUsername.toLowerCase().trim() : "herindev";
        this.title = title;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.category = category;
        this.badge = badge;
        this.stock = stock;
        this.unlimitedStock = unlimitedStock;
        this.soldCount = soldCount;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getUnlimitedStock() {
        return unlimitedStock;
    }

    public void setUnlimitedStock(Boolean unlimitedStock) {
        this.unlimitedStock = unlimitedStock;
    }

    public Integer getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(Integer soldCount) {
        this.soldCount = soldCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = (sellerUsername != null && !sellerUsername.isBlank()) ? sellerUsername.toLowerCase().trim() : "herindev";
    }
}
