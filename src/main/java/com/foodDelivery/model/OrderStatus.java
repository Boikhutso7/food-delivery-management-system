package com.foodDelivery.model;

public enum OrderStatus {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    PREPARING("Preparing"),
    READY("Ready for Pickup"),
    OUT_FOR_DELIVERY("out for delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String displayName;

    OrderStatus(String displayName){
        this.displayName = displayName;
    }
    public String getDisplayName(){
        return  displayName;
    }
}
