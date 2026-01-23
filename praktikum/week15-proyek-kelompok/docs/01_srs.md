# 01 - Software Requirements Specification (SRS)
## Agri-POS: Aplikasi Point of Sale Pertanian

### 1. Ringkasan Eksekutif
**Agri-POS** adalah aplikasi Point of Sale (POS) berbasis desktop yang dirancang untuk memudahkan transaksi penjualan di toko pertanian. Aplikasi ini mengintegrasikan GUI modern (JavaFX), database relasional (PostgreSQL), dan arsitektur berlapis yang mengikuti prinsip SOLID.

**Tujuan Utama:**
- Menyediakan sistem kasir yang efisien untuk transaksi penjualan produk pertanian
- Mengelola inventori produk dengan akurat
- Mendukung berbagai metode pembayaran (tunai dan e-wallet)
- Menghasilkan laporan penjualan untuk keperluan administrasi

---

### 2. Scope & Batasan
#### Dalam Scope (In Scope):
- FR-1: Manajemen Produk (CRUD)
- FR-2: Transaksi Penjualan & Keranjang Belanja
- FR-3: Metode Pembayaran (Tunai, E-Wallet)
- FR-4: Struk & Laporan Dasar
- FR-5: Login & Hak Akses (Kasir & Admin)

#### Di Luar Scope (Out of Scope):
- Integrasi payment gateway eksternal (hanya mock)
- Multi-toko atau multi-warehouse
- Aplikasi mobile
- Integrasi real-time dengan supplier

---

### 3. Functional Requirements (FR)

#### FR-1: Manajemen Produk
**Deskripsi:** Sistem memungkinkan admin untuk mengelola data produk pertanian.

**Functional Requirements:**
- FR-1.1: Admin dapat menambah produk baru dengan data: kode, nama, kategori, harga, stok
- FR-1.2: Admin dapat melihat daftar semua produk
- FR-1.3: Admin dapat mengubah data produk yang sudah ada
- FR-1.4: Admin dapat menghapus produk dari sistem
- FR-1.5: Sistem mencegah duplikasi kode produk

**Acceptance Criteria:**
- Produk berhasil disimpan ke database
- Data produk tampil dengan benar di tabel
- Validasi input berjalan sebelum penyimpanan
- Kode produk unik (tidak ada duplikasi)

#### FR-2: Transaksi Penjualan
**Deskripsi:** Kasir dapat melakukan transaksi penjualan dengan menambahkan produk ke keranjang dan menghitung total.

**Functional Requirements:**
- FR-2.1: Kasir dapat memilih produk dari daftar
- FR-2.2: Kasir dapat menambahkan produk ke keranjang dengan qty
- FR-2.3: Kasir dapat melihat item dalam keranjang
- FR-2.4: Kasir dapat mengubah qty item dalam keranjang
- FR-2.5: Kasir dapat menghapus item dari keranjang
- FR-2.6: Sistem menghitung total belanja otomatis
- FR-2.7: Kasir dapat mengosongkan keranjang

**Acceptance Criteria:**
- Item dapat ditambahkan ke keranjang
- Total belanja dihitung dengan benar
- Validasi stok sebelum penambahan item
- Item dapat diubah atau dihapus

#### FR-3: Metode Pembayaran
**Deskripsi:** Sistem mendukung pembayaran tunai dan e-wallet dengan desain extensible.

**Functional Requirements:**
- FR-3.1: Kasir dapat memilih metode pembayaran (Tunai/E-Wallet)
- FR-3.2: Sistem mendukung pembayaran Tunai
- FR-3.3: Sistem mendukung pembayaran E-Wallet
- FR-3.4: Sistem menghitung kembalian untuk pembayaran tunai
- FR-3.5: Sistem validasi saldo untuk e-wallet
- FR-3.6: Desain menggunakan Strategy Pattern untuk extensibility

**Acceptance Criteria:**
- Pengguna dapat memilih metode pembayaran
- Kembalian dihitung dengan benar
- E-wallet dapat ditambahkan tanpa mengubah kode inti
- Validasi pembayaran berjalan sesuai metode

#### FR-4: Struk & Laporan
**Deskripsi:** Setelah pembayaran berhasil, sistem menampilkan struk dan admin dapat melihat laporan.

**Functional Requirements:**
- FR-4.1: Sistem menampilkan struk setelah checkout berhasil
- FR-4.2: Struk berisi detail transaksi (item, qty, harga, total)
- FR-4.3: Admin dapat melihat laporan penjualan harian
- FR-4.4: Laporan menampilkan total penjualan per hari

**Acceptance Criteria:**
- Struk ditampilkan dengan format yang rapi
- Semua detail transaksi ada di struk
- Laporan dapat diakses dari menu admin

#### FR-5: Login & Hak Akses
**Deskripsi:** Sistem membedakan hak akses antara Kasir dan Admin.

**Functional Requirements:**
- FR-5.1: Pengguna dapat login dengan username dan password
- FR-5.2: Ada dua role: Kasir dan Admin
- FR-5.3: Kasir hanya dapat mengakses halaman transaksi
- FR-5.4: Admin dapat mengakses manajemen produk dan laporan
- FR-5.5: Sistem validasi kredensial pengguna

**Acceptance Criteria:**
- Login berhasil dengan kredensial benar
- Pengguna tidak bisa login dengan kredensial salah
- Menu sesuai dengan role pengguna
- Logout berjalan dengan baik

---

### 4. Non-Functional Requirements (NFR)

#### NFR-1: Maintainability
- Kode mengikuti prinsip SOLID
- Setiap class memiliki single responsibility
- Mudah dilacak dan dipahami

#### NFR-2: Extensibility
- Penambahan metode pembayaran baru tidak mengubah kode inti
- Implementasi Open/Closed Principle

#### NFR-3: Reliability
- Validasi input lengkap untuk setiap transaksi
- Error handling dengan exception yang tepat
- Database transaction consistency

#### NFR-4: Performance
- Tampilan list produk responsif (< 1 detik)
- Checkout process < 2 detik

#### NFR-5: Security
- Password tidak disimpan plain text (dalam implementasi real, gunakan hash)
- Prepared statement untuk mencegah SQL injection
- Role-based access control

#### NFR-6: Usability
- Interface intuitif dan mudah dipahami
- Pesan error yang jelas
- Button dan menu yang konsisten

---

### 5. Constraint & Assumption

**Constraints:**
- Database: PostgreSQL
- GUI Framework: JavaFX 21
- Java Version: JDK 21+
- Build Tool: Maven

**Assumptions:**
- Pengguna memiliki kredensial valid untuk login
- Database sudah terkonfigurasi dan berjalan
- Network connectivity stabil
- E-wallet adalah mock (untuk demo)

---

### 6. Glossary

| Term | Definisi |
|------|----------|
| Produk | Item yang dijual (benih, pupuk, pestisida, alat) |
| Keranjang | Koleksi item yang akan dibeli customer |
| Transaksi | Proses penjualan dari mulai pemilihan item hingga pembayaran |
| Struk | Bukti transaksi yang dicetak/ditampilkan |
| Kasir | Pengguna yang melakukan transaksi penjualan |
| Admin | Pengguna yang mengelola produk dan melihat laporan |

---
