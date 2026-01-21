# 04 - Test Plan & Test Cases

## Agri-POS Test Plan

### 1. Testing Strategy

**Tipe Testing:**
1. Unit Testing: Menguji individual method CartService dengan data test
2. Integration Testing: Menguji interaksi CartService ↔ Product Model
3. Manual Testing: Menguji GUI kasir dan end-to-end transaction flow

**Tools:**
- JUnit 5: Unit testing framework
- Static test data: Product test fixtures

### 2. Test Scope

**In Scope:**
- CartService layer (addToCart, removeFromCart, updateCartItemQuantity, clearCart)
- Cart business logic (calculation, validation, quantity management)
- Exception handling (ValidationException untuk input invalid)
- Edge cases (null product, zero/negative quantity, duplicate product)

**Out of Scope:**
- GUI component testing (manual testing saja)
- Database persistence
- Payment processing
- Performance/load testing

### 3. Unit Test Cases (CartServiceTest.java)

#### TC-UNIT-01: Add Single Product to Cart
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-01 |
| **Method** | testAddToCart() |
| **Purpose** | Memverifikasi penambahan 1 produk ke keranjang kosong |
| **Test Data** | Product P001 (Benih Padi, Rp50.000), qty: 2 |
| **Steps** | 1. addToCart(product1, 2) |
| **Expected Result** | Cart item count = 1, Cart total = Rp100.000 |
| **Assertion** | assertEquals(1, getCartItemCount())<br/>assertEquals(100000.0, getCartTotal()) |

#### TC-UNIT-02: Add Multiple Different Products
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-02 |
| **Method** | testAddMultipleItems() |
| **Purpose** | Memverifikasi penambahan produk berbeda ke keranjang |
| **Test Data** | P001 qty:1 (Rp50.000) + P002 qty:2 (Rp75.000) |
| **Steps** | 1. addToCart(product1, 1)<br/>2. addToCart(product2, 2) |
| **Expected Result** | Cart item count = 2, Cart total = Rp200.000 (50k+150k) |
| **Assertion** | assertEquals(2, getCartItemCount())<br/>assertEquals(200000.0, getCartTotal()) |

#### TC-UNIT-03: Validate Invalid Quantity (Zero & Negative)
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-03 |
| **Method** | testAddToCartInvalidQuantity() |
| **Purpose** | Memverifikasi validasi kuantitas input |
| **Test Data** | qty: 0, qty: -1 |
| **Steps** | 1. addToCart(product1, 0)<br/>2. addToCart(product1, -1) |
| **Expected Result** | Kedua operasi melempar ValidationException |
| **Assertion** | assertThrows(ValidationException.class, ...) |

#### TC-UNIT-04: Validate Null Product
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-04 |
| **Method** | testAddToCartNullProduct() |
| **Purpose** | Memverifikasi validasi product null |
| **Test Data** | product: null, qty: 1 |
| **Steps** | 1. addToCart(null, 1) |
| **Expected Result** | Melempar ValidationException |
| **Assertion** | assertThrows(ValidationException.class, ...) |

#### TC-UNIT-05: Remove Item from Cart
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-05 |
| **Method** | testRemoveFromCart() |
| **Purpose** | Memverifikasi penghapusan item dari keranjang |
| **Test Data** | Product P001 di keranjang |
| **Steps** | 1. addToCart(product1, 2)<br/>2. removeFromCart("P001") |
| **Expected Result** | Keranjang kosong (isCartEmpty = true) |
| **Assertion** | assertTrue(isCartEmpty()) |

#### TC-UNIT-06: Update Cart Item Quantity
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-06 |
| **Method** | testUpdateCartQuantity() |
| **Purpose** | Memverifikasi update kuantitas item existing |
| **Test Data** | Product P001 initial qty: 1, updated qty: 3 |
| **Steps** | 1. addToCart(product1, 1)<br/>2. updateCartItemQuantity("P001", 3) |
| **Expected Result** | Cart total = Rp150.000 (3 × 50.000) |
| **Assertion** | assertEquals(150000.0, getCartTotal()) |

#### TC-UNIT-07: Clear All Cart Items
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-07 |
| **Method** | testClearCart() |
| **Purpose** | Memverifikasi pengosongan seluruh keranjang |
| **Test Data** | 2 produk berbeda di keranjang |
| **Steps** | 1. addToCart(product1, 2)<br/>2. addToCart(product2, 1)<br/>3. clearCart() |
| **Expected Result** | Keranjang kosong |
| **Assertion** | assertTrue(isCartEmpty()) |

#### TC-UNIT-08: Check Initial Cart Empty State
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-08 |
| **Method** | testIsCartEmpty() |
| **Purpose** | Memverifikasi state keranjang kosong awal |
| **Test Data** | CartService baru tanpa item |
| **Steps** | 1. Cek isCartEmpty() |
| **Expected Result** | Keranjang kosong (true) |
| **Assertion** | assertTrue(isCartEmpty()) |

#### TC-UNIT-09: Calculate Correct Total Price
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-09 |
| **Method** | testGetCartTotal() |
| **Purpose** | Memverifikasi kalkulasi total harga multiple items |
| **Test Data** | P001 qty:2 (100k) + P002 qty:1 (75k) |
| **Steps** | 1. addToCart(product1, 2)<br/>2. addToCart(product2, 1) |
| **Expected Result** | Cart total = Rp175.000 |
| **Assertion** | assertEquals(175000.0, getCartTotal()) |

#### TC-UNIT-10: Add Same Product Increases Quantity
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-10 |
| **Method** | testAddSameProductIncreasesQuantity() |
| **Purpose** | Memverifikasi penambahan produk sama menambah qty, bukan item baru |
| **Test Data** | Product P001 ditambah 2x: qty 1, lalu qty 2 |
| **Steps** | 1. addToCart(product1, 1)<br/>2. addToCart(product1, 2) |
| **Expected Result** | Cart item count = 1 (bukan 2), total qty = 3, total = Rp150.000 |
| **Assertion** | assertEquals(1, getCartItemCount())<br/>assertEquals(150000.0, getCartTotal()) |

#### TC-UNIT-11: Validate Remove with Null/Invalid Code
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-11 |
| **Method** | testRemoveFromCartInvalidCodeThrows() |
| **Purpose** | Memverifikasi validasi product code saat remove |
| **Test Data** | code: null, code: "  " (whitespace) |
| **Steps** | 1. removeFromCart(null)<br/>2. removeFromCart("  ") |
| **Expected Result** | Kedua operasi melempar ValidationException |
| **Assertion** | assertThrows(ValidationException.class, ...) |

#### TC-UNIT-12: Validate Update with Null/Empty Code
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-12 |
| **Method** | testUpdateCartQuantityInvalidCodeThrows() |
| **Purpose** | Memverifikasi validasi product code saat update qty |
| **Test Data** | code: null, code: "" (empty) |
| **Steps** | 1. updateCartItemQuantity(null, 1)<br/>2. updateCartItemQuantity("", 1) |
| **Expected Result** | Kedua operasi melempar ValidationException |
| **Assertion** | assertThrows(ValidationException.class, ...) |

#### TC-UNIT-13: Get Cart Items Contents
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-UNIT-13 |
| **Method** | testGetCartItemsContents() |
| **Purpose** | Memverifikasi retrieval & validasi isi keranjang |
| **Test Data** | 2 produk berbeda: P001, P002 |
| **Steps** | 1. addToCart(product1, 2)<br/>2. addToCart(product2, 1)<br/>3. List<CartItem> items = getCartItems() |
| **Expected Result** | Items size = 2, mengandung produk P001 dan P002 |
| **Assertion** | assertEquals(2, items.size())<br/>assertTrue(hasP001 && hasP002) |

### 4. Manual Test Cases (GUI Testing)

#### TC-MANUAL-01: Add Product via GUI
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-MANUAL-01 |
| **Feature** | Kasir - Tambah Produk ke Keranjang |
| **Precondition** | Aplikasi buka, halaman kasir aktif |
| **Steps** | 1. Pilih P001 (Benih Padi)<br/>2. Input qty: 2<br/>3. Klik "Tambah ke Keranjang" |
| **Expected Result** | Item muncul di keranjang, total: Rp100.000 |

#### TC-MANUAL-02: Remove via GUI
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-MANUAL-02 |
| **Feature** | Kasir - Hapus Item dari Keranjang |
| **Precondition** | Keranjang berisi ≥1 item |
| **Steps** | 1. Pilih item di keranjang<br/>2. Klik "Hapus" |
| **Expected Result** | Item dihapus, total terupdate |

#### TC-MANUAL-03: Checkout Process
| Aspect | Detail |
|--------|--------|
| **Test ID** | TC-MANUAL-03 |
| **Feature** | Kasir - Checkout & Pembayaran |
| **Precondition** | Keranjang berisi items |
| **Steps** | 1. Review keranjang<br/>2. Pilih metode pembayaran<br/>3. Input nominal<br/>4. Klik "Checkout" |
| **Expected Result** | Transaksi berhasil, struk tampil |
