package com.upb.agripos.service;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.model.Product;
import com.upb.agripos.exception.ValidationException;
import java.util.List;

public class ProductService {
    private ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Product getProductByCode(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException("Kode produk tidak boleh kosong");
        }
        return productDAO.findByCode(code);
    }

    public List<Product> getAllProducts() throws Exception {
        return productDAO.findAll();
    }

    public void addProduct(String code, String name, String category, double price, int stock) 
            throws Exception {
        validateProductInput(code, name, category, price, stock);
        
        if (productDAO.findByCode(code) != null) {
            throw new ValidationException("Kode produk sudah terdaftar");
        }
        
        Product product = new Product(code, name, category, price, stock);
        productDAO.create(product);
    }

    public void updateProduct(String code, String name, String category, double price, int stock) 
            throws Exception {
        validateProductInput(code, name, category, price, stock);
        
        Product product = productDAO.findByCode(code);
        if (product == null) {
            throw new ValidationException("Produk dengan kode " + code + " tidak ditemukan");
        }
        
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);
        productDAO.update(product);
    }

    public void deleteProduct(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException("Kode produk tidak boleh kosong");
        }
        
        Product product = productDAO.findByCode(code);
        if (product == null) {
            throw new ValidationException("Produk dengan kode " + code + " tidak ditemukan");
        }
        
        productDAO.delete(code);
    }

    private void validateProductInput(String code, String name, String category, double price, int stock) 
            throws ValidationException {
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException("Kode produk tidak boleh kosong");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nama produk tidak boleh kosong");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Kategori produk tidak boleh kosong");
        }
        if (price <= 0) {
            throw new ValidationException("Harga harus lebih dari 0");
        }
        if (stock < 0) {
            throw new ValidationException("Stok tidak boleh negatif");
        }
    }
}
