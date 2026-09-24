package com.digisell.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProductRequest {

    @NotBlank(message = "Judul produk wajib diisi")
    private String title;

    @NotBlank(message = "Deskripsi produk wajib diisi")
    private String description;

    @NotNull(message = "Harga produk wajib diisi")
    @DecimalMin(value = "1000", message = "Harga minimal Rp 1.000")
    private BigDecimal price;

    private BigDecimal originalPrice;

    @NotBlank(message = "URL cover / gambar produk wajib diisi")
    private String imageUrl;

    @NotBlank(message = "URL file digital / link akses wajib diisi")
    private String fileUrl;

    private String category;
    private String badge;
    private Integer stock = 10;
    private Boolean unlimitedStock = false;

    public ProductRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Boolean getUnlimitedStock() { return unlimitedStock; }
    public void setUnlimitedStock(Boolean unlimitedStock) { this.unlimitedStock = unlimitedStock; }
}
