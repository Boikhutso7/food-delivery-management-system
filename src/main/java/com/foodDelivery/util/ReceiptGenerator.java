package com.foodDelivery.util;

import com.foodDelivery.model.Order;
import java.time.format.DateTimeFormatter;

public class ReceiptGenerator {

    public static String generateReceipt(Order order) {
        StringBuilder receipt = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        receipt.append("========================================\n");
        receipt.append("          RECEIPT / ORDER SUMMARY          \n");
        receipt.append("========================================\n");
        receipt.append("Order ID: ").append(order.getId()).append("\n");
        receipt.append("Order Time: ").append(order.getOrderTime().format(dtf)).append("\n");
        receipt.append("Status: ").append(order.getStatus().getDisplayName()).append("\n");
        receipt.append("----------------------------------------\n");
        receipt.append("Customer: ").append(order.getCustomerName()).append("\n");
        receipt.append("Phone: ").append(order.getCustomerPhone()).append("\n");
        receipt.append("Address: ").append(order.getCustomerAddress()).append("\n");
        receipt.append("----------------------------------------\n");
        receipt.append("Items:\n");

        for (Order.OrderItem item : order.getItems()) {
            receipt.append(String.format("  %-20s x%d   R%.2f\n",
                    item.getMenuItem().getName(),
                    item.getQuantity(),
                    item.getSubtotal()));
        }

        receipt.append("----------------------------------------\n");
        receipt.append(String.format("TOTAL:                          R%.2f\n", order.getTotalAmount()));
        receipt.append("========================================\n");
        receipt.append("    Thank you for your order!    \n");
        receipt.append("========================================\n");

        return receipt.toString();
    }
}