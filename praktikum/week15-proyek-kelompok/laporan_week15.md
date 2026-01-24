# Laporan Proyek Kelompok - Agri-POS
**Topik:** Desain & Implementasi Sistem Point-of-Sale untuk Produk Pertanian

---

## 1. Identitas Kelompok

| Nama | NIM | Peran |
|--- | --- | --- |
| As Syifa Dian Rinesti | 240320559 | Manajemen Produk |
| Azizzah Nurul Putri | 240320560 | Exception & Validasi |
| Azzahra Ramadhani | 240320561 | Login & Hak Akses |
| Bunga Maura Aulya | 240320562 | Metode Pembayaran |
| Difa Rizkiana Fauziyah | 240320564 | Transaksi & Kasir |
| Rossa Aqila Zahra | 240320568 | Struk, Laporan & Dokumentasi |

---

## 2. Ringkasan Sistem

Agri-POS adalah aplikasi Point-of-Sale desktop (JavaFX) untuk toko produk pertanian.
Tujuan: memfasilitasi kasir dan admin dalam melakukan penjualan, mengelola produk, dan menghasilkan laporan.

Fitur utama (implemented):
- FR-1: Manajemen Produk (CRUD)
- FR-2: Transaksi Penjualan dengan keranjang (Cart)
- FR-3: Metode Pembayaran (Cash, E-Wallet) — extensible via Strategy Pattern
- FR-4: Struk & Laporan Penjualan (ReceiptService)
- FR-5: Login & Role-Based Access (Admin, Cashier)

Integrasi teknis: Java 17+ / JavaFX, Maven, PostgreSQL, JDBC, JUnit untuk unit test.

---

## 3. Desain Sistem

Arsitektur lapisan:
- View (JavaFX) — `view` package
- Controller — `controller` package
- Service / Business Logic — `service` package
- DAO / Persistence — `dao` package (interface + `Jdbc*` implementations)
- Model — `model` package

Design decisions:
- DAO pattern untuk akses DB dan kemudahan mocking/testing
- Strategy pattern untuk metode pembayaran (mempermudah penambahan metode baru)
- Single Responsibility pada tiap service class


---

## 4. UML & Diagram
1. Use Case

![Screenshot hasil](/praktikum/week15-proyek-kelompok/screenshots/use%20case.png)

2. Activity Diagram

![Screenshot hasil](/praktikum/week15-proyek-kelompok/screenshots/activity%20diagram.png)

3. Class diagram

![Screenshot hasil](/praktikum/week15-proyek-kelompok/screenshots/class%20diagram.png)

4. Sequence diagram (Checkout flow)

![Screenshot hasil](/praktikum/week15-proyek-kelompok/screenshots/sequence%20diagram.png)

5. Sequence diagram (Manajemen Produk)

![Screenshot hasil](/praktikum/week15-proyek-kelompok/screenshots/sequence%20manajemen%20produk.png)

6. ERD

![Screenshot hasil](/praktikum/week15-proyek-kelompok/screenshots/ERD%20Agri-POS.drawio.png)


---

## 5. Database (DDL excerpt)

Database lengkap ada di `sql/schema.sql`. Berikut cuplikan DDL utama:

```sql
-- users
CREATE TABLE users (
	id SERIAL PRIMARY KEY,
	username VARCHAR(50) UNIQUE NOT NULL,
	password_hash VARCHAR(255) NOT NULL,
	role VARCHAR(20) NOT NULL -- 'ADMIN' | 'CASHIER'
);

-- products
CREATE TABLE products (
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	price NUMERIC(12,2) NOT NULL,
	stock INT NOT NULL
);

-- transactions
CREATE TABLE transactions (
	id SERIAL PRIMARY KEY,
	user_id INT REFERENCES users(id),
	total NUMERIC(12,2) NOT NULL,
	payment_method VARCHAR(50),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- transaction_items
CREATE TABLE transaction_items (
	id SERIAL PRIMARY KEY,
	transaction_id INT REFERENCES transactions(id),
	product_id INT REFERENCES products(id),
	quantity INT NOT NULL,
	subtotal NUMERIC(12,2) NOT NULL
);
```


---

## 6. Testing

6.1 Unit tests
- Lokasi: `test/java/com/upb/agripos` (contoh: `CartServiceTest`)
- Hasil 
![Screenshot hasil](/praktikum/week15-proyek-kelompok/screenshots/Junit.png)

6.2 Manual test cases (minimal 8)

1) Login sukses (Kasir)
- Steps: buka aplikasi → masukkan username/password kasir valid → login
- Expected: Masuk ke tampilan kasir

2) Login gagal (invalid credentials)
- Steps: masukkan username/password salah
- Expected: Notifikasi error, tidak boleh login

3) CRUD Produk (Tambah Produk)
- Steps: login sebagai admin → buka manajemen produk → tambah produk baru
- Expected: Produk tersimpan, muncul di daftar

4) CRUD Produk (Edit Produk)
- Steps: pilih produk → ubah harga/stock → simpan
- Expected: Perubahan tersimpan

5) Tambah item ke keranjang
- Steps: pilih produk → tambahkan qty ke cart
- Expected: Item muncul di cart dengan subtotal benar

6) Transaksi checkout tunai
- Steps: checkout pilih Cash → masukkan jumlah tunai → konfirmasi
- Expected: Transaksi tersimpan, struk muncul, stok berkurang

7) Transaksi checkout e-wallet
- Steps: checkout pilih E-Wallet → proses (simulasi) sukses
- Expected: Transaksi tersimpan, struk muncul

8) Validasi stok (OutOfStockException)
- Steps: masukkan qty lebih besar dari stock → checkout
- Expected: Muncul error `OutOfStockException`, transaksi dibatalkan


6.3 Evidence yang disertakan
- Unit test report: [target/surefire-reports/com.upb.agripos.CartServiceTest.txt](target/surefire-reports/com.upb.agripos.CartServiceTest.txt)


---

## 7. Traceability Matrix (Detailed)

| FR ID | Fitur | Dokumen | Implementasi | Test Case |
|-------|-------|---------|--------------|----------|
| FR-1 | Manajemen Produk | docs/01_srs.md, docs/03_database.md | `ProductService`, `JdbcProductDAO`, `view/PosView` | TC-3, TC-4 |
| FR-2 | Transaksi Penjualan | docs/01_srs.md | `CartService`, `Transaction`, `JdbcTransactionDAO` | TC-5, TC-6, TC-7 |
| FR-3 | Metode Pembayaran | docs/01_srs.md | `PaymentMethod` interface, `CashPayment`, `EWalletPayment` | TC-6, TC-7 |
| FR-4 | Struk & Laporan | docs/01_srs.md | `ReceiptService` | TC-6, TC-7, report generation |
| FR-5 | Login & Hak Akses | docs/01_srs.md | `AuthService`, `UserDAO` | TC-1, TC-2 |

---

## 8. Kontribusi & Bukti

- Detail kontribusi tiap anggota: lihat [docs/08_contribution.md](docs/08_contribution.md)
- Bukti unit test: `target/surefire-reports/com.upb.agripos.CartServiceTest.txt`
- Commit/PR: tambahkan link PR/commit pada `docs/08_contribution.md` bila tersedia (disarankan untuk tiap tugas utama)

---

## 9. Kendala & Solusi

- Kendala: Integrasi JavaFX dengan testing otomatis — Solusi: fokus testing pada service/logic (unit test) dan lakukan manual test pada GUI.
- Kendala: Konsistensi schema awal — Solusi: perbaiki schema (`transaction_items`) dan update DAO.
- Kendala: Extensibility pembayaran — Solusi: Strategy Pattern pada `payment` package.

---

## Lampiran & Instruksi Perlu Tindak Lanjut

- File DDL lengkap: [sql/schema.sql](sql/schema.sql)
- Unit test report: [target/surefire-reports/com.upb.agripos.CartServiceTest.txt](target/surefire-reports/com.upb.agripos.CartServiceTest.txt)

---



