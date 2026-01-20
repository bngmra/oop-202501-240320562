package com.upb.agripos.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class LoginView extends VBox {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label statusLabel;
    private Runnable loginHandler;

    public LoginView() {
        initializeUI();
    }

    private void initializeUI() {
        this.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 12;");
        this.setPadding(new Insets(40));
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #f0f0f0;");

        // Title
        Label titleLabel = new Label("AGRI-POS");
        titleLabel.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitleLabel = new Label("Sistem Penjualan Pertanian");
        subtitleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #7f8c8d;");

        // Login Form Container
        VBox formBox = new VBox(12);
        formBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-color: white; -fx-padding: 30;");
        formBox.setMaxWidth(400);

        // Username
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-weight: bold;");
        usernameField = new TextField();
        usernameField.setPromptText("Masukkan username");
        usernameField.setPrefHeight(35);

        // Password
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan password");
        passwordField.setPrefHeight(35);

        // Login Button
        loginButton = new Button("Login");
        loginButton.setPrefHeight(40);
        loginButton.setPrefWidth(150);
        loginButton.setStyle("-fx-font-size: 13; -fx-padding: 10; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");

        // Status Label
        statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");

        formBox.getChildren().addAll(
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            loginButton,
            statusLabel
        );

        // Info Label
        Label infoLabel = new Label("Demo Credentials:\nKasir: kasir001 / kasir123\nAdmin: admin001 / admin123");
        infoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11;");

        this.getChildren().addAll(titleLabel, subtitleLabel, formBox, infoLabel);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }

    public void setStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        if (isError) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #27ae60;");
        }
    }

    public void onLoginClicked(Runnable handler) {
        this.loginHandler = handler;
        loginButton.setOnAction(e -> handler.run());
    }

    // Allow Enter key to trigger login
    public void enableEnterKeyLogin() {
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                loginButton.fire();
            }
        });
    }
}
