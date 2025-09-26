package com.foodDelivery.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private  Long id;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private List<OrderItem> items;
    private  OrderStatus status;
    private LocalDateTime orderTime;
    private LocalDateTime estimatedDelivery;
    private BigDecimal totalAmount;

    public Order(){
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.orderTime = LocalDateTime.now();
    }

    public static  class OrderItem{
        private  MenuItem menuItem;
        private int quantity;

        public OrderItem(MenuItem menuItem,int quantity){
            this.menuItem = menuItem;
            this.quantity = quantity;
        }

        public MenuItem getMenuItem() {
            return menuItem;
        }
        public void setMenuItem(MenuItem menuItem) {
            this.menuItem = menuItem;
        }
        public int getQuantity() {
            return quantity;
        }
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        public BigDecimal getSubtotal(){
            return menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }
    public void addItem(MenuItem  menuItem, int quantity){
        this.items.add(new OrderItem(menuItem, quantity));
        calculateTotal();
    }
    public void calculateTotal(){
        this.totalAmount = items.stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO,BigDecimal::add);
    }
    public Long getId(){
        return id;
    }
    public  void  setId(Long id){
        this.id = id;
    }
    public String getCustomerName(){
        return  customerName;
    }
    public void setCustomerName(String customerName){
        this.customerName = customerName;
    }
    public String getCustomerPhone(){
        return  customerPhone;
    }
    public void setCustomerPhone(String customerPhone){
        this.customerPhone = customerPhone;
    }
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public LocalDateTime getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
