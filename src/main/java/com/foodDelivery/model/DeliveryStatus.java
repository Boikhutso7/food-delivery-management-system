package com.foodDelivery.model;

public enum DeliveryStatus {
    PENDING("Pending delivery Assignment"),
    ASSIGNED("Driver found"),
    PICKED_UP("Order picked-up"),
    IN_TRANSIT("Order on the way"),
    DELIVERED("order delivered"),
    FAILED("Delivery failed");

    private final String delivery_details;

    DeliveryStatus(String delivery_details){
        this.delivery_details = delivery_details;
    }
    public String getDelivery_details(){
        return  delivery_details;
    }
}
