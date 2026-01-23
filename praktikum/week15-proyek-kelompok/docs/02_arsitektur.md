# 02 - Arsitektur Sistem

## Agri-POS Architecture Design

### 1. Arsitektur Berlapis (Layered Architecture)

Agri-POS mengikuti pola MVC (Model-View-Controller) dengan pemisahan layer yang jelas:

```
┌─────────────────────────────────────────────┐
│           Presentation Layer (View)          │
│           - PosView (JavaFX GUI)            │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│           Control Layer (Controller)         │
│           - PosController                    │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│           Business Logic Layer (Service)     │
│  - ProductService                            │
│  - CartService                               │
│  - PaymentService                            │
│  - ReceiptService                            │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│           Data Access Layer (DAO)            │
│  - ProductDAO (Interface)                    │
│  - JdbcProductDAO (Implementation)           │
│  - TransactionDAO (Interface)                │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│           Database Layer                     │
│           - PostgreSQL Database              │
└─────────────────────────────────────────────┘
```

### 2. Package Structure

```
com.upb.agripos
├── model/
│   ├── Product.java
│   ├── CartItem.java
│   ├── Cart.java
│   └── Transaction.java
├── dao/
│   ├── ProductDAO.java (Interface)
│   ├── JdbcProductDAO.java (Implementation)
│   ├── TransactionDAO.java (Interface)
│   └── JdbcTransactionDAO.java (Implementation)
├── service/
│   ├── ProductService.java
│   ├── CartService.java
│   ├── PaymentService.java
│   └── ReceiptService.java
├── payment/
│   ├── PaymentMethod.java (Interface - Strategy)
│   ├── CashPayment.java (Implementation)
│   └── EWalletPayment.java (Implementation)
├── exception/
│   ├── ValidationException.java
│   ├── OutOfStockException.java
│   └── PaymentException.java
├── controller/
│   └── PosController.java
├── view/
│   └── PosView.java
└── AppJavaFX.java (Main Entry Point)
```

### 3. Dependency Rules (SOLID - DIP)

**Dependency Flow (High-level → Low-level):**
- View depends on Controller
- Controller depends on Service
- Service depends on DAO (via interface)
- DAO implements concrete database access

**Inversion of Control:**
```
View → Controller → Service → DAO (Interface)
                              ↓
                        JdbcProductDAO (Concrete)
```

**Key Principle:** High-level modules tidak bergantung pada low-level modules. Keduanya bergantung pada abstraksi (interface).

### 4. Class Responsibilities

#### Model Layer
- **Product**: Representasi produk pertanian
- **CartItem**: Item dalam keranjang dengan qty
- **Cart**: Kumpulan CartItem dengan operasi CRUD
- **Transaction**: Representasi transaksi penjualan

#### DAO Layer
- **ProductDAO**: Interface operasi CRUD produk
- **JdbcProductDAO**: Implementasi JDBC untuk ProductDAO
- **TransactionDAO**: Interface operasi transaksi

#### Service Layer
- **ProductService**: Business logic manajemen produk
- **CartService**: Business logic keranjang belanja
- **PaymentService**: Business logic pembayaran
- **ReceiptService**: Business logic pembuatan struk

#### Payment (Strategy Pattern)
- **PaymentMethod**: Interface untuk berbagai metode pembayaran
- **CashPayment**: Implementasi pembayaran tunai
- **EWalletPayment**: Implementasi pembayaran e-wallet
→ Memudahkan penambahan metode pembayaran baru tanpa mengubah kode inti

#### Exception
- **ValidationException**: Error validasi input
- **OutOfStockException**: Error stok tidak cukup
- **PaymentException**: Error proses pembayaran

#### Controller Layer
- **PosController**: Orchestrator antara View dan Service
  - Menangani event dari GUI
  - Memanggil business logic di Service
  - Menampilkan hasil ke View

#### View Layer
- **PosView**: GUI menggunakan JavaFX
  - TableView untuk daftar produk dan keranjang
  - Input fields untuk quantity dan amount
  - Tombol action untuk add to cart, checkout, clear
  - TextArea untuk display struk

### 5. SOLID Principles Penerapan

#### S - Single Responsibility Principle
- ProductService: hanya handle product logic
- CartService: hanya handle cart logic
- PaymentService: hanya handle payment logic
- PosController: koordinasi antar layer
- Setiap exception class punya tanggung jawab spesifik

#### O - Open/Closed Principle
- **PaymentMethod interface** terbuka untuk extension (CashPayment, EWalletPayment)
- Penambahan metode pembayaran baru tidak mengubah existing code
- **DAO interface** terbuka untuk implementasi berbeda (JDBC, ORM, mock, etc)

#### L - Liskov Substitution Principle
- `CashPayment` dan `EWalletPayment` dapat menggantikan `PaymentMethod` tanpa error
- `JdbcProductDAO` dapat menggantikan `ProductDAO` tanpa mengubah logic
- Semua implementasi interface konsisten dengan kontrak interface

#### I - Interface Segregation Principle
- **ProductDAO**: interface spesifik untuk product operations
- **TransactionDAO**: interface spesifik untuk transaction operations
- Client hanya bergantung pada method yang diperlukan, tidak ada "fat interface"

#### D - Dependency Inversion Principle
- Service tidak bergantung pada concrete JdbcProductDAO
- Service bergantung pada abstraksi ProductDAO interface
- DAO injected ke Service via constructor
- Database query detail tersembunyi di DAO layer

### 6. Data Flow Diagram

#### Flow: Add to Cart
```
User Input (Select Product + Qty)
         ↓
View.onAddToCartClicked()
         ↓
Controller.handleAddToCart()
         ↓
Service.addToCart(product, qty)
         ↓
Cart.addItem(product, qty)
         ↓
Update View Display (CartTable, Total)
```

#### Flow: Checkout
```
User Input (Amount Paid + Payment Method)
         ↓
View.onCheckoutClicked()
         ↓
Controller.handleCheckout()
         ↓
Service.processCheckout(items, total, method, amount)
         ↓
PaymentMethod.processPayment(amount)
         ↓
Calculate Change
         ↓
ReceiptService.generateReceipt()
         ↓
Update View (Display Receipt, Clear Cart)
         ↓
Clear InputFields
```

### 7. Design Patterns Used

#### 1. Strategy Pattern (Payment Methods)
**Digunakan untuk:** Menangani berbagai metode pembayaran dengan cara yang extensible

```java
PaymentMethod paymentMethod = (method.equals("CASH")) 
    ? new CashPayment() 
    : new EWalletPayment(balance);
paymentMethod.processPayment(amount);
```

Keuntungan:
- Menambah metode pembayaran baru tanpa memodifikasi code existing
- Setiap metode punya implementasi logic yang independen
- Runtime dapat memilih metode yang tepat

#### 2. MVC Pattern (Model-View-Controller)
**Digunakan untuk:** Pemisahan antara data, presentasi, dan logic

- **Model**: Product, CartItem, Cart, Transaction
- **View**: PosView (JavaFX)
- **Controller**: PosController

#### 3. Singleton Pattern (Database Connection)
**Digunakan untuk:** Memastikan hanya ada satu koneksi database

```java
Connection connection = DriverManager.getConnection(...)
// Dijaga dalam AppJavaFX, di-pass ke DAO
```

#### 4. Data Access Object (DAO) Pattern
**Digunakan untuk:** Abstraksi akses data dari business logic

```java
Interface: ProductDAO
Implementation: JdbcProductDAO
```

### 8. Error Handling Strategy

**Exception Hierarchy:**
```
Exception
├── ValidationException (input/business rule violation)
├── OutOfStockException (inventory related)
└── PaymentException (payment process error)
```

**Try-Catch Strategy:**
- Service layer: throws Exception (propogates to Controller)
- Controller layer: catches Exception, shows Alert dialog
- View layer: displays error message to user

### 9. Dependency Injection

```java
// Constructor Injection
ProductService productService = new ProductService(productDAO);
CartService cartService = new CartService();
new PosController(productService, cartService, view);
```

Keuntungan:
- Loose coupling antar component
- Mudah di-mock untuk testing
- Easy to swap implementations

---
