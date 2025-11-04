package com.foodDelivery.database;

import com.foodDelivery.model.Delivery;
import com.foodDelivery.model.DeliveryStatus;
import com.foodDelivery.model.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeliveryDAO {

    public Delivery save(Delivery delivery) {
        String sql = "INSERT INTO deliveries (order_id, driver_name, driver_phone, status, assigned_time) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, delivery.getOrder().getId());
            stmt.setString(2, delivery.getDriverName());
            stmt.setString(3, delivery.getDriverPhone());
            stmt.setString(4, delivery.getStatus().name());
            stmt.setTimestamp(5, Timestamp.valueOf(delivery.getAssignedTime()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating delivery failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    delivery.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving delivery", e);
        }
        return delivery;
    }

    public Optional<Delivery> findById(Long id) {
        String sql = "SELECT d.*, o.customer_name, o.customer_phone, o.delivery_address " +
                "FROM deliveries d " +
                "JOIN orders o ON d.order_id = o.id " +
                "WHERE d.id = ?";
        Delivery delivery = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                delivery = mapResultSetToDelivery(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding delivery by id: " + id, e);
        }
        return Optional.ofNullable(delivery);
    }

    public Optional<Delivery> findByOrderId(Long orderId) {
        String sql = "SELECT d.*, o.customer_name, o.customer_phone, o.delivery_address " +
                "FROM deliveries d " +
                "JOIN orders o ON d.order_id = o.id " +
                "WHERE d.order_id = ?";
        Delivery delivery = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                delivery = mapResultSetToDelivery(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding delivery by order id: " + orderId, e);
        }
        return Optional.ofNullable(delivery);
    }

    public List<Delivery> findAll() {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT d.*, o.customer_name, o.customer_phone, o.delivery_address " +
                "FROM deliveries d " +
                "JOIN orders o ON d.order_id = o.id " +
                "ORDER BY d.assigned_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                deliveries.add(mapResultSetToDelivery(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all deliveries", e);
        }
        return deliveries;
    }

    public Delivery update(Delivery delivery) {
        String sql = "UPDATE deliveries SET driver_name = ?, driver_phone = ?, status = ?, " +
                "pickup_time = ?, delivered_time = ?, tracking_notes = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, delivery.getDriverName());
            stmt.setString(2, delivery.getDriverPhone());
            stmt.setString(3, delivery.getStatus().name());
            stmt.setTimestamp(4, delivery.getPickupTime() != null ? Timestamp.valueOf(delivery.getPickupTime()) : null);
            stmt.setTimestamp(5, delivery.getDeliveredTime() != null ? Timestamp.valueOf(delivery.getDeliveredTime()) : null);
            stmt.setString(6, delivery.getTrackingNotes());
            stmt.setLong(7, delivery.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating delivery", e);
        }
        return delivery;
    }

    private Delivery mapResultSetToDelivery(ResultSet rs) throws SQLException {
        Delivery delivery = new Delivery();
        delivery.setId(rs.getLong("id"));

        // Create order with basic info
        Order order = new Order();
        order.setId(rs.getLong("order_id"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setCustomerPhone(rs.getString("customer_phone"));
        order.setCustomerAddress(rs.getString("delivery_address"));
        delivery.setOrder(order);

        delivery.setDriverName(rs.getString("driver_name"));
        delivery.setDriverPhone(rs.getString("driver_phone"));
        delivery.setStatus(DeliveryStatus.valueOf(rs.getString("status")));
        delivery.setAssignedTime(rs.getTimestamp("assigned_time").toLocalDateTime());
        if (rs.getTimestamp("pickup_time") != null) {
            delivery.setPickupTime(rs.getTimestamp("pickup_time").toLocalDateTime());
        }
        if (rs.getTimestamp("delivered_time") != null) {
            delivery.setDeliveredTime(rs.getTimestamp("delivered_time").toLocalDateTime());
        }
        delivery.setTrackingNotes(rs.getString("tracking_notes"));
        return delivery;
    }
}