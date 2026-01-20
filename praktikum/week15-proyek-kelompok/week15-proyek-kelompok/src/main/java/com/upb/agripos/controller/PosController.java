package com.upb.agripos.controller;

import com.upb.agripos.dao.JdbcTransactionDAO;
import com.upb.agripos.model.Product;
import com.upb.agripos.model.Transaction;
import com.upb.agripos.model.User;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.service.PaymentService;
import com.upb.agripos.service.ReceiptService;
import com.upb.agripos.service.ReportService;
import com.upb.agripos.service.AuthService;
import com.upb.agripos.payment.PaymentMethod;
import com.upb.agripos.view.PosView;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.sql.Connection;
import java.util.*;

public class PosController {
    private ProductService productService;
    private CartService cartService;
    private PaymentService paymentService;
    private ReceiptService receiptService;
    private ReportService reportService;
    private AuthService authService;
    private JdbcTransactionDAO transactionDAO;
    private PosView view;
    private User currentUser;
    private Runnable onLogoutHandler;

    public PosController(ProductService productService, CartService cartService, 
                         AuthService authService, User user, PosView view, Connection connection) {
        this.productService = productService;
        this.cartService = cartService;
        this.paymentService = new PaymentService();
        this.receiptService = new ReceiptService();
        this.reportService = new ReportService(connection);
        this.authService = authService;
        this.transactionDAO = new JdbcTransactionDAO(connection);
        this.currentUser = user;
        this.view = view;
        initializeUI();
    }

    private void initializeUI() {
        // Display user info
        view.setUserInfo(currentUser.getUsername(), currentUser.getRole());

        // Role-based UI setup
        if (authService.isCashier(currentUser)) {
            // KASIR: Show only Transaksi and Laporan tabs
            view.setupForKasirRole();
        } else if (authService.isAdmin(currentUser)) {
            // ADMIN: Show only Produk tab
            view.setupForAdminRole();
        }

        // Load initial data
        loadProducts();
        
        // Setup event handlers
        view.onAddToCartClicked(() -> handleAddToCart());
        view.onCheckoutClicked(() -> handleCheckout());
        view.onClearCartClicked(() -> handleClearCart());
        view.onRemoveFromCartClicked(() -> handleRemoveFromCart());
        view.onUpdateQtyClicked((productCode, qty) -> handleUpdateQty(productCode, qty));
        view.onLogoutClicked(() -> handleLogout());
        view.onViewReportClicked(() -> handleViewReport());
        
        // Setup product management handlers for admin
        view.setupProductManagementHandlers(
            () -> handleAddProduct(),
            () -> handleEditProduct(),
            () -> handleDeleteProduct(),
            () -> handleClearProductForm()
        );
        
        // Setup product table selection for admin
        if (authService.isAdmin(currentUser)) {
            view.onProductTableSelectionChanged(() -> view.setProductFormFromSelected());
        }
    }

    private void loadProducts() {
        try {
            var products = productService.getAllProducts();
            view.displayProducts(products);
        } catch (Exception e) {
            showError("Error loading products: " + e.getMessage());
        }
    }

    private void handleAddProduct() {
        try {
            String code = view.getProductCode();
            String name = view.getProductName();
            String category = view.getProductCategory();
            double price = view.getProductPrice();
            int stock = view.getProductStock();

            if (code.isEmpty() || name.isEmpty() || category.isEmpty()) {
                showWarning("Kode, Nama, dan Kategori harus diisi");
                return;
            }

            if (price <= 0 || stock < 0) {
                showWarning("Harga harus > 0 dan Stok >= 0");
                return;
            }

            productService.addProduct(code, name, category, price, stock);
            showInfo("Produk berhasil ditambahkan");
            view.clearProductForm();
            loadProducts();
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void handleEditProduct() {
        try {
            Product selected = view.getSelectedProductForAdmin();
            if (selected == null) {
                showWarning("Pilih produk yang akan diedit");
                return;
            }

            String code = selected.getCode(); // Gunakan kode asli
            String name = view.getProductName();
            String category = view.getProductCategory();
            double price = view.getProductPrice();
            int stock = view.getProductStock();

            if (name.isEmpty() || category.isEmpty()) {
                showWarning("Nama dan Kategori harus diisi");
                return;
            }

            if (price <= 0 || stock < 0) {
                showWarning("Harga harus > 0 dan Stok >= 0");
                return;
            }

            productService.updateProduct(code, name, category, price, stock);
            showInfo("Produk berhasil diupdate");
            view.clearProductForm();
            loadProducts();
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void handleDeleteProduct() {
        try {
            Product selected = view.getSelectedProductForAdmin();
            if (selected == null) {
                showWarning("Pilih produk yang akan dihapus");
                return;
            }

            productService.deleteProduct(selected.getCode());
            showInfo("Produk berhasil dihapus");
            view.clearProductForm();
            loadProducts();
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void handleClearProductForm() {
        view.clearProductForm();
    }

    private void handleAddToCart() {
        try {
            Product selected = view.getSelectedProductForSelection();
            if (selected == null) {
                showWarning("Pilih produk terlebih dahulu");
                return;
            }
            
            int quantity = view.getInputQuantity();
            if (quantity <= 0) {
                showWarning("Quantity harus lebih dari 0");
                return;
            }
            
            cartService.addToCart(selected, quantity);
            view.displayCartItems(cartService.getCartItems());
            view.displayCartTotal(cartService.getCartTotal());
            view.clearInputQuantity();
            showInfo("Produk ditambahkan ke keranjang");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void handleCheckout() {
        try {
            if (cartService.isCartEmpty()) {
                showWarning("Keranjang kosong");
                return;
            }
            
            double total = cartService.getCartTotal();
            PaymentMethod method = view.getSelectedPaymentMethod();
            double amountPaid = view.getAmountPaid();
            
            paymentService.processCheckout(cartService.getCartItems(), total, method, amountPaid);
            double change = paymentService.calculateChange(total, amountPaid);
            
            // Generate transaction ID and receipt
            String transactionId = generateTransactionId();
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.now();
            
            String receipt = receiptService.generateReceipt(
                transactionId,
                dateTime,
                cartService.getCartItems(),
                total,
                amountPaid,
                change,
                method.getMethodName()
            );
            
            // Save transaction to database
            try {
                Transaction transaction = new Transaction(transactionId, dateTime);
                transaction.setTotal(total);
                transaction.setAmountPaid(amountPaid);
                transaction.setChange(change);
                transaction.setPaymentMethod(method.getMethodName());
                transaction.setStatus("COMPLETED");
                
                // Add cart items to transaction
                for (CartItem item : cartService.getCartItems()) {
                    transaction.addItem(item);
                }
                
                transactionDAO.create(transaction);
            } catch (Exception e) {
                System.err.println("Warning: Failed to save transaction to database: " + e.getMessage());
                // Continue with receipt display even if DB save fails
            }
            
            // Show receipt in popup dialog
            showReceiptPopup(receipt);
            
            // Clear old receipt display in main view
            view.displayReceipt("");
            
            cartService.clearCart();
            view.displayCartItems(cartService.getCartItems());
            view.displayCartTotal(0);
            view.clearInputFields();
            showInfo("Checkout berhasil! Kembalian: Rp" + change);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void handleClearCart() {
        cartService.clearCart();
        view.displayCartItems(cartService.getCartItems());
        view.displayCartTotal(0);
        showInfo("Keranjang dikosongkan");
    }

    private void handleRemoveFromCart() {
        try {
            CartItem selectedItem = view.getSelectedCartItem();
            if (selectedItem == null) {
                showWarning("Pilih item yang akan dihapus dari keranjang");
                return;
            }
            
            String productCode = selectedItem.getProduct().getCode();
            cartService.removeFromCart(productCode);
            view.displayCartItems(cartService.getCartItems());
            view.displayCartTotal(cartService.getCartTotal());
            showInfo("Item berhasil dihapus dari keranjang");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private String generateTransactionId() {
        return "TRX-" + System.currentTimeMillis();
    }

    private void handleUpdateQty(String productCode, int newQty) {
        try {
            if (newQty <= 0) {
                showWarning("Jumlah item harus lebih dari 0");
                view.displayCartItems(cartService.getCartItems());
                return;
            }
            cartService.updateCartItemQuantity(productCode, newQty);
            view.displayCartItems(cartService.getCartItems());
            view.displayCartTotal(cartService.getCartTotal());
        } catch (Exception e) {
            showError("Error updating quantity: " + e.getMessage());
        }
    }

    private void handleViewReport() {
        try {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.util.List<java.util.Map<String, Object>> tableData = reportService.getDailyReportTableData(today);
            JdbcTransactionDAO.TransactionSummary summary = reportService.getTodayTransactionSummary();
            int totalItems = reportService.getTotalItemsForDate(today);
            
            view.displayReportTable(today, tableData, summary.transactionCount, totalItems, 
                                    summary.totalRevenue, summary.cashCount, summary.ewalletCount);
        } catch (Exception e) {
            showError("Error generating report: " + e.getMessage());
        }
    }

    private void handleLogout() {
        if (onLogoutHandler != null) {
            onLogoutHandler.run();
        }
    }

    public void onLogout(Runnable handler) {
        this.onLogoutHandler = handler;
    }

    public User getCurrentUser() {
        return currentUser;
    }
    
    private void showReceiptPopup(String receipt) {
        // Create a new stage (popup window)
        Stage receiptStage = new Stage();
        receiptStage.setTitle("Struk Pembayaran");
        receiptStage.initModality(Modality.APPLICATION_MODAL);
        
        // Create TextArea for receipt
        TextArea receiptArea = new TextArea(receipt);
        receiptArea.setEditable(false);
        receiptArea.setWrapText(true);
        receiptArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        
        // Wrap in ScrollPane for safety
        ScrollPane scrollPane = new ScrollPane(receiptArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        VBox vbox = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        Scene scene = new Scene(vbox, 500, 600);
        receiptStage.setScene(scene);
        
        // Show and wait (modal)
        receiptStage.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
