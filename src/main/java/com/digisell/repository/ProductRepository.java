package com.digisell.repository;

import com.digisell.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findBySellerUsername(String sellerUsername);

    List<Product> findBySellerUsernameAndCategoryIgnoreCase(String sellerUsername, String category);

    /**
     * Pessimistic Write Lock (SELECT ... FOR UPDATE) to strictly avoid race conditions
     * when multiple buyers attempt to purchase the remaining stock simultaneously.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);
}
