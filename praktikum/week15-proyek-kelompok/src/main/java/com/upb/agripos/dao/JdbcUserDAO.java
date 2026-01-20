package com.upb.agripos.dao;

import com.upb.agripos.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JdbcUserDAO implements UserDAO {
    private Connection connection;

    public JdbcUserDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User findByUsername(String username) throws Exception {
        String sql = "SELECT id, username, password, role, name FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    @Override
    public User authenticate(String username, String password) throws Exception {
        String sql = "SELECT id, username, password, role, name FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    private User mapResultSetToUser(ResultSet rs) throws Exception {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String role = rs.getString("role");
        String name = rs.getString("name");
        return new User(id, username, password, role, name);
    }
}
