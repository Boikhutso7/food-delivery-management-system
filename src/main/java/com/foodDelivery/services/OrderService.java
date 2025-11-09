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

    // New method for CustomerInterface
    public Order createOrder(String customerName, String customerPhone, String customerAddress) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerPhone(customerPhone);
        order.setCustomerAddress(customerAddress);
        order.setStatus(OrderStatus.PENDING);
        order.calculateTotal(); // Will be 0.00 initially
        return orderDAO.save(order); // Save the initial order
    }

    // Existing method, slightly modified to ensure status is PENDING
    public Order createOrder(Order order) {
        // Validate order items and calculate total
        for (Order.OrderItem item : order.getItems()) {
            validateOrderItem(item);
        }

        order.calculateTotal();
        order.setStatus(OrderStatus.PENDING);
        return orderDAO.save(order);
    }

    // New method for CustomerInterface
    public Order addItemToOrder(Long orderId, Long menuItemId, int quantity) {
        Order order = getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot add items to an order that is not PENDING. Status: " + order.getStatus());
        }

        MenuItem menuItem = menuItemDAO.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + menuItemId));

        Order.OrderItem item = new Order.OrderItem(menuItem, quantity);
        validateOrderItem(item);

        order.addItem(menuItem, quantity); // This recalculates total

        // Update the order in the database
        return orderDAO.update(order);
    }

    // New method for CustomerInterface
    public Order cancelOrder(Long orderId) {
        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
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

    private void validateOrderItem(Order.OrderItem item) {
        Optional<MenuItem> menuItemOpt = menuItemDAO.findById(item.getMenuItem().getId());
        if (menuItemOpt.isEmpty()) {
            throw new IllegalArgumentException("Menu item not found: " + item.getMenuItem().getName());
        }

        MenuItem menuItem = menuItemOpt.get();
        if (!menuItem.isAvailable()) {
            throw new IllegalArgumentException("Menu item not available: " + menuItem.getName());
        }

        // Check stock if necessary (assuming 0 stock means not tracked)
        if (menuItem.getStockQuantity() > 0 && menuItem.getStockQuantity() < item.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for: " + menuItem.getName());
        }
    }
}