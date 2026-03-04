-- 1. TẠO DATABASE VÀ SỬ DỤNG DATABASE
CREATE DATABASE IF NOT EXISTS QLCT
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE QLCT;

-- 2. TẠO BẢNG USERS (Người dùng)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, -- Lưu mật khẩu mã hóa (như bcrypt)
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. TẠO BẢNG CATEGORIES (Danh mục thu/chi)
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type ENUM('income', 'expense') NOT NULL,
    icon VARCHAR(50) DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Ràng buộc khóa ngoại nối với bảng users
    -- Khi xóa user thì tự động xóa luôn các danh mục của user đó (CASCADE)
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. TẠO BẢNG TRANSACTIONS (Giao dịch)
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL, -- Kiểu DECIMAL chuẩn nhất cho tiền tệ
    note TEXT,
    transaction_date DATE NOT NULL DEFAULT (CURRENT_DATE), -- MySQL 8.0.13+ trở lên hỗ trợ DEFAULT (CURRENT_DATE)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Ràng buộc khóa ngoại
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
) ENGINE=InnoDB;


-- ==========================================
-- 5. CHÈN DỮ LIỆU MẪU (SEED DATA) ĐỂ CHẠY THỬ
-- ==========================================

-- Thêm 1 User mẫu (Giả sử mật khẩu đã được mã hóa bằng bcrypt)
INSERT INTO users (username, password_hash, email) 
VALUES ('nguyenvana', '$2b$10$hashedpasswordstringhere', 'nguyenvana@example.com');

-- Lấy lại ID của user vừa tạo (trong thực tế sẽ có biến lưu ID, ở đây giả sử ID là 1)
-- Thêm 5 Danh mục mẫu cho user ID = 1
INSERT INTO categories (user_id, name, type, icon) VALUES 
(1, 'Tiền lương', 'income', 'fa-money-bill-wave'),
(1, 'Ăn uống', 'expense', 'fa-utensils'),
(1, 'Xăng xe', 'expense', 'fa-gas-pump'),
(1, 'Tiền nhà', 'expense', 'fa-home'),
(1, 'Mua sắm', 'expense', 'fa-shopping-cart');

-- Thêm 3 Giao dịch (Transactions) mẫu cho user ID = 1
-- Chú ý: Cần truyền đúng category_id tương ứng với bảng categories ở trên
-- (1: Lương, 2: Ăn uống, 3: Xăng xe...)
INSERT INTO transactions (user_id, category_id, amount, note, transaction_date) VALUES 
(1, 1, 15000000.00, 'Nhận lương tháng 10', '2023-10-05'),
(1, 2, 55000.00, 'Ăn bún chả với bạn', '2023-10-06'),
(1, 3, 100000.00, 'Đổ xăng xe máy', '2023-10-07');
