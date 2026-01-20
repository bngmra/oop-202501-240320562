package com.upb.agripos.service;

import com.upb.agripos.model.Cart;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;
import com.upb.agripos.exception.ValidationException;
import java.util.List;

public class CartService {
    private Cart cart;

    public CartService() {
        this.cart = new Cart();
    }

    public void addToCart(Product product, int quantity) throws Exception {
        if (product == null) {
            throw new ValidationException("Produk tidak boleh kosong");
        }
        if (quantity <= 0) {
            throw new ValidationException("Quantity harus lebih dari 0");
        }
        cart.addItem(product, quantity);
    }

    public void removeFromCart(String productCode) throws Exception {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new ValidationException("Kode produk tidak boleh kosong");
        }
        cart.removeItem(productCode);
    }

    public void updateCartItemQuantity(String productCode, int quantity) throws Exception {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new ValidationException("Kode produk tidak boleh kosong");
        }
        cart.updateQuantity(productCode, quantity);
    }

    public List<CartItem> getCartItems() {
        return cart.getItems();
    }

    public double getCartTotal() {
        return cart.getTotal();
    }

    public void clearCart() {
        cart.clear();
    }

    public boolean isCartEmpty() {
        return cart.isEmpty();
    }

    public int getCartItemCount() {
        return cart.getItemCount();
    }
}
