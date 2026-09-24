package com.digisell;

import com.digisell.model.Product;
import com.digisell.model.Seller;
import com.digisell.model.SellerWallet;
import com.digisell.repository.ProductRepository;
import com.digisell.repository.SellerRepository;
import com.digisell.repository.SellerWalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class DigisellApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigisellApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(ProductRepository productRepository,
                              SellerWalletRepository walletRepository,
                              SellerRepository sellerRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Seed Default Seller (@herindev)
            if (sellerRepository.count() == 0) {
                Seller defaultSeller = new Seller(
                    "herindev",
                    "herin@digisell.com",
                    encoder.encode("password123"),
                    "Herin Dev",
                    "Creator & software engineer. Menyediakan template Notion, otomasi spreadsheet, dan source code siap pakai langsung dikirim otomatis setelah bayar.",
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80"
                );
                sellerRepository.save(defaultSeller);
                System.out.println(">>> Default seller @herindev created with password: password123");
            }

            if (!sellerRepository.existsByUsername("anitatemplate")) {
                Seller anita = new Seller(
                    "anitatemplate",
                    "anita@digisell.com",
                    encoder.encode("password123"),
                    "Anita Template",
                    "Creator & designer template Notion estetik. Membantu produktivitas harian, meal planning, dan habit tracker kamu.",
                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=300&q=80"
                );
                sellerRepository.save(anita);
                System.out.println(">>> Second seller @anitatemplate created with password: password123");
            }

            // Seed Wallet
            if (walletRepository.count() == 0) {
                SellerWallet wallet = new SellerWallet();
                wallet.setAvailableBalance(new BigDecimal("1250000"));
                wallet.setTotalRevenue(new BigDecimal("2450000"));
                wallet.setTotalWithdrawn(new BigDecimal("1200000"));
                walletRepository.save(wallet);
                System.out.println(">>> Seller wallet initialized with initial balance!");
            }

            // Seed Products if none, or update seller assignments for existing
            if (productRepository.count() == 0) {
                List<Product> initialProducts = List.of(
                    new Product(
                        "anitatemplate",
                        "Notion Fat Loss & Workout Tracker",
                        "Template adalah all-in-one sistem untuk menjalankan program turun berat badan secara bertahap. Menggabungkan Workout Plan, Intermittent Fasting, dan Meal Plan dalam satu dashboard Notion.",
                        new BigDecimal("43000"),
                        new BigDecimal("80000"),
                        "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=800&q=80",
                        "https://drive.google.com/drive/folders/sample-notion-fat-loss",
                        "Template Notion",
                        "🔥 Best Seller",
                        8,
                        false,
                        342
                    ),
                    new Product(
                        "herindev",
                        "iPhone Shortcut - One Click Money Tracker",
                        "Catat keuangan harian lebih cepat dari iPhone langsung otomatis tersinkronisasi ke database Notion. Pantau pemasukan, pengeluaran, dan saldo akun.",
                        new BigDecimal("198000"),
                        new BigDecimal("218000"),
                        "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=800&q=80",
                        "https://drive.google.com/drive/folders/sample-money-tracker",
                        "iOS Shortcut",
                        "⚡ Populer",
                        12,
                        false,
                        215
                    ),
                    new Product(
                        "anitatemplate",
                        "Notion Template Life Planner All-in-One",
                        "Rencanakan hari mu, akademik dan raih goal yang ingin kamu capai dengan satu sistem produktivitas rapi & estetik.",
                        new BigDecimal("58000"),
                        new BigDecimal("145000"),
                        "https://images.unsplash.com/photo-1484480974693-6ca0a78fb36b?auto=format&fit=crop&w=800&q=80",
                        "https://drive.google.com/drive/folders/sample-life-planner",
                        "Template Notion",
                        "Diskon 60%",
                        5,
                        false,
                        520
                    ),
                    new Product(
                        "herindev",
                        "Fullstack Spring Boot & React Starter Kit",
                        "Source code arsitektur micro-SaaS modern siap pakai dengan autentikasi JWT, integrasi Midtrans, Docker Compose, dan Clean Architecture.",
                        new BigDecimal("150000"),
                        new BigDecimal("300000"),
                        "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=800&q=80",
                        "https://github.com/sample/starter-kit-repo",
                        "Source Code",
                        "Pro Dev",
                        3,
                        false,
                        180
                    )
                );
                productRepository.saveAll(initialProducts);
                System.out.println(">>> Demo digital products successfully seeded with seller ownership!");
            } else {
                // Ensure existing products in DB have assigned seller usernames
                List<Product> existing = productRepository.findAll();
                for (Product p : existing) {
                    if (p.getTitle().contains("Notion")) {
                        p.setSellerUsername("anitatemplate");
                    } else {
                        p.setSellerUsername("herindev");
                    }
                }
                productRepository.saveAll(existing);
            }
        };
    }
}
