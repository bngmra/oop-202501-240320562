package com.upb.agripos.dao;

import com.upb.agripos.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductDAO implements ProductDAO {
    private Connection connection;

    public JdbcProductDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Product findByCode(String code) throws Exception {
        String sql = "SELECT * FROM products WHERE code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        }
        return null;
    }

    @Override
    public List<Product> findAll() throws Exception {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY code";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }

    @Override
    public void create(Product product) throws Exception {
        String sql = "INSERT INTO products (code, name, category, price, stock) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getCode());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getCategory());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getStock());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Product product) throws Exception {
        String sql = "UPDATE products SET name = ?, category = ?, price = ?, stock = ? WHERE code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStock());
            stmt.setString(5, product.getCode());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(String code) throws Exception {
        String sql = "DELETE FROM products WHERE code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.executeUpdate();
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws Exception {
        String code = rs.getString("code");
        String name = rs.getString("name");
        String category = rs.getString("category");
        double price = rs.getDouble("price");
        int stock = rs.getInt("stock");
        return new Product(code, name, category, price, stock);
    }
}
