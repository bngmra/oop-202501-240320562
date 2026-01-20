package com.upb.agripos.dao;

import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcTransactionDAO implements TransactionDAO {
    private Connection connection;

    public JdbcTransactionDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Transaction transaction) throws Exception {
        String insertTxnSQL = "INSERT INTO transactions (transaction_id, cashier_id, total, amount_paid, change, payment_method, status, created_at) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertItemSQL = "INSERT INTO transaction_items (transaction_id, product_code, product_name, quantity, unit_price, subtotal) " +
                               "VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            connection.setAutoCommit(false);
            
            // Insert transaction header
            int transactionDbId;
            try (PreparedStatement pstmt = connection.prepareStatement(insertTxnSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, transaction.getId());
                pstmt.setInt(2, 1); // Default cashier_id = 1 (kasir001)
                pstmt.setDouble(3, transaction.getTotal());
                pstmt.setDouble(4, transaction.getAmountPaid());
                pstmt.setDouble(5, transaction.getChange());
                pstmt.setString(6, transaction.getPaymentMethod());
                pstmt.setString(7, transaction.getStatus());
                pstmt.setTimestamp(8, Timestamp.valueOf(transaction.getDateTime()));
                
                pstmt.executeUpdate();
                
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    transactionDbId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to get generated transaction ID");
                }
            }
            
            // Insert transaction items
            try (PreparedStatement pstmt = connection.prepareStatement(insertItemSQL)) {
                for (CartItem item : transaction.getItems()) {
                    pstmt.setInt(1, transactionDbId);
                    pstmt.setString(2, item.getProduct().getCode());
                    pstmt.setString(3, item.getProduct().getName());
                    pstmt.setInt(4, item.getQuantity());
                    pstmt.setDouble(5, item.getProduct().getPrice());
                    pstmt.setDouble(6, item.getSubtotal());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw new Exception("Failed to save transaction: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public Transaction findById(String id) throws Exception {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
            return null;
        }
    }

    @Override
    public List<Transaction> findAll() throws Exception {
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        
        return transactions;
    }

    @Override
    public List<Transaction> findByDateRange(String startDate, String endDate) throws Exception {
        String sql = "SELECT * FROM transactions WHERE DATE(created_at) BETWEEN ?::date AND ?::date ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        
        return transactions;
    }
    
    /**
     * Get detailed transaction report for a specific date
     */
    public List<TransactionDetail> getTransactionDetails(String date) throws Exception {
        String sql = "SELECT t.id, ti.product_name, ti.quantity, ti.unit_price, ti.subtotal, t.payment_method " +
                     "FROM transaction_items ti " +
                     "JOIN transactions t ON ti.transaction_id = t.id " +
                     "WHERE DATE(t.created_at) = ?::date " +
                     "ORDER BY ti.id";
        
        List<TransactionDetail> details = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TransactionDetail detail = new TransactionDetail(
                    rs.getString("id"),
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("subtotal"),
                    rs.getString("payment_method")
                );
                details.add(detail);
            }
        }
        
        return details;
    }
    
    /**
     * Get transaction summary for a specific date
     */
    public TransactionSummary getTransactionSummary(String date) throws Exception {
        String sql = "SELECT " +
                     "COUNT(*) as txn_count, " +
                     "SUM(total) as total_revenue, " +
                     "SUM(CASE WHEN UPPER(payment_method) = 'CASH' THEN 1 ELSE 0 END) as cash_count, " +
                     "SUM(CASE WHEN UPPER(payment_method) = 'E-WALLET' THEN 1 ELSE 0 END) as ewallet_count " +
                     "FROM transactions " +
                     "WHERE DATE(created_at) = ?::date";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new TransactionSummary(
                    rs.getInt("txn_count"),
                    rs.getDouble("total_revenue"),
                    rs.getInt("cash_count"),
                    rs.getInt("ewallet_count")
                );
            }
        }
        
        return new TransactionSummary(0, 0, 0, 0);
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        String txnId = rs.getString("transaction_id");
        LocalDateTime dateTime = rs.getTimestamp("created_at").toLocalDateTime();
        
        Transaction txn = new Transaction(txnId, dateTime);
        txn.setTotal(rs.getDouble("total"));
        txn.setAmountPaid(rs.getDouble("amount_paid"));
        txn.setChange(rs.getDouble("change"));
        txn.setPaymentMethod(rs.getString("payment_method"));
        txn.setStatus(rs.getString("status"));
        
        return txn;
    }
    
    // Helper classes for reporting
    public static class TransactionDetail {
        public final String transactionId;
        public final String productName;
        public final int quantity;
        public final double unitPrice;
        public final double subtotal;
        public final String paymentMethod;
        
        public TransactionDetail(String transactionId, String productName, int quantity, double unitPrice, double subtotal, String paymentMethod) {
            this.transactionId = transactionId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
            this.paymentMethod = paymentMethod;
        }
    }
    
    public static class TransactionSummary {
        public final int transactionCount;
        public final double totalRevenue;
        public final int cashCount;
        public final int ewalletCount;
        
        public TransactionSummary(int transactionCount, double totalRevenue, int cashCount, int ewalletCount) {
            this.transactionCount = transactionCount;
            this.totalRevenue = totalRevenue;
            this.cashCount = cashCount;
            this.ewalletCount = ewalletCount;
        }
    }
}
