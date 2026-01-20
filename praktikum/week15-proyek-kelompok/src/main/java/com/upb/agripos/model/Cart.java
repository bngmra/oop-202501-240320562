package com.upb.agripos.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class Cart {
    private Map<String, CartItem> items;

    public Cart() {
        this.items = new HashMap<>();
    }

    public void addItem(Product product, int quantity) throws Exception {
        if (quantity <= 0) {
            throw new Exception("Quantity harus lebih dari 0");
        }
        if (product.getStock() < quantity) {
            throw new Exception("Stok tidak cukup. Stok tersedia: " + product.getStock());
        }
        
        String key = product.getCode();
        if (items.containsKey(key)) {
            CartItem existing = items.get(key);
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            items.put(key, new CartItem(product, quantity));
        }
    }

    public void removeItem(String productCode) {
        items.remove(productCode);
    }

    public void updateQuantity(String productCode, int quantity) throws Exception {
        if (!items.containsKey(productCode)) {
            throw new Exception("Produk tidak ada di keranjang");
        }
        if (quantity <= 0) {
            removeItem(productCode);
        } else {
            CartItem item = items.get(productCode);
            if (item.getProduct().getStock() < quantity) {
                throw new Exception("Stok tidak cukup");
            }
            item.setQuantity(quantity);
        }
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public double getTotal() {
        return items.values().stream()
            .mapToDouble(CartItem::getSubtotal)
            .sum();
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getItemCount() {
        return items.size();
    }
}
