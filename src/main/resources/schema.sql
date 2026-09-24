-- ========================================================
-- DigiSell Database Schema (MySQL 8.0+)
-- Database: digisell_db
-- ========================================================

CREATE DATABASE IF NOT EXISTS `digisell_db`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `digisell_db`;

-- --------------------------------------------------------
-- 1. Table: sellers
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sellers` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Custom link slug, misal: herindev',
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL COMMENT 'BCrypt hashed password',
    `full_name` VARCHAR(150) NOT NULL,
    `bio` VARCHAR(1000) NULL,
    `avatar_url` VARCHAR(500) NULL,
    `auth_token` VARCHAR(255) NULL,
    `token_expiry` DATETIME NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 2. Table: products
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `products` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `description` VARCHAR(2000) NOT NULL,
    `price` DECIMAL(15,2) NOT NULL,
    `original_price` DECIMAL(15,2) NULL COMMENT 'Harga coret diskon',
    `image_url` VARCHAR(500) NOT NULL,
    `file_url` VARCHAR(500) NOT NULL,
    `category` VARCHAR(100) NULL,
    `badge` VARCHAR(100) NULL,
    `stock` INT NOT NULL DEFAULT 10,
    `unlimited_stock` BOOLEAN NOT NULL DEFAULT FALSE,
    `sold_count` INT NOT NULL DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 3. Table: order_transactions
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `order_transactions` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` VARCHAR(100) NOT NULL UNIQUE,
    `product_id` BIGINT NOT NULL,
    `product_title` VARCHAR(255) NOT NULL,
    `customer_name` VARCHAR(150) NULL,
    `customer_email` VARCHAR(150) NOT NULL,
    `customer_phone` VARCHAR(50) NULL,
    `amount` DECIMAL(15,2) NOT NULL,
    `status` VARCHAR(50) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, PAID, EXPIRED, CANCELLED',
    `snap_token` VARCHAR(255) NULL,
    `snap_redirect_url` VARCHAR(500) NULL,
    `payment_type` VARCHAR(50) NULL,
    `download_token` VARCHAR(255) NULL,
    `download_expiry` DATETIME NULL COMMENT 'Masa aktif link download (24 jam)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `paid_at` DATETIME NULL,
    INDEX `idx_order_status` (`status`),
    INDEX `idx_order_token` (`download_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 4. Table: seller_wallets
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `seller_wallets` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `available_balance` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    `total_revenue` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    `total_withdrawn` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 5. Table: withdrawals
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `withdrawals` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `withdrawal_code` VARCHAR(100) NOT NULL UNIQUE,
    `amount` DECIMAL(15,2) NOT NULL,
    `bank_name` VARCHAR(100) NOT NULL,
    `account_number` VARCHAR(100) NOT NULL,
    `account_holder` VARCHAR(150) NOT NULL,
    `status` VARCHAR(50) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, SUCCESS, REJECTED',
    `requested_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `processed_at` DATETIME NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Default Data Seeder (Opsional jika Hibernate ddl-auto=update sudah generate)
-- --------------------------------------------------------
INSERT IGNORE INTO `sellers` (`id`, `username`, `email`, `password`, `full_name`, `bio`, `avatar_url`, `created_at`) 
VALUES (
    1, 
    'herindev', 
    'herin@digisell.com', 
    '$2a$10$7vN34tV.k/Dugwz4g7wTge7jAevxKsf4q9w1YwE19pGk3gV7m.1O2', -- 'password123'
    'Herin Dev', 
    'Software Engineer & Digital Creator. Sharing high-performance Notion templates, developer tools, and productivity shortcuts.', 
    'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80',
    NOW()
);

INSERT IGNORE INTO `seller_wallets` (`id`, `available_balance`, `total_revenue`, `total_withdrawn`, `updated_at`)
VALUES (1, 1420000.00, 2450000.00, 1030000.00, NOW());

INSERT IGNORE INTO `products` (`id`, `title`, `description`, `price`, `original_price`, `stock`, `unlimited_stock`, `sold_count`, `category`, `badge`, `image_url`, `file_url`, `created_at`)
VALUES 
(
    1,
    'Notion Fat Loss & Workout Tracker',
    'Template Notion komprehensif untuk tracking kalori, gym split, habit pembentukan otot, dan progress foto mingguan.',
    43000.00,
    99000.00,
    8,
    0,
    342,
    'Fitness & Health',
    'Best Seller 🔥',
    'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=800&q=80',
    'https://digisell.dev/vault/notion-fatloss-template.zip',
    NOW()
),
(
    2,
    'iPhone Shortcut - One Click Money Tracker',
    'Otomatisasi iOS Shortcut untuk mencatat pengeluaran langsung dari Lock Screen & sinkronisasi otomatis ke Google Sheets.',
    198000.00,
    350000.00,
    12,
    0,
    215,
    'Productivity',
    'Hot Item ⚡',
    'https://images.unsplash.com/photo-1563986768609-322da13575f3?auto=format&fit=crop&w=800&q=80',
    'https://digisell.dev/vault/ios-money-tracker-shortcut.shortcut',
    NOW()
),
(
    3,
    'Notion Template Life Planner All-in-One',
    'Sistem hidup terintegrasi: Goal Setting 2026, Finance Tracker, Habit Tracker, dan Daily Journaling minimalis.',
    58000.00,
    120000.00,
    5,
    0,
    520,
    'Self Development',
    'Staff Pick ⭐',
    'https://images.unsplash.com/photo-1484480974693-6ca0a78fb36b?auto=format&fit=crop&w=800&q=80',
    'https://digisell.dev/vault/notion-life-planner-v2.zip',
    NOW()
),
(
    4,
    'Fullstack Spring Boot & React Starter Kit',
    'Source code production-ready boilerplate dengan JWT Auth, Midtrans payment gateway, Docker, dan clean architecture.',
    150000.00,
    499000.00,
    3,
    0,
    180,
    'Developer Tool',
    'Limited License 🚀',
    'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=800&q=80',
    'https://digisell.dev/vault/spring-boot-react-starter-kit.zip',
    NOW()
);
