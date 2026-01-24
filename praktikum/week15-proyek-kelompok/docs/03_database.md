# 03 - Database Design

## Agri-POS Database Schema

### 1. Entity Relationship Diagram (ERD)

![Screenshot hasil](/screenshots/ERD%20Agri-POS.drawio.png)


### 2. DDL (Data Definition Language)

#### USERS Table
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CASHIER', 'ADMIN')),
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Penjelasan:**
- `id`: Primary key, auto-increment
- `username`: Unique identifier untuk login
- `password`: Password (dalam prod gunakan hash)
- `role`: CASHIER atau ADMIN
- `name`: Nama lengkap user
- `created_at`: Timestamp pembuatan

#### PRODUCTS Table
```sql
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(12, 2) NOT NULL CHECK (price > 0),
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Penjelasan:**
- `code`: Unique product identifier (misalnya: P001, P002)
- `name`: Nama produk
- `category`: Kategori produk (Benih, Pupuk, Pestisida, Alat)
- `price`: Harga per unit (check constraint: > 0)
- `stock`: Jumlah stok (check constraint: >= 0)
- `created_at`, `updated_at`: Audit timestamp

#### TRANSACTIONS Table
```sql
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    transaction_id VARCHAR(50) UNIQUE NOT NULL,
    cashier_id INTEGER NOT NULL REFERENCES users(id),
    total DECIMAL(12, 2) NOT NULL,
    amount_paid DECIMAL(12, 2) NOT NULL,
    change DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED' 
        CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Penjelasan:**
- `transaction_id`: Unique reference (TRX-xxxx)
- `cashier_id`: Foreign key ke users.id
- `total`: Total belanja
- `amount_paid`: Jumlah uang yang diterima
- `change`: Kembalian
- `payment_method`: CASH, E-WALLET, TRANSFER, etc
- `status`: PENDING, COMPLETED, CANCELLED
- `created_at`: Waktu transaksi

#### TRANSACTION_ITEMS Table
```sql
CREATE TABLE transaction_items (
    id SERIAL PRIMARY KEY,
    transaction_id INTEGER NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    product_code VARCHAR(20) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL
);
```

**Penjelasan:**
- `transaction_id`: Foreign key ke transactions.id (one-to-many)
- `product_code`: Kode produk yang dibeli
- `product_name`: Nama produk (snapshot dari products table)
- `quantity`: Jumlah item yang dibeli
- `unit_price`: Harga per unit saat transaksi
- `subtotal`: quantity × unit_price
- ON DELETE CASCADE: Jika transaksi dihapus, items ikut terhapus

### 3. Indexes

```sql
CREATE INDEX idx_products_code ON products(code);
CREATE INDEX idx_transactions_cashier ON transactions(cashier_id);
CREATE INDEX idx_transactions_created ON transactions(created_at);
```

**Penjelasan:**
- `idx_products_code`: Mempercepat query by product code
- `idx_transactions_cashier`: Mempercepat query transaksi per kasir
- `idx_transactions_created`: Mempercepat query laporan per tanggal

### 4. Sample Data (Seed)

#### Users
```sql
INSERT INTO users (username, password, role, name) VALUES 
('kasir001', 'password123', 'CASHIER', 'Kasir 1'),
('kasir002', 'password123', 'CASHIER', 'Kasir 2'),
('admin001', 'admin123', 'ADMIN', 'Administrator'),
('manager001', 'manager123', 'ADMIN', 'Manager Toko');
```

#### Products
```sql
INSERT INTO products (code, name, category, price, stock) VALUES 
('P001', 'Benih Padi Unggul', 'Benih', 50000.00, 100),
('P002', 'Pupuk NPK 15-15-15', 'Pupuk', 75000.00, 80),
('P003', 'Pestisida Organik 500ml', 'Pestisida', 125000.00, 50),
('P004', 'Benih Jagung Hibrida', 'Benih', 65000.00, 60),
('P005', 'Pupuk Urea 50kg', 'Pupuk', 350000.00, 30),
('P006', 'Alat Semprot Manual 8L', 'Alat', 450000.00, 20),
('P007', 'Bibit Cabai Merah', 'Bibit', 25000.00, 150),
('P008', 'Pupuk Kandang Organik 25kg', 'Pupuk', 120000.00, 40),
('P009', 'Benih Tomat Unggul', 'Benih', 45000.00, 90),
('P010', 'Fungisida Copper 1L', 'Pestisida', 180000.00, 35);
```

### 5. Query Examples

#### Cari semua produk kategori Pupuk
```sql
SELECT * FROM products WHERE category = 'Pupuk' ORDER BY price;
```

#### Lihat laporan penjualan harian
```sql
SELECT 
    DATE(created_at) as tanggal,
    COUNT(*) as jumlah_transaksi,
    SUM(total) as total_penjualan,
    payment_method
FROM transactions 
WHERE DATE(created_at) = CURRENT_DATE
GROUP BY DATE(created_at), payment_method;
```

#### Detail transaksi dengan items
```sql
SELECT 
    t.transaction_id,
    t.created_at,
    u.name as kasir,
    ti.product_name,
    ti.quantity,
    ti.unit_price,
    ti.subtotal,
    t.total,
    t.payment_method
FROM transactions t
JOIN users u ON t.cashier_id = u.id
JOIN transaction_items ti ON t.id = ti.transaction_id
ORDER BY t.created_at DESC;
```

#### Cek stok produk yang menipis (< 20)
```sql
SELECT code, name, stock FROM products WHERE stock < 20 ORDER BY stock;
```

### 6. Connection String

**PostgreSQL JDBC URL:**
```
jdbc:postgresql://localhost:5432/agripos
```

**Configuration:**
- Host: localhost
- Port: 5432
- Database: agripos
- User: postgres
- Password: postgres

**Connection dalam Java:**
```java
Connection connection = DriverManager.getConnection(
    "jdbc:postgresql://localhost:5432/agripos",
    "postgres",
    "postgres"
);
```

### 7. Backup & Migration

#### Backup Database
```bash
pg_dump -U postgres agripos > agripos_backup.sql
```

#### Restore Database
```bash
psql -U postgres agripos < agripos_backup.sql
```

#### Run Schema Script
```bash
psql -U postgres -d agripos -f schema.sql
```

### 8. Constraints & Validations

**Data Integrity:**
- Price harus > 0 (CHECK constraint)
- Stock harus >= 0 (CHECK constraint)
- Code unik per produk (UNIQUE constraint)
- Username unik per user (UNIQUE constraint)
- Role hanya CASHIER atau ADMIN (CHECK constraint)
- Status transaksi hanya PENDING/COMPLETED/CANCELLED (CHECK constraint)

**Foreign Key Constraints:**
- transactions.cashier_id → users.id
- transaction_items.transaction_id → transactions.id (with ON DELETE CASCADE)

---
