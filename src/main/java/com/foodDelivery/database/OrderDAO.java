package com.foodDelivery.database;

import com.foodDelivery.model.Order;
import com.foodDelivery.model.OrderStatus;
import com.foodDelivery.model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAO {

    public Order save(Order order) {
        String sql = "INSERT INTO orders (customer_name, customer_phone, delivery_address, status, total_amount) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, order.getCustomerName());
            stmt.setString(2, order.getCustomerPhone());
            stmt.setString(3, order.getCustomerAddress());
            stmt.setString(4, order.getStatus().name());
            stmt.setBigDecimal(5, order.getTotalAmount());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getLong(1));
                }
            }

            // Save order items
            saveOrderItems(order);

        } catch (SQLException e) {
            throw new RuntimeException("Error saving order", e);
        }
        return order;
    }

    private void saveOrderItems(Order order) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, menu_item_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Order.OrderItem item : order.getItems()) {
                stmt.setLong(1, order.getId());
                stmt.setLong(2, item.getMenuItem().getId());
                stmt.setInt(3, item.getQuantity());
                stmt.setBigDecimal(4, item.getMenuItem().getPrice());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public Optional<Order> findById(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        Order order = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                order = mapResultSetToOrder(rs);
                // Load order items
                order.setItems(findOrderItemsByOrderId(id));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order by id: " + id, e);
        }
        return Optional.ofNullable(order);
    }

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(findOrderItemsByOrderId(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all orders", e);
        }
        return orders;
    }

    public List<Order> findByStatus(OrderStatus status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY order_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(findOrderItemsByOrderId(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding orders by status: " + status, e);
        }
        return orders;
    }

    public Order update(Order order) {
        String sql = "UPDATE orders SET customer_name = ?, customer_phone = ?, delivery_address = ?, status = ?, total_amount = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, order.getCustomerName());
            stmt.setString(2, order.getCustomerPhone());
            stmt.setString(3, order.getCustomerAddress());
            stmt.setString(4, order.getStatus().name());
            stmt.setBigDecimal(5, order.getTotalAmount());
            stmt.setLong(6, order.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order", e);
        }
        return order;
    }

    private List<Order.OrderItem> findOrderItemsByOrderId(Long orderId) {
        List<Order.OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, mi.name, mi.description, mi.category " +
                "FROM order_items oi " +
                "JOIN menu_items mi ON oi.menu_item_id = mi.id " +
                "WHERE oi.order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MenuItem menuItem = new MenuItem();
                menuItem.setId(rs.getLong("menu_item_id"));
                menuItem.setName(rs.getString("name"));
                menuItem.setDescription(rs.getString("description"));
                menuItem.setCategory(rs.getString("category"));
                menuItem.setPrice(rs.getBigDecimal("price"));

                int quantity = rs.getInt("quantity");
                items.add(new Order.OrderItem(menuItem, quantity));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order items for order id: " + orderId, e);
        }
        return items;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setCustomerPhone(rs.getString("customer_phone"));
        order.setCustomerAddress(rs.getString("delivery_address"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setOrderTime(rs.getTimestamp("order_time").toLocalDateTime());
        if (rs.getTimestamp("estimated_delivery") != null) {
            order.setEstimatedDelivery(rs.getTimestamp("estimated_delivery").toLocalDateTime());
        }
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        return order;
    }
}