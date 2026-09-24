package com.digisell.service;

import com.digisell.model.SellerWallet;
import com.digisell.model.Withdrawal;
import com.digisell.model.WithdrawalStatus;
import com.digisell.repository.SellerWalletRepository;
import com.digisell.repository.WithdrawalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawalService {

    private final SellerWalletRepository walletRepository;
    private final WithdrawalRepository withdrawalRepository;

    public WithdrawalService(SellerWalletRepository walletRepository, WithdrawalRepository withdrawalRepository) {
        this.walletRepository = walletRepository;
        this.withdrawalRepository = withdrawalRepository;
    }

    public SellerWallet getWallet() {
        return walletRepository.findAll().stream().findFirst().orElseGet(() -> {
            SellerWallet w = new SellerWallet();
            return walletRepository.save(w);
        });
    }

    @Transactional
    public Withdrawal requestWithdrawal(BigDecimal amount, String bankName, String accountNumber, String accountHolder) {
        if (amount == null || amount.compareTo(new BigDecimal("50000")) < 0) {
            throw new IllegalArgumentException("Minimal penarikan dana adalah Rp 50.000");
        }

        SellerWallet wallet = getWallet();
        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo aktif tidak mencukupi untuk penarikan sebesar Rp " + amount);
        }

        // Deduct available balance and increase totalWithdrawn
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        wallet.setTotalWithdrawn(wallet.getTotalWithdrawn().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        String code = "WD-" + System.currentTimeMillis();
        Withdrawal withdrawal = new Withdrawal(code, amount, bankName, accountNumber, accountHolder);
        withdrawal.setStatus(WithdrawalStatus.PROCESSED); // Auto-processed for portfolio demonstration
        withdrawal.setProcessedAt(LocalDateTime.now());

        return withdrawalRepository.save(withdrawal);
    }

    public List<Withdrawal> getAllWithdrawals() {
        return withdrawalRepository.findAllByOrderByRequestedAtDesc();
    }
}
