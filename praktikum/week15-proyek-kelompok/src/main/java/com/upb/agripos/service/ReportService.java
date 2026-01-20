package com.upb.agripos.service;

import com.upb.agripos.dao.JdbcTransactionDAO;
import com.upb.agripos.dao.JdbcTransactionDAO.TransactionDetail;
import com.upb.agripos.dao.JdbcTransactionDAO.TransactionSummary;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportService {
    private JdbcTransactionDAO transactionDAO;

    public ReportService(Connection connection) {
        this.transactionDAO = new JdbcTransactionDAO(connection);
    }

    public String generateDailyReport(LocalDate date) {
        try {
            StringBuilder report = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String dateStr = date.toString(); // Format: yyyy-MM-dd for SQL
            
            // Get transaction details and summary from database
            List<TransactionDetail> details = transactionDAO.getTransactionDetails(dateStr);
            TransactionSummary summary = transactionDAO.getTransactionSummary(dateStr);
            
            report.append("=====================================\n");
            report.append("   LAPORAN PENJUALAN HARIAN          \n");
            report.append("=====================================\n");
            report.append("Tanggal: ").append(date.format(formatter)).append("\n");
            report.append("-------------------------------------\n");
            report.append(String.format("%-15s %15s %10s %12s\n", "PRODUK", "HARGA", "QTY", "SUBTOTAL"));
            report.append("-------------------------------------\n");
            
            int totalItems = 0;
            
            if (details.isEmpty()) {
                report.append("Tidak ada transaksi untuk hari ini\n");
            } else {
                for (TransactionDetail detail : details) {
                    report.append(String.format("%-15s Rp%13.2f %10d Rp%10.2f\n",
                        detail.productName,
                        detail.unitPrice,
                        detail.quantity,
                        detail.subtotal));
                    
                    totalItems += detail.quantity;
                }
            }
            
            report.append("-------------------------------------\n");
            report.append(String.format("Total Item: %d | Total Transaksi: %d\n", 
                totalItems, summary.transactionCount));
            report.append(String.format("%-30s Rp%10.2f\n", "TOTAL PENDAPATAN", summary.totalRevenue));
            report.append("-------------------------------------\n");
            report.append("Metode Pembayaran:\n");
            report.append(String.format("  Tunai      : %d transaksi\n", summary.cashCount));
            report.append(String.format("  E-Wallet   : %d transaksi\n", summary.ewalletCount));
            report.append("-------------------------------------\n");
            report.append("Generated: " + java.time.LocalDateTime.now() + "\n");
            report.append("=====================================\n");
            
            return report.toString();
        } catch (Exception e) {
            return "Error generating report: " + e.getMessage();
        }
    }

    public String generateTodayReport() {
        return generateDailyReport(LocalDate.now());
    }

    /**
     * Get transaction details formatted for table display
     */
    public List<Map<String, Object>> getDailyReportTableData(LocalDate date) throws Exception {
        String dateStr = date.toString(); // Format: yyyy-MM-dd for SQL
        List<TransactionDetail> details = transactionDAO.getTransactionDetails(dateStr);
        List<Map<String, Object>> tableData = new ArrayList<>();
        
        for (TransactionDetail detail : details) {
            Map<String, Object> row = new HashMap<>();
            row.put("transactionId", detail.transactionId);
            row.put("productName", detail.productName);
            row.put("unitPrice", detail.unitPrice);
            row.put("quantity", detail.quantity);
            row.put("subtotal", detail.subtotal);
            tableData.add(row);
        }
        
        return tableData;
    }
    
    /**
     * Get transaction summary for today
     */
    public TransactionSummary getTodayTransactionSummary() throws Exception {
        return transactionDAO.getTransactionSummary(LocalDate.now().toString());
    }
    
    /**
     * Get transaction summary for a specific date
     */
    public TransactionSummary getDailyTransactionSummary(LocalDate date) throws Exception {
        return transactionDAO.getTransactionSummary(date.toString());
    }
    
    /**
     * Get total items count for a specific date
     */
    public int getTotalItemsForDate(LocalDate date) throws Exception {
        String dateStr = date.toString();
        List<TransactionDetail> details = transactionDAO.getTransactionDetails(dateStr);
        int total = 0;
        for (TransactionDetail detail : details) {
            total += detail.quantity;
        }
        return total;
    }

}
