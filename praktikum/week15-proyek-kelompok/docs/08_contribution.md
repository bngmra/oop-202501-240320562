# Agri-POS  Contribution

## Anggota & Peran
| Nama | NIM | Peran |
|--- | --- | --- |
| As Syifa Dian Rinesti | 240320559 | Manajemen Produk |
| Azizzah Nurul Putri | 240320560 | Exception & Validasi |
| Azzahra Ramadhani | 240320561 | Login & Hak Akses |
| Bunga Maura Aulya | 240320562 | Metode Pembayaran |
| Difa Rizkiana Fauziyah | 240320564 | Transaksi & Kasir |
| Rossa Aqila Zahra | 240320568 | Struk, Laporan & Dokumentasi |

## Rincian Kontribusi Per Anggota

### As Syifa Dian Rinesti  Manajemen Produk
- Tugas: Desain model `Product`, CRUD produk di UI, integrasi dengan database.
- Implementasi utama:
  - `src/main/java/com/upb/agripos/model/Product.java`
  - `src/main/java/com/upb/agripos/dao/ProductDAO.java`
  - `src/main/java/com/upb/agripos/dao/JdbcProductDAO.java`
  - `src/main/java/com/upb/agripos/service/ProductService.java`
  - Bagian produk di `src/main/java/com/upb/agripos/view/PosView.java`
- Bukti: commit berisi "feat(product): implement product CRUD with database integration".

### Azizzah Nurul Putri  Exception
- Tugas: Rancang dan implementasi custom exceptions serta error propagation.
- Implementasi utama:
  - `src/main/java/com/upb/agripos/exception/ValidationException.java`
  - `src/main/java/com/upb/agripos/exception/OutOfStockException.java`
  - `src/main/java/com/upb/agripos/exception/PaymentException.java`
  - Menambahkan penanganan exception di service/controller.
- Bukti: commit berisi "feat(exception): implement custom exception hierarchy".

### Azzahra Ramadhani  Login dan Hak Akses
- Tugas: Implementasi autentikasi, otorisasi, dan scene switching berdasarkan role.
- Implementasi utama:
  - `src/main/java/com/upb/agripos/model/User.java`
  - `src/main/java/com/upb/agripos/dao/UserDAO.java`
  - `src/main/java/com/upb/agripos/dao/JdbcUserDAO.java`
  - `src/main/java/com/upb/agripos/service/AuthService.java`
  - `src/main/java/com/upb/agripos/view/LoginView.java`
  - `src/main/java/com/upb/agripos/controller/AuthController.java`
- Bukti: commit berisi "feat(auth): implement user model and UserDAO" dan "feat(login): implement LoginView".

### Bunga Maura Aulya  Metode Pembayaran
- Tugas: Rancang pattern pembayaran (Strategy), implementasi `CashPayment` & `EWalletPayment`.
- Implementasi utama:
  - `src/main/java/com/upb/agripos/payment/PaymentMethod.java`
  - `src/main/java/com/upb/agripos/payment/CashPayment.java`
  - `src/main/java/com/upb/agripos/payment/EWalletPayment.java`
  - `src/main/java/com/upb/agripos/service/PaymentService.java`
  - Integrasi di `src/main/java/com/upb/agripos/controller/PosController.java`
- Bukti: commit berisi "feat(payment): implement Strategy pattern for payment methods".

### Difa Rizkiana Fauziyah  Transaksi Penjualan
- Tugas: Implementasi cart, processing checkout, dan persistence transaksi ke DB.
- Implementasi utama:
  - `src/main/java/com/upb/agripos/model/Cart.java`
  - `src/main/java/com/upb/agripos/model/CartItem.java`
  - `src/main/java/com/upb/agripos/model/Transaction.java`
  - `src/main/java/com/upb/agripos/service/CartService.java`
  - `src/main/java/com/upb/agripos/dao/TransactionDAO.java`
  - `src/main/java/com/upb/agripos/dao/JdbcTransactionDAO.java`
  - Checkout integration di `PosController.java`
- Bukti: commit berisi "feat(service): add CartService" dan "feat(dao): implement TransactionDAO".

### Rossa Aqila Zahra  Struk dan Laporan
- Tugas: Generate receipt (struk) setelah checkout dan implementasi laporan harian/periodik.
- Implementasi utama:
  - `src/main/java/com/upb/agripos/service/ReceiptService.java`
  - `src/main/java/com/upb/agripos/service/ReportService.java`
  - UI: receipt popup & laporan view in `PosView`/`PosController`
- Bukti: commit berisi "feat(receipt): implement ReceiptService" dan "feat(report): add ReportService".

## Bukti Kolaborasi & Referensi Commit
- Periksa riwayat commit di repository untuk detail PR/commit per anggota.

![Screenshot hasil](/screenshots/bukti%20pembagian%20commit.png)



## Catatan Tambahan
- Semua anggota berkolaborasi untuk integrasi (checkout  payment  transaction  receipt  report).
- Dokumentasi terkait: `docs/06_user_guide.md`, `docs/07_runbook.md`, `docs/04_test_plan.md`.

---
