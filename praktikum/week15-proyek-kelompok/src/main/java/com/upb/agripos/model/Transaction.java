package com.upb.agripos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private String id;
    private LocalDateTime dateTime;
    private List<CartItem> items;
    private double total;
    private String paymentMethod;
    private double amountPaid;
    private double change;
    private String status; // PENDING, COMPLETED, CANCELLED

    public Transaction(String id, LocalDateTime dateTime) {
        this.id = id;
        this.dateTime = dateTime;
        this.items = new ArrayList<>();
        this.status = "PENDING";
    }

    public String getId() { return id; }
    public LocalDateTime getDateTime() { return dateTime; }
    public List<CartItem> getItems() { return items; }
    public double getTotal() { return total; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getAmountPaid() { return amountPaid; }
    public double getChange() { return change; }
    public String getStatus() { return status; }

    public void addItem(CartItem item) { items.add(item); }
    public void setTotal(double total) { this.total = total; }
    public void setPaymentMethod(String method) { this.paymentMethod = method; }
    public void setAmountPaid(double amount) { this.amountPaid = amount; }
    public void setChange(double change) { this.change = change; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("TX-%s [%s] Total: Rp%.2f", id, status, total);
    }
}
