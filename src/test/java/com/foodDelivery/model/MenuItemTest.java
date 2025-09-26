package com.foodDelivery.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MenuItemTest {

    private MenuItem menuItem;

    @BeforeEach
    public void setUp(){
        menuItem = new MenuItem("MOGODU", "Delicious African tripe", "African food", new BigDecimal("69.99"));
    }

    @Test
    public void testMenuItem(){

        assertNotNull(menuItem);
        assertEquals("MOGODU", menuItem.getName());
        assertEquals("Delicious African tripe",menuItem.getDescription());
        assertEquals("African food", menuItem.getCategory());
        assertEquals(new BigDecimal("69.99"), menuItem.getPrice());
        assertTrue(menuItem.isAvailable());
        assertEquals(0, menuItem.getStockQuantity());
    }

}
