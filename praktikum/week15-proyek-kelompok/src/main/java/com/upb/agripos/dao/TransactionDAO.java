package com.upb.agripos.dao;

import com.upb.agripos.model.Transaction;
import java.util.List;

public interface TransactionDAO {
    void create(Transaction transaction) throws Exception;
    Transaction findById(String id) throws Exception;
    List<Transaction> findAll() throws Exception;
    List<Transaction> findByDateRange(String startDate, String endDate) throws Exception;
}
