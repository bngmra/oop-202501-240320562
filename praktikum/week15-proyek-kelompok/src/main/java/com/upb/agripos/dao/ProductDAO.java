package com.upb.agripos.dao;

import com.upb.agripos.model.Product;
import java.util.List;

public interface ProductDAO {
    Product findByCode(String code) throws Exception;
    List<Product> findAll() throws Exception;
    void create(Product product) throws Exception;
    void update(Product product) throws Exception;
    void delete(String code) throws Exception;
}
