# Agri-POS – User Guide

## 1. Login
Sistem Agri-POS menggunakan mekanisme autentikasi untuk membedakan akses pengguna berdasarkan role.

### Kredensial Default
- **Kasir**: Username: `kasir001`, Password: `kasir123`
- **Admin**: Username: `admin001`, Password: `admin123`

### Alur Login
1. Buka aplikasi Agri-POS
2. Masukkan username dan password
3. Klik tombol "Login"
4. Sistem akan memvalidasi kredensial ke database
5. Jika valid, pengguna akan diarahkan ke halaman utama sesuai role

### Role & Hak Akses
- **Kasir**: 
  - Akses ke tab Transaksi (untuk melakukan penjualan)
  - Akses ke Laporan Harian (untuk melihat rekapitulasi harian)
  
- **Admin**: 
  - Akses ke tab Manajemen Produk (CRUD produk)
  - Akses ke Laporan Lengkap (laporan periode)

---

## 2. Transaksi (Kasir)

### Alur Penjualan Lengkap

#### a. Memilih Produk
1. Pada tab **Transaksi**, lihat daftar produk yang tersedia
2. Setiap produk menampilkan: Nama, Harga, Stok
3. Klik produk atau gunakan tombol "Tambah ke Keranjang"

#### b. Mengatur Jumlah
1. Masukkan jumlah (quantity) di kolom yang tersedia
2. Sistem akan menampilkan total harga (qty × harga satuan)
3. Pastikan stok mencukupi; jika tidak, pesan error akan ditampilkan

#### c. Menambah ke Keranjang
1. Klik tombol "Tambah ke Keranjang"
2. Produk akan masuk ke keranjang belanja
3. Jumlah item di keranjang akan terupdate

#### d. Melihat Keranjang
- Keranjang menampilkan:
  - List produk yang dipilih
  - Qty masing-masing produk
  - Harga satuan dan subtotal
  - **Total Pembayaran**

#### e. Menghapus Item dari Keranjang
1. Klik tombol "Hapus" atau "X" di sebelah item yang ingin dihapus
2. Item akan dihapus dan total akan diperbarui

#### f. Memilih Metode Pembayaran
1. Sebelum checkout, pilih metode pembayaran:
   - **Tunai (Cash)**: Pembayaran langsung dengan uang tunai
   - **E-Wallet**: Pembayaran menggunakan dompet digital
2. Sistem akan menampilkan form pembayaran sesuai metode

#### g. Melakukan Pembayaran
1. **Untuk Tunai**:
   - Masukkan jumlah uang yang diterima
   - Sistem otomatis menghitung kembalian
   - Klik tombol "Checkout"

2. **Untuk E-Wallet**:
   - Masukkan nomor akun/referensi
   - Konfirmasi pembayaran
   - Klik tombol "Checkout"

#### h. Transaksi Berhasil
1. Struk (receipt) akan ditampilkan sebagai popup
2. Struk mencakup:
   - Nomor transaksi
   - Tanggal & waktu
   - Detail produk (nama, qty, harga)
   - Total pembayaran
   - Metode pembayaran
   - Kembalian (jika tunai)
3. Opsi cetak atau tutup struk
4. Transaksi tersimpan otomatis ke database
5. Stok produk akan terupdate

#### i. Pesan Error Umum
- **"Stok tidak cukup"**: Qty melebihi stok tersedia
- **"Keranjang kosong"**: Tambahkan produk sebelum checkout
- **"Jumlah pembayaran kurang"**: Jumlah uang tidak cukup untuk total belanja

---

## 3. Manajemen Produk (Admin)

### Melihat Daftar Produk
1. Login sebagai Admin
2. Buka tab **Produk**
3. Tabel menampilkan semua produk dengan kolom:
   - ID Produk
   - Nama Produk
   - Kategori
   - Harga
   - Stok

### Menambah Produk Baru
1. Klik tombol **"Tambah Produk"**
2. Isi form dengan data:
   - Nama Produk (wajib)
   - Kategori (pilih dari dropdown)
   - Harga (rupiah, wajib)
   - Stok Awal (unit, wajib)
   - Deskripsi (opsional)
3. Klik tombol **"Simpan"**
4. Produk akan ditambahkan ke database dan tabel akan refresh

### Mengubah Produk
1. Klik tombol **"Edit"** pada produk yang ingin diubah
2. Form akan menampilkan data produk saat ini
3. Ubah field yang diperlukan
4. Klik **"Update"** untuk menyimpan perubahan
5. Tabel akan refresh dengan data terbaru

### Menghapus Produk
1. Klik tombol **"Hapus"** pada produk yang ingin dihapus
2. Konfirmasi penghapusan akan muncul
3. Klik **"Ya, Hapus"** untuk melanjutkan
4. Produk akan dihapus dari database
5. Tabel akan refresh

### Catatan Penting
- Tidak bisa menambah produk dengan nama duplikat
- Stok tidak boleh negatif
- Harga harus lebih dari 0
- Jika produk sudah terjual, hindari penghapusan; gunakan update stok 0 saja

---

## 4. Laporan

### Laporan Harian (Kasir)
1. Login sebagai Kasir
2. Buka tab **Laporan**
3. Klik tombol **"Lihat Laporan Hari Ini"**
4. Sistem akan menampilkan:
   - Tanggal laporan
   - Jumlah transaksi hari ini
   - Total penjualan
   - Detail transaksi per jam
   - Breakdown metode pembayaran
5. Opsi untuk cetak laporan

### Laporan Lengkap (Admin)
1. Login sebagai Admin
2. Buka tab **Laporan**
3. Pilih periode laporan (opsional):
   - Tanggal mulai
   - Tanggal selesai
4. Klik **"Generate Laporan"**
5. Laporan menampilkan:
   - Total transaksi dalam periode
   - Total penjualan
   - Produk terlaris
   - Perbandingan metode pembayaran
   - Tren penjualan harian
6. Opsi ekspor ke PDF atau print

---

## 5. Troubleshooting

### Login Gagal
- **Masalah**: Username/password salah
- **Solusi**: Periksa kembali kredensial; jika lupa, minta reset ke administrator

### Stok Tidak Terlihat
- **Masalah**: Kolom stok kosong atau 0
- **Solusi**: Pastikan admin sudah menambahkan produk dan stok awal di halaman Manajemen Produk

### Transaksi Tidak Tersimpan
- **Masalah**: Setelah checkout, transaksi tidak muncul di laporan
- **Solusi**: 
  - Cek koneksi database
  - Pastikan tidak ada error pada saat checkout
  - Lihat log aplikasi untuk error detail

### Struk Tidak Muncul
- **Masalah**: Popup struk tidak tampil setelah checkout
- **Solusi**:
  - Cek bahwa popup tidak terblokir oleh window lain
  - Restart aplikasi
  - Periksa event listener di kode checkout

### Laporan Kosong
- **Masalah**: Laporan menampilkan 0 transaksi meski ada penjualan
- **Solusi**:
  - Pastikan tanggal transaksi sesuai dengan tanggal laporan
  - Cek bahwa transaksi tersimpan di database
  - Gunakan tools admin untuk verifikasi data

---

## 6. Panduan Cepat Keyboard
- **Enter**: Konfirmasi / Checkout
- **Esc**: Batalkan / Tutup popup
- **Tab**: Navigasi antar field
- **Ctrl+P**: Print struk/laporan
