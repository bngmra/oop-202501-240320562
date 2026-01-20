package com.upb.agripos.service;

import com.upb.agripos.dao.UserDAO;
import com.upb.agripos.model.User;
import com.upb.agripos.exception.ValidationException;

public class AuthService {
    private UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username tidak boleh kosong");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password tidak boleh kosong");
        }

        User user = userDAO.authenticate(username, password);
        if (user == null) {
            throw new ValidationException("Username atau password salah");
        }

        return user;
    }

    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }

    public boolean isCashier(User user) {
        return user != null && "CASHIER".equals(user.getRole());
    }
}
