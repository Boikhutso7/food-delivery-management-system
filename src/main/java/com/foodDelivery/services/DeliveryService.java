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
}