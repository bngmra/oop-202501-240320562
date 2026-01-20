package com.upb.agripos.view;

import com.upb.agripos.model.Product;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.payment.CashPayment;
import com.upb.agripos.payment.EWalletPayment;
import com.upb.agripos.payment.PaymentMethod;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import java.util.List;

public class PosView extends VBox {
    private Label userInfoLabel;
    private Button logoutBtn;
    private TableView<Product> productTableAdmin;
    private TableView<Product> productTableSelect;
    private TableView<CartItem> cartTable;
    private Label totalLabel;
    private Label cartCountLabel;
    private TextArea receiptArea;
    private TextArea reportArea;
    private SpinnerValueFactory.IntegerSpinnerValueFactory quantityFactory;
    private TextField amountPaidField;
    private ComboBox<String> paymentMethodCombo;
    private Button addToCartBtn;
    private Button checkoutBtn;
    private Button clearCartBtn;
    private Button viewReportBtn;
    private VBox productPanel;
    private VBox cartPanel;
    private VBox reportPanel;
    private TabPane tabPane;
    private Tab productsTab;
    private Tab checkoutTab;
    private Tab reportTab;
    private Runnable addToCartHandler;
    private Runnable checkoutHandler;
    private Runnable clearCartHandler;
    private Runnable logoutHandler;

    public PosView() {
        initializeUI();
    }

    private void initializeUI() {
        this.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 11;");
        this.setPadding(new Insets(10));
        this.setSpacing(10);

        // Top Bar: Title + User Info
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(5, 10, 5, 10));
        topBar.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #34495e;");

        Label titleLabel = new Label("AGRI-POS - Sistem Penjualan Pertanian");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox.setHgrow(titleLabel, javafx.scene.layout.Priority.ALWAYS);

        userInfoLabel = new Label("User: -");
        userInfoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");

        logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-padding: 5 15; -fx-background-color: #e74c3c; -fx-text-fill: white;");

        topBar.getChildren().addAll(titleLabel, userInfoLabel, logoutBtn);
        this.getChildren().add(topBar);

        // Main content area with TabPane for kasir views
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setPrefHeight(450);

        // Tab 1: Products (for admin)
        productsTab = new Tab("Produk", createProductPanel());
        productsTab.setClosable(false);
        productPanel = (VBox) productsTab.getContent();
        tabPane.getTabs().add(productsTab);

        // Tab 2: Cart & Checkout (for kasir)
        checkoutTab = new Tab("Transaksi", createCheckoutTab());
        checkoutTab.setClosable(false);
        cartPanel = (VBox) checkoutTab.getContent();
        tabPane.getTabs().add(checkoutTab);

        // Tab 3: Laporan (for kasir)
        reportTab = new Tab("Laporan Harian", createReportPanel());
        reportTab.setClosable(false);
        reportPanel = (VBox) reportTab.getContent();
        tabPane.getTabs().add(reportTab);

        this.getChildren().add(tabPane);

        // Bottom: Receipt display
        receiptArea = new TextArea();
        receiptArea.setPrefHeight(120);
        receiptArea.setEditable(false);
        receiptArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");
        receiptArea.setWrapText(true);
        this.getChildren().add(new Label("Receipt Preview:"));
        this.getChildren().add(receiptArea);
    }

    private VBox createCheckoutTab() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        // Combine products and cart side by side
        HBox mainContent = new HBox(10);
        mainContent.setPrefHeight(350);

        // Products list for selection (kasir)
        VBox productSelectPanel = new VBox(10);
        productSelectPanel.setPrefWidth(400);
        productSelectPanel.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");

        Label prodLabel = new Label("Pilih Produk");
        prodLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        productSelectPanel.getChildren().add(prodLabel);

        productTableSelect = new TableView<>();
        productTableSelect.setPrefHeight(200);

        TableColumn<Product, String> codeCol = new TableColumn<>("Kode");
        codeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getCode()));
        codeCol.setPrefWidth(80);

        TableColumn<Product, String> nameCol = new TableColumn<>("Nama");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getName()));
        nameCol.setPrefWidth(120);

        TableColumn<Product, Double> priceCol = new TableColumn<>("Harga");
        priceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(
            cellData.getValue().getPrice()));
        priceCol.setPrefWidth(80);

        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stok");
        stockCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(
            cellData.getValue().getStock()));
        stockCol.setPrefWidth(60);

        productTableSelect.getColumns().addAll(codeCol, nameCol, priceCol, stockCol);
        productTableSelect.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productSelectPanel.getChildren().add(productTableSelect);

        // Quantity input
        HBox qtyBox = new HBox(10);
        qtyBox.setPadding(new Insets(5));
        Label qtyLabel = new Label("Qty:");
        Spinner<Integer> qtySpinner = new Spinner<>(1, 1000, 1);
        quantityFactory = (SpinnerValueFactory.IntegerSpinnerValueFactory) qtySpinner.getValueFactory();
        addToCartBtn = new Button("Tambah ke Keranjang");
        addToCartBtn.setStyle("-fx-font-size: 11; -fx-padding: 8;");
        addToCartBtn.setPrefWidth(150);
        qtyBox.getChildren().addAll(qtyLabel, qtySpinner, addToCartBtn);
        productSelectPanel.getChildren().add(qtyBox);

        mainContent.getChildren().add(productSelectPanel);

        // Cart panel
        mainContent.getChildren().add(createCartPanel());

        container.getChildren().add(mainContent);
        return container;
    }

    private VBox createProductPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");

        Label titleLabel = new Label("Manajemen Produk");
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        panel.getChildren().add(titleLabel);

        // Product table (admin)
        productTableAdmin = new TableView<>();
        productTableAdmin.setPrefHeight(250);

        TableColumn<Product, String> codeCol = new TableColumn<>("Kode");
        codeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCode()));
        codeCol.setPrefWidth(80);

        TableColumn<Product, String> nameCol = new TableColumn<>("Nama");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(120);

        TableColumn<Product, String> categoryCol = new TableColumn<>("Kategori");
        categoryCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        categoryCol.setPrefWidth(100);

        TableColumn<Product, Double> priceCol = new TableColumn<>("Harga");
        priceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));
        priceCol.setPrefWidth(80);

        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stok");
        stockCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStock()));
        stockCol.setPrefWidth(60);

        productTableAdmin.getColumns().addAll(codeCol, nameCol, categoryCol, priceCol, stockCol);
        productTableAdmin.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        panel.getChildren().add(productTableAdmin);

        // Input form for CRUD
        VBox formBox = new VBox(8);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 3; -fx-background-color: #f9f9f9;");
        
        Label formLabel = new Label("Form Produk");
        formLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        formBox.getChildren().add(formLabel);

        // Code input
        HBox codeBox = new HBox(5);
        codeBox.setPrefHeight(25);
        Label codeLabel = new Label("Kode:");
        codeLabel.setPrefWidth(70);
        TextField codeInput = new TextField();
        codeInput.setPromptText("ex: BENIH-001");
        codeInput.setPrefWidth(200);
        codeBox.getChildren().addAll(codeLabel, codeInput);
        formBox.getChildren().add(codeBox);

        // Name input
        HBox nameBox = new HBox(5);
        nameBox.setPrefHeight(25);
        Label nameLabel = new Label("Nama:");
        nameLabel.setPrefWidth(70);
        TextField nameInput = new TextField();
        nameInput.setPromptText("Nama produk");
        nameInput.setPrefWidth(200);
        nameBox.getChildren().addAll(nameLabel, nameInput);
        formBox.getChildren().add(nameBox);

        // Category input
        HBox catBox = new HBox(5);
        catBox.setPrefHeight(25);
        Label catLabel = new Label("Kategori:");
        catLabel.setPrefWidth(70);
        TextField catInput = new TextField();
        catInput.setPromptText("Benih / Pupuk / Pestisida");
        catInput.setPrefWidth(200);
        catBox.getChildren().addAll(catLabel, catInput);
        formBox.getChildren().add(catBox);

        // Price input
        HBox priceBox = new HBox(5);
        priceBox.setPrefHeight(25);
        Label priceLabel = new Label("Harga:");
        priceLabel.setPrefWidth(70);
        TextField priceInput = new TextField();
        priceInput.setPromptText("0");
        priceInput.setPrefWidth(200);
        priceBox.getChildren().addAll(priceLabel, priceInput);
        formBox.getChildren().add(priceBox);

        // Stock input
        HBox stockBox = new HBox(5);
        stockBox.setPrefHeight(25);
        Label stockLabel = new Label("Stok:");
        stockLabel.setPrefWidth(70);
        TextField stockInput = new TextField();
        stockInput.setPromptText("0");
        stockInput.setPrefWidth(200);
        stockBox.getChildren().addAll(stockLabel, stockInput);
        formBox.getChildren().add(stockBox);

        panel.getChildren().add(formBox);

        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(5));

        Button addBtn = new Button("➕ Tambah Produk");
        addBtn.setStyle("-fx-font-size: 11; -fx-padding: 8; -fx-background-color: #27ae60; -fx-text-fill: white;");
        addBtn.setPrefWidth(140);

        Button editBtn = new Button("✏️ Edit Produk");
        editBtn.setStyle("-fx-font-size: 11; -fx-padding: 8; -fx-background-color: #3498db; -fx-text-fill: white;");
        editBtn.setPrefWidth(140);

        Button deleteBtn = new Button("🗑️ Hapus Produk");
        deleteBtn.setStyle("-fx-font-size: 11; -fx-padding: 8; -fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setPrefWidth(140);

        Button clearBtn = new Button("🔄 Clear Form");
        clearBtn.setStyle("-fx-font-size: 11; -fx-padding: 8; -fx-background-color: #95a5a6; -fx-text-fill: white;");
        clearBtn.setPrefWidth(140);

        buttonBox.getChildren().addAll(addBtn, editBtn, deleteBtn, clearBtn);
        panel.getChildren().add(buttonBox);

        // Store references for controller access
        panel.setUserData(new java.util.HashMap<String, Object>() {{
            put("codeInput", codeInput);
            put("nameInput", nameInput);
            put("catInput", catInput);
            put("priceInput", priceInput);
            put("stockInput", stockInput);
            put("addBtn", addBtn);
            put("editBtn", editBtn);
            put("deleteBtn", deleteBtn);
            put("clearBtn", clearBtn);
        }});

        return panel;
    }

    private VBox createCartPanel() {
        VBox panel = new VBox(10);
        panel.setPrefWidth(400);
        panel.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");

        Label titleLabel = new Label("Keranjang Belanja");
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        panel.getChildren().add(titleLabel);

        // Cart table
        cartTable = new TableView<>();
        cartTable.setPrefHeight(200);

        TableColumn<CartItem, String> itemCol = new TableColumn<>("Item");
        itemCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getProduct().getName()));
        itemCol.setPrefWidth(120);

        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(
            cellData.getValue().getQuantity()));
        qtyCol.setPrefWidth(50);

        TableColumn<CartItem, Double> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(
            cellData.getValue().getSubtotal()));
        subtotalCol.setPrefWidth(80);

        cartTable.getColumns().addAll(itemCol, qtyCol, subtotalCol);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        panel.getChildren().add(cartTable);

        // Summary section
        HBox summaryBox = new HBox(10);
        cartCountLabel = new Label("Item: 0");
        totalLabel = new Label("Total: Rp 0,00");
        totalLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        summaryBox.getChildren().addAll(cartCountLabel, totalLabel);
        panel.getChildren().add(summaryBox);

        // Payment section
        HBox paymentBox = new HBox(10);
        paymentBox.setPadding(new Insets(5));

        Label methodLabel = new Label("Metode:");
        paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.setItems(FXCollections.observableArrayList("TUNAI", "E-WALLET"));
        paymentMethodCombo.setValue("TUNAI");
        paymentMethodCombo.setPrefWidth(100);

        Label amountLabel = new Label("Dibayar:");
        amountPaidField = new TextField();
        amountPaidField.setPromptText("0");
        amountPaidField.setPrefWidth(80);

        paymentBox.getChildren().addAll(methodLabel, paymentMethodCombo, amountLabel, amountPaidField);
        panel.getChildren().add(paymentBox);

        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(5));

        checkoutBtn = new Button("Checkout");
        checkoutBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        checkoutBtn.setPrefWidth(100);

        clearCartBtn = new Button("Clear Cart");
        clearCartBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        clearCartBtn.setPrefWidth(100);

        buttonBox.getChildren().addAll(checkoutBtn, clearCartBtn);
        panel.getChildren().add(buttonBox);

        return panel;
    }

    private VBox createReportPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");

        Label titleLabel = new Label("Laporan Penjualan Harian");
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        panel.getChildren().add(titleLabel);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(5));
        
        viewReportBtn = new Button("Lihat Laporan Hari Ini");
        viewReportBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        viewReportBtn.setPrefWidth(150);
        
        Button exportBtn = new Button("Export Laporan");
        exportBtn.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        exportBtn.setPrefWidth(150);
        exportBtn.setDisable(true); // TODO: Implement export
        
        buttonBox.getChildren().addAll(viewReportBtn, exportBtn);
        panel.getChildren().add(buttonBox);

        // Report display area
        reportArea = new TextArea();
        reportArea.setPrefHeight(300);
        reportArea.setEditable(false);
        reportArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");
        reportArea.setWrapText(false);
        panel.getChildren().add(reportArea);

        return panel;
    }

    public void displayProducts(List<Product> products) {
        if (productTableAdmin != null) {
            productTableAdmin.setItems(FXCollections.observableArrayList(products));
        }
        if (productTableSelect != null) {
            productTableSelect.setItems(FXCollections.observableArrayList(products));
        }
    }

    public Product getSelectedProductForAdmin() {
        return productTableAdmin == null ? null : productTableAdmin.getSelectionModel().getSelectedItem();
    }

    public Product getSelectedProductForSelection() {
        return productTableSelect == null ? null : productTableSelect.getSelectionModel().getSelectedItem();
    }

    public void setupProductManagementHandlers(
            Runnable onAddProduct,
            Runnable onEditProduct,
            Runnable onDeleteProduct,
            Runnable onClearForm) {
        
        java.util.Map<String, Object> userData = (java.util.Map<String, Object>) productPanel.getUserData();
        if (userData != null) {
            ((Button) userData.get("addBtn")).setOnAction(e -> onAddProduct.run());
            ((Button) userData.get("editBtn")).setOnAction(e -> onEditProduct.run());
            ((Button) userData.get("deleteBtn")).setOnAction(e -> onDeleteProduct.run());
            ((Button) userData.get("clearBtn")).setOnAction(e -> onClearForm.run());
        }
    }

    public String getProductCode() {
        java.util.Map<String, Object> userData = (java.util.Map<String, Object>) productPanel.getUserData();
        if (userData != null) {
            return ((TextField) userData.get("codeInput")).getText().trim();
        }
        return "";
    }

    public String getProductName() {
        java.util.Map<String, Object> userData = (java.util.Map<String, Object>) productPanel.getUserData();
        if (userData != null) {
            return ((TextField) userData.get("nameInput")).getText().trim();
        }
        return "";
    }

    public String getProductCategory() {
        java.util.Map<String, Object> userData = (java.util.Map<String, Object>) productPanel.getUserData();
        if (userData != null) {
            return ((TextField) userData.get("catInput")).getText().trim();
        }
        return "";
    }

    public double getProductPrice() {
        java.util.Map<String, Object> userData = (java.util.Map<String, Object>) productPanel.getUserData();
        if (userData != null) {
            String price = ((TextField) userData.get("priceInput")).getText().trim();
            try {
                return Double.parseDouble(price);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public int getProductStock() {
        java.util.Map<String, Object> userData = (java.util.Map<String, Object>) productPanel.getUserData();
        if (userData != null) {
            String stock = ((TextField) userData.get("stockInput")).getText().trim();
            try {
                return Integer.parseInt(stock);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public void clearProductForm() {
        java.util.Map<String, Object> userData = (java.util.Map<String, Object>) productPanel.getUserData();
        if (userData != null) {
            ((TextField) userData.get("codeInput")).clear();
            ((TextField) userData.get("nameInput")).clear();
            ((TextField) userData.get("catInput")).clear();
            ((TextField) userData.get("priceInput")).clear();
            ((TextField) userData.get("stockInput")).clear();
            if (productTableAdmin != null) {
                productTableAdmin.getSelectionModel().clearSelection();
            }
        }
    }

    public void setProductFormFromSelected() {
        Product selected = getSelectedProductForAdmin();
        if (selected != null) {
            java.util.Map<String, Object> userData = (java.util.Map<String, Object>) productPanel.getUserData();
            if (userData != null) {
                ((TextField) userData.get("codeInput")).setText(selected.getCode());
                ((TextField) userData.get("nameInput")).setText(selected.getName());
                ((TextField) userData.get("catInput")).setText(selected.getCategory());
                ((TextField) userData.get("priceInput")).setText(String.valueOf(selected.getPrice()));
                ((TextField) userData.get("stockInput")).setText(String.valueOf(selected.getStock()));
            }
        }
    }

    public void displayCartItems(List<CartItem> items) {
        cartTable.setItems(FXCollections.observableArrayList(items));
        cartCountLabel.setText("Item: " + items.size());
    }

    public void displayCartTotal(double total) {
        totalLabel.setText(String.format("Total: Rp %.2f", total));
    }

    public void displayReceipt(String receipt) {
        receiptArea.setText(receipt);
    }

    public int getInputQuantity() {
        return quantityFactory.getValue();
    }

    public PaymentMethod getSelectedPaymentMethod() {
        String method = paymentMethodCombo.getValue();
        if ("E-WALLET".equals(method)) {
            return new EWalletPayment(1000000); // Mock balance
        }
        return new CashPayment();
    }

    public double getAmountPaid() throws NumberFormatException {
        String amount = amountPaidField.getText().trim();
        if (amount.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(amount);
    }

    public void clearInputQuantity() {
        quantityFactory.setValue(1);
    }

    public void clearInputFields() {
        amountPaidField.clear();
        quantityFactory.setValue(1);
        paymentMethodCombo.setValue("TUNAI");
        if (productTableSelect != null) {
            productTableSelect.getSelectionModel().clearSelection();
        }
    }

    public void onAddToCartClicked(Runnable handler) {
        this.addToCartHandler = handler;
        addToCartBtn.setOnAction(e -> handler.run());
    }

    public void onCheckoutClicked(Runnable handler) {
        this.checkoutHandler = handler;
        checkoutBtn.setOnAction(e -> handler.run());
    }

    public void onClearCartClicked(Runnable handler) {
        this.clearCartHandler = handler;
        clearCartBtn.setOnAction(e -> handler.run());
    }

    public void onLogoutClicked(Runnable handler) {
        this.logoutHandler = handler;
        logoutBtn.setOnAction(e -> handler.run());
    }

    public void onViewReportClicked(Runnable handler) {
        if (viewReportBtn != null) {
            viewReportBtn.setOnAction(e -> handler.run());
        }
    }

    public void onProductTableSelectionChanged(Runnable handler) {
        productTableAdmin.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                handler.run();
            }
        });
    }

    public void displayReport(String report) {
        if (reportArea != null) {
            reportArea.setText(report);
        }
    }

    public void setUserInfo(String username, String role) {
        userInfoLabel.setText(String.format("User: %s (%s)", username, role));
    }

    public void hideProductManagement() {
        if (productPanel != null) {
            productPanel.setVisible(false);
            productPanel.setManaged(false);
        }
    }

    public void showProductManagement() {
        if (productPanel != null) {
            productPanel.setVisible(true);
            productPanel.setManaged(true);
        }
    }

    public void hideCheckoutPanel() {
        if (checkoutTab != null) {
            tabPane.getTabs().remove(checkoutTab);
        }
    }

    public void showCheckoutPanel() {
        if (checkoutTab != null && !tabPane.getTabs().contains(checkoutTab)) {
            tabPane.getTabs().add(1, checkoutTab);
        }
    }

    public void hideReportPanel() {
        if (reportTab != null) {
            tabPane.getTabs().remove(reportTab);
        }
    }

    public void showReportPanel() {
        if (reportTab != null && !tabPane.getTabs().contains(reportTab)) {
            tabPane.getTabs().add(reportTab);
        }
    }

    public void setupForAdminRole() {
        // Admin: Show only Produk tab
        tabPane.getTabs().clear();
        tabPane.getTabs().add(productsTab);
    }

    public void setupForKasirRole() {
        // Kasir: Show Transaksi and Laporan tabs (hide Produk)
        tabPane.getTabs().clear();
        tabPane.getTabs().add(checkoutTab);
        tabPane.getTabs().add(reportTab);
        if (checkoutTab.getTabPane() == null) {
            tabPane.getTabs().add(1, checkoutTab);
        }
    }
}
