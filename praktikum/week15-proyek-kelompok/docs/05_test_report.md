# 05 - Test Report

## Agri-POS Unit Test Execution Report

**Project:** Agri-POS (Sistem Kasir Pertanian)  
**Module Tested:** CartService  
**Test Class:** CartServiceTest.java  
**Framework:** JUnit 5  
**Test Date:** 21 Januari 2026  
**Tester:** QA Team  

---

## Executive Summary
![Screenshot hasil](/praktikum/week15-proyek-kelompok/screenshots/Junit.png)

| Metric | Value |
|--------|-------|
| **Total Test Cases** | 13 Unit Tests |
| **Passed** | 13 ✅ |
| **Failed** | 0 ❌ |
| **Skipped** | 0 ⏭️ |
| **Success Rate** | 100% |
| **Coverage** | CartService core functionality |

---

## Test Results Detail

### ✅ TC-UNIT-01: Add Single Product to Cart
- **Status:** PASS
- **Method:** testAddToCart()
- **Result:** addToCart(P001, qty:2) → item count=1, total=Rp100.000 ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-02: Add Multiple Different Products
- **Status:** PASS
- **Method:** testAddMultipleItems()
- **Result:** addToCart(P001, qty:1) + addToCart(P002, qty:2) → count=2, total=Rp200.000 ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-03: Validate Invalid Quantity
- **Status:** PASS
- **Method:** testAddToCartInvalidQuantity()
- **Result:** ValidationException thrown untuk qty 0 dan qty -1 ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-04: Validate Null Product
- **Status:** PASS
- **Method:** testAddToCartNullProduct()
- **Result:** ValidationException thrown saat addToCart(null, 1) ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-05: Remove Item from Cart
- **Status:** PASS
- **Method:** testRemoveFromCart()
- **Result:** removeFromCart("P001") → cart empty ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-06: Update Cart Item Quantity
- **Status:** PASS
- **Method:** testUpdateCartQuantity()
- **Result:** updateCartItemQuantity("P001", qty:3) → total=Rp150.000 ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-07: Clear All Cart Items
- **Status:** PASS
- **Method:** testClearCart()
- **Result:** clearCart() → cart empty ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-08: Check Initial Cart Empty State
- **Status:** PASS
- **Method:** testIsCartEmpty()
- **Result:** New CartService instance → isCartEmpty = true ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-09: Calculate Correct Total Price
- **Status:** PASS
- **Method:** testGetCartTotal()
- **Result:** 2 products → total=Rp175.000 (100k+75k) ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-10: Add Same Product Increases Quantity
- **Status:** PASS
- **Method:** testAddSameProductIncreasesQuantity()
- **Result:** addToCart(P001, qty:1) + addToCart(P001, qty:2) → count=1, total=Rp150.000 ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-11: Validate Remove with Invalid Code
- **Status:** PASS
- **Method:** testRemoveFromCartInvalidCodeThrows()
- **Result:** ValidationException thrown untuk code null dan whitespace ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-12: Validate Update with Invalid Code
- **Status:** PASS
- **Method:** testUpdateCartQuantityInvalidCodeThrows()
- **Result:** ValidationException thrown untuk code null dan empty ✓
- **Duration:** < 10ms

### ✅ TC-UNIT-13: Get Cart Items Contents
- **Status:** PASS
- **Method:** testGetCartItemsContents()
- **Result:** getCartItems() → list berisi 2 items dengan code P001, P002 ✓
- **Duration:** < 10ms

---

## Test Coverage Analysis

### Methods Tested
| Method | Test Cases | Coverage |
|--------|-----------|----------|
| addToCart() | TC-UNIT-01, 02, 03, 04, 10 | 5 tests |
| removeFromCart() | TC-UNIT-05, 11 | 2 tests |
| updateCartItemQuantity() | TC-UNIT-06, 12 | 2 tests |
| clearCart() | TC-UNIT-07 | 1 test |
| isCartEmpty() | TC-UNIT-08 | 1 test |
| getCartTotal() | TC-UNIT-09 | 1 test |
| getCartItems() | TC-UNIT-13 | 1 test |

### Test Case Categories
| Category | Count | Status |
|----------|-------|--------|
| Happy Path (Valid Input) | 7 tests | ✅ PASS |
| Validation / Exception | 6 tests | ✅ PASS |
| **TOTAL** | **13 tests** | **✅ 100% PASS** |

---

## Issues & Observations

### No Issues Found
✅ Semua test case berhasil dieksekusi tanpa kegagalan  
✅ ValidationException handling berfungsi dengan baik  
✅ Calculation logic (total price) akurat  
✅ Cart state management konsisten  

### Recommendations
1. **Performance:** Test execution time excellent (<10ms per test)
2. **Code Quality:** CartService implementation solid dan reliable
3. **Next Steps:** 
   - Proceed dengan integration testing (CartService ↔ Database)
   - Lanjut dengan GUI testing for checkout flow
   - Implement Payment integration tests

---

## Conclusion

**CartService Unit Testing: PASSED ✅**

Modul CartService telah lulus semua unit test dengan tingkat keberhasilan 100%. Semua fungsi core (add, remove, update, clear) berfungsi dengan baik. Validasi input dan exception handling sudah proper. Module ini dinyatakan **READY FOR INTEGRATION TESTING**.

---

