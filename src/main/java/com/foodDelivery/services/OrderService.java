package com.foodDelivery.services;

import com.foodDelivery.database.MenuItemDAO;
import com.foodDelivery.database.OrderDAO;
import com.foodDelivery.model.MenuItem;
import com.foodDelivery.model.Order;
import com.foodDelivery.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OrderService {

    private OrderDAO orderDAO;
    private MenuItemDAO menuItemDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.menuItemDAO = new MenuItemDAO();
    }

    public Order createOrder(Order order) {
        // Validate order items and calculate total
        for (Order.OrderItem item : order.getItems()) {
            Optional<MenuItem> menuItem = menuItemDAO.findById(item.getMenuItem().getId());
            if (menuItem.isEmpty() || !menuItem.get().isAvailable()) {
                throw new IllegalArgumentException("Menu item not available or not found: " + item.getMenuItem().getName());
            }
            // Check stock if necessary
            if (menuItem.get().getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for: " + menuItem.get().getName());
            }
        }

        order.calculateTotal();
        order.setStatus(OrderStatus.PENDING);
        return orderDAO.save(order);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Optional<Order> orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            return orderDAO.update(order);
        } else {
            throw new IllegalArgumentException("Order not found with id: " + orderId);
        }
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderDAO.findById(orderId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderDAO.findByStatus(status);
    }
}