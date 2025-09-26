package com.foodDelivery.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private Order order;
    private MenuItem MOGODU;

    @BeforeEach
    public void setUp(){

        order = new Order();
        order.setCustomerName("Tshepiso");
        order.setCustomerPhone("1234567899");
        order.setCustomerAddress("132 maponya str");
        MOGODU = new MenuItem("MOGODU", "Delicious African tripe","African food",new BigDecimal("69.99"));
    }
    @Test
    public  void testOrderCreation(){

        assertNotNull(order);
        assertEquals("Tshepiso" , order.getCustomerName());
        assertEquals("1234567899", order.getCustomerPhone());
        assertEquals("132 maponya str", order.getCustomerAddress());
        assertEquals(OrderStatus.PENDING,order.getStatus());
        assertNotNull(order.getOrderTime());
        assertTrue(order.getItems().isEmpty());
        assertNull(order.getTotalAmount());
    }

    @Test
    public  void testPriceCalculation(){
        order.addItem(MOGODU,2);
        order.calculateTotal();
        assertEquals(new BigDecimal("139.98"), order.getTotalAmount());
    }
    @Test
    public void TestItemToOrder(){

        order.addItem(MOGODU, 2);
        List<Order.OrderItem> items = order.getItems();
        assertEquals(1, items.size());
        Order.OrderItem firstItem = items.get(0);
        assertEquals(MOGODU, firstItem.getMenuItem());
        assertEquals(2, firstItem.getQuantity());
        assertEquals(new BigDecimal("139.98"), firstItem.getSubtotal());

    }

}
