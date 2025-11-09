package com.foodDelivery.services;

import com.foodDelivery.database.DeliveryDAO;
import com.foodDelivery.model.Delivery;
import com.foodDelivery.model.DeliveryStatus;
import com.foodDelivery.model.Order;

import java.util.List;
import java.util.Optional;

public class DeliveryService {

    private DeliveryDAO deliveryDAO;

    public DeliveryService() {
        this.deliveryDAO = new DeliveryDAO();
    }

    public Delivery assignDelivery(Order order, String driverName, String driverPhone) {
        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDriverName(driverName);
        delivery.setDriverPhone(driverPhone);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        return deliveryDAO.save(delivery);
    }

    public Delivery updateDeliveryStatus(Long deliveryId, DeliveryStatus status) {
        Optional<Delivery> deliveryOpt = deliveryDAO.findById(deliveryId);
        if (deliveryOpt.isPresent()) {
            Delivery delivery = deliveryOpt.get();
            delivery.setStatus(status);

            // Logic to set timestamps
            if (status == DeliveryStatus.PICKED_UP) {
                delivery.setPickupTime(java.time.LocalDateTime.now());
            } else if (status == DeliveryStatus.DELIVERED) {
                delivery.setDeliveredTime(java.time.LocalDateTime.now());
            }

            return deliveryDAO.update(delivery);
        } else {
            throw new IllegalArgumentException("Delivery not found with id: " + deliveryId);
        }
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryDAO.findAll();
    }

    public Optional<Delivery> getDeliveryByOrderId(Long orderId) {
        return deliveryDAO.findByOrderId(orderId);
    }

    // New method for CustomerInterface
    public String getDeliveryTrackingInfo(Long orderId) {
        Optional<Delivery> deliveryOpt = deliveryDAO.findByOrderId(orderId);
        if (deliveryOpt.isEmpty()) {
            return "No delivery information available yet.";
        }

        Delivery delivery = deliveryOpt.get();
        String info = "Status: " + delivery.getStatus().getDelivery_details();

        if (delivery.getStatus() == DeliveryStatus.ASSIGNED ||
                delivery.getStatus() == DeliveryStatus.PICKED_UP ||
                delivery.getStatus() == DeliveryStatus.IN_TRANSIT) {
            info += " (Driver: " + delivery.getDriverName() + ")";
        }

        if (delivery.getPickupTime() != null) {
            info += " | Picked up at: " + delivery.getPickupTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }

        if (delivery.getTrackingNotes() != null) {
            info += " | Notes: " + delivery.getTrackingNotes();
        }

        return info;
    }
}