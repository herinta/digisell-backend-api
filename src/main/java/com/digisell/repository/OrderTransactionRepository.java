package com.digisell.repository;

import com.digisell.model.OrderTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {
    Optional<OrderTransaction> findByOrderId(String orderId);
    Optional<OrderTransaction> findByDownloadToken(String downloadToken);
    List<OrderTransaction> findBySellerUsername(String sellerUsername);
    List<OrderTransaction> findBySellerUsernameOrderByCreatedAtDesc(String sellerUsername);
}
