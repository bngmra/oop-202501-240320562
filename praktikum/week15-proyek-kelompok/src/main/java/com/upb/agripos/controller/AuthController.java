package com.upb.agripos.controller;

import com.upb.agripos.model.User;
import com.upb.agripos.service.AuthService;
import com.upb.agripos.view.LoginView;
import javafx.scene.control.Alert;

public class AuthController {
    private AuthService authService;
    private LoginView view;
    private Runnable onLoginSuccessHandler;

    public AuthController(AuthService authService, LoginView view) {
        this.authService = authService;
        this.view = view;
        initializeUI();
    }

    private void initializeUI() {
        view.enableEnterKeyLogin();
        view.onLoginClicked(() -> handleLogin());
    }

    private void handleLogin() {
        try {
            String username = view.getUsername();
            String password = view.getPassword();

            User user = authService.login(username, password);
            
            view.setStatusMessage("✓ Login berhasil! Selamat datang " + user.getName(), false);
            
            // Trigger success callback setelah 1 detik
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    if (onLoginSuccessHandler != null) {
                        javafx.application.Platform.runLater(() -> onLoginSuccessHandler.run());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            view.setStatusMessage("✗ " + e.getMessage(), true);
            view.clearFields();
        }
    }

    public void onLoginSuccess(Runnable handler) {
        this.onLoginSuccessHandler = handler;
    }

    public User getCurrentUser() {
        String username = view.getUsername();
        try {
            return authService.login(username, view.getPassword());
        } catch (Exception e) {
            return null;
        }
    }
}
