package com.digisell.controller;

import com.digisell.dto.ProductPublicResponse;
import com.digisell.model.Product;
import com.digisell.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProductPublicResponse>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String seller) {
        List<Product> products;
        if (seller != null && !seller.isBlank()) {
            String cleanSeller = seller.toLowerCase().trim();
            if (category != null && !category.equalsIgnoreCase("all") && !category.isBlank()) {
                products = productRepository.findBySellerUsernameAndCategoryIgnoreCase(cleanSeller, category);
            } else {
                products = productRepository.findBySellerUsername(cleanSeller);
            }
        } else {
            products = (category != null && !category.equalsIgnoreCase("all") && !category.isBlank())
                    ? productRepository.findByCategoryIgnoreCase(category)
                    : productRepository.findAll();
        }

        List<ProductPublicResponse> publicList = products.stream()
                .map(ProductPublicResponse::new)
                .toList();

        return ResponseEntity.ok(publicList);
    }

    @GetMapping("/seller/{username}")
    public ResponseEntity<List<ProductPublicResponse>> getProductsBySeller(@PathVariable String username) {
        List<Product> products = productRepository.findBySellerUsername(username.toLowerCase().trim());
        List<ProductPublicResponse> publicList = products.stream()
                .map(ProductPublicResponse::new)
                .toList();
        return ResponseEntity.ok(publicList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductPublicResponse> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ProductPublicResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
