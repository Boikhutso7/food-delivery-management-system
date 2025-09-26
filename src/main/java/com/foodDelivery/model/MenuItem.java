package com.foodDelivery.model;

import java.math.BigDecimal;

public class MenuItem {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private  String category;
    private int stockQuantity;
    private BigDecimal price;

    public MenuItem(String name, String description, String category, BigDecimal price){
                this.name = name;
                this.description = description;
                this.category = category;
                this.price  = price;
                this.available = true;
                this.stockQuantity = 0;
    }

    public Long getId(){
        return  id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getName(){
        return  name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getDescription(){
        return  description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public BigDecimal getPrice(){
        return  price;
    }
    public void setPrice(BigDecimal price){
        this.price = price;
    }
    public  boolean isAvailable(){
        return  available;
    }
    public void setAvailable(boolean available){
        this.available = available;
    }
    public int getStockQuantity(){
        return  stockQuantity;
    }
    public void setStockQuantity(int stockQuantity){
        this.stockQuantity = stockQuantity;
    }
    public String getCategory(){
        return  category;
    }
    public void setCategory(String category){
        this.category = category;
    }
    @Override
    public String toString(){
        return  String.format("%s - $%.2f (%s)", name , price, category);
    }
}
