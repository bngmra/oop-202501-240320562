package com.upb.agripos.service;

import com.upb.agripos.model.CartItem;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReceiptService {
    
    public String generateReceipt(String transactionId, LocalDateTime dateTime, List<CartItem> items,
                                  double total, double amountPaid, double change, String paymentMethod) {
        StringBuilder receipt = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        receipt.append("=====================================\n");
        receipt.append("        AGRI-POS RECEIPT              \n");
        receipt.append("=====================================\n");
        receipt.append("Transaction ID: ").append(transactionId).append("\n");
        receipt.append("Date/Time: ").append(dateTime.format(formatter)).append("\n");
        receipt.append("-------------------------------------\n");
        receipt.append(String.format("%-20s %10s %12s\n", "ITEM", "QTY", "SUBTOTAL"));
        receipt.append("-------------------------------------\n");
        
        for (CartItem item : items) {
            receipt.append(String.format("%-20s %10d Rp%10.2f\n",
                item.getProduct().getName(),
                item.getQuantity(),
                item.getSubtotal()));
        }
        
        receipt.append("-------------------------------------\n");
        receipt.append(String.format("%-20s         Rp%10.2f\n", "TOTAL", total));
        receipt.append(String.format("%-20s         Rp%10.2f\n", "AMOUNT PAID", amountPaid));
        receipt.append(String.format("%-20s         Rp%10.2f\n", "CHANGE", change));
        receipt.append("-------------------------------------\n");
        receipt.append(String.format("Payment Method: %s\n", paymentMethod));
        receipt.append("=====================================\n");
        receipt.append("       Thank you for your purchase    \n");
        receipt.append("=====================================\n");
        
        return receipt.toString();
    }
}
