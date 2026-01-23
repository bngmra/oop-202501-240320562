package com.upb.agripos;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.upb.agripos.exception.ValidationException;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;
import com.upb.agripos.service.CartService;

public class CartServiceTest {
    private CartService cartService;
    private Product product1;
    private Product product2;

    @BeforeEach
    public void setUp() {
        cartService = new CartService();
        product1 = new Product("P001", "Benih Padi", "Benih", 50000, 100);
        product2 = new Product("P002", "Pupuk NPK", "Pupuk", 75000, 50);
    }

    @Test
    public void testAddToCart() throws Exception {
        cartService.addToCart(product1, 2);
        assertEquals(1, cartService.getCartItemCount());
        assertEquals(100000.0, cartService.getCartTotal(), 0.001);
    }

    @Test
    public void testAddMultipleItems() throws Exception {
        cartService.addToCart(product1, 1);
        cartService.addToCart(product2, 2);
        assertEquals(2, cartService.getCartItemCount());
        assertEquals(200000.0, cartService.getCartTotal(), 0.001);
    }

    @Test
    public void testAddToCartInvalidQuantity() {
        assertThrows(ValidationException.class, () -> cartService.addToCart(product1, 0));
        assertThrows(ValidationException.class, () -> cartService.addToCart(product1, -1));
    }

    @Test
    public void testAddToCartNullProduct() {
        assertThrows(ValidationException.class, () -> cartService.addToCart(null, 1));
    }

    @Test
    public void testRemoveFromCart() throws Exception {
        cartService.addToCart(product1, 2);
        cartService.removeFromCart("P001");
        assertTrue(cartService.isCartEmpty());
    }

    @Test
    public void testUpdateCartQuantity() throws Exception {
        cartService.addToCart(product1, 1);
        cartService.updateCartItemQuantity("P001", 3);
        assertEquals(150000.0, cartService.getCartTotal(), 0.001);
    }

    @Test
    public void testClearCart() throws Exception {
        cartService.addToCart(product1, 2);
        cartService.addToCart(product2, 1);
        cartService.clearCart();
        assertTrue(cartService.isCartEmpty());
    }

    @Test
    public void testIsCartEmpty() {
        assertTrue(cartService.isCartEmpty());
    }

    @Test
    public void testGetCartTotal() throws Exception {
        cartService.addToCart(product1, 2);
        cartService.addToCart(product2, 1);
        assertEquals(175000.0, cartService.getCartTotal(), 0.001);
    }

    // Additional tests

    @Test
    public void testAddSameProductIncreasesQuantity() throws Exception {
        cartService.addToCart(product1, 1);
        cartService.addToCart(product1, 2);
        // still one cart item, total = 3 * 50000
        assertEquals(1, cartService.getCartItemCount());
        assertEquals(150000.0, cartService.getCartTotal(), 0.001);
    }

    @Test
    public void testRemoveFromCartInvalidCodeThrows() {
        assertThrows(ValidationException.class, () -> cartService.removeFromCart(null));
        assertThrows(ValidationException.class, () -> cartService.removeFromCart("  "));
    }

    @Test
    public void testUpdateCartQuantityInvalidCodeThrows() {
        assertThrows(ValidationException.class, () -> cartService.updateCartItemQuantity(null, 1));
        assertThrows(ValidationException.class, () -> cartService.updateCartItemQuantity("", 1));
    }

    @Test
    public void testGetCartItemsContents() throws Exception {
        cartService.addToCart(product1, 2);
        cartService.addToCart(product2, 1);
        List<CartItem> items = cartService.getCartItems();
        assertEquals(2, items.size());
        // verify product codes present
        boolean hasP001 = items.stream().anyMatch(i -> i.getProduct().getCode().equals("P001"));
        boolean hasP002 = items.stream().anyMatch(i -> i.getProduct().getCode().equals("P002"));
        assertTrue(hasP001 && hasP002);
    }
}