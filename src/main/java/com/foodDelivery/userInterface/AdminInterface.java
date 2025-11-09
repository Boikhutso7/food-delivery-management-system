package com.foodDelivery.userInterface;

import com.foodDelivery.model.*;
import com.foodDelivery.services.DeliveryService;
import com.foodDelivery.services.MenuItemService;
import com.foodDelivery.services.OrderService;
import com.foodDelivery.util.InputValidator;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class AdminInterface {
    private Scanner scanner;
    private MenuItemService menuItemService;
    private OrderService orderService;
    private DeliveryService deliveryService;

    public AdminInterface() {
        this.scanner = new Scanner(System.in);
        this.menuItemService = new MenuItemService();
        this.orderService = new OrderService();
        this.deliveryService = new DeliveryService();
    }

    public void showAdminMenu() {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Manage Menu Items");
            System.out.println("2. View All Orders");
            System.out.println("3. View Orders by Status");
            System.out.println("4. Update Order Status");
            System.out.println("5. Assign Delivery");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> manageMenuItems();
                case 2 -> viewAllOrders();
                case 3 -> viewOrdersByStatus();
                case 4 -> updateOrderStatus();
                case 5 -> assignDelivery();
                case 6 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void manageMenuItems() {
        while (true) {
            System.out.println("\n--- Manage Menu Items ---");
            System.out.println("1. View All Menu Items");
            System.out.println("2. Add New Menu Item");
            System.out.println("3. Update Menu Item");
            System.out.println("4. Delete Menu Item");
            System.out.println("5. Back to Admin Menu");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> viewAllMenuItems();
                case 2 -> addMenuItem();
                case 3 -> updateMenuItem();
                case 4 -> deleteMenuItem();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewAllMenuItems() {
        List<MenuItem> items = menuItemService.getAllMenuItems();
        System.out.println("\n--- All Menu Items ---");
        if (items.isEmpty()) {
            System.out.println("No menu items found.");
            return;
        }
        for (MenuItem item : items) {
            System.out.printf("ID: %d - %s (%s) - R%.2f - Available: %s\n",
                    item.getId(), item.getName(), item.getCategory(), item.getPrice(), item.isAvailable());
            System.out.println("  " + item.getDescription());
        }
    }

    private void addMenuItem() {
        System.out.println("\n--- Add New Menu Item ---");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Enter Category: ");
        String category = scanner.nextLine().trim();
        System.out.print("Enter Price: ");
        BigDecimal price = getBigDecimalInput();

        try {
            MenuItem newItem = menuItemService.createMenuItem(name, description, price, category);
            System.out.println("Menu item added successfully! ID: " + newItem.getId());
        } catch (Exception e) {
            System.out.println("Error adding menu item: " + e.getMessage());
        }
    }

    private void updateMenuItem() {
        System.out.print("Enter Menu Item ID to update: ");
        Long id = getLongInput();

        try {
            MenuItem item = menuItemService.getMenuItemById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Menu item not found."));

            System.out.printf("Updating: %s\n", item.getName());
            System.out.print("Enter new Name (or press Enter to keep '" + item.getName() + "'): ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = item.getName();

            System.out.print("Enter new Description (or press Enter to keep '" + item.getDescription() + "'): ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) description = item.getDescription();

            System.out.print("Enter new Category (or press Enter to keep '" + item.getCategory() + "'): ");
            String category = scanner.nextLine().trim();
            if (category.isEmpty()) category = item.getCategory();

            System.out.print("Enter new Price (or press Enter to keep '" + item.getPrice() + "'): ");
            String priceStr = scanner.nextLine().trim();
            BigDecimal price = priceStr.isEmpty() ? item.getPrice() : new BigDecimal(priceStr);

            System.out.print("Is it available? (true/false) (or press Enter to keep '" + item.isAvailable() + "'): ");
            String availableStr = scanner.nextLine().trim();
            boolean available = availableStr.isEmpty() ? item.isAvailable() : Boolean.parseBoolean(availableStr);

            menuItemService.updateMenuItem(id, name, description, price, category, available);
            System.out.println("Menu item updated successfully!");

        } catch (Exception e) {
            System.out.println("Error updating menu item: " + e.getMessage());
        }
    }

    private void deleteMenuItem() {
        System.out.print("Enter Menu Item ID to delete: ");
        Long id = getLongInput();

        try {
            menuItemService.getMenuItemById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Menu item not found."));

            menuItemService.deleteMenuItem(id);
            System.out.println("Menu item deleted successfully!");
        } catch (Exception e) {
            System.out.println("Error deleting menu item: " + e.getMessage());
        }
    }

    private void viewAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        System.out.println("\n--- All Orders ---");
        displayOrders(orders);
    }

    private void viewOrdersByStatus() {
        System.out.println("Select Status to view:");
        for (OrderStatus status : OrderStatus.values()) {
            System.out.println(status.ordinal() + ". " + status.getDisplayName());
        }
        System.out.print("Choose an option: ");
        int statusChoice = getIntInput();

        if (statusChoice < 0 || statusChoice >= OrderStatus.values().length) {
            System.out.println("Invalid status choice.");
            return;
        }

        OrderStatus status = OrderStatus.values()[statusChoice];
        List<Order> orders = orderService.getOrdersByStatus(status);
        System.out.println("\n--- Orders: " + status.getDisplayName() + " ---");
        displayOrders(orders);
    }

    private void displayOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Order order : orders) {
            System.out.println("----------------------------------------");
            System.out.printf("Order ID: %d | Status: %s | Total: R%.2f\n",
                    order.getId(), order.getStatus().getDisplayName(), order.getTotalAmount());
            System.out.printf("Customer: %s | Phone: %s | Address: %s\n",
                    order.getCustomerName(), order.getCustomerPhone(), order.getCustomerAddress());
            System.out.println("Order Time: " + order.getOrderTime().format(dtf));
            System.out.println("Items:");
            for (Order.OrderItem item : order.getItems()) {
                System.out.printf("  - %s (ID: %d) x%d\n",
                        item.getMenuItem().getName(), item.getMenuItem().getId(), item.getQuantity());
            }
        }
    }

    private void updateOrderStatus() {
        System.out.print("Enter Order ID to update: ");
        Long orderId = getLongInput();

        try {
            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found."));

            System.out.println("Current Status: " + order.getStatus().getDisplayName());
            System.out.println("Select New Status:");
            for (OrderStatus status : OrderStatus.values()) {
                System.out.println(status.ordinal() + ". " + status.getDisplayName());
            }
            System.out.print("Choose an option: ");
            int statusChoice = getIntInput();

            if (statusChoice < 0 || statusChoice >= OrderStatus.values().length) {
                System.out.println("Invalid status choice.");
                return;
            }

            OrderStatus newStatus = OrderStatus.values()[statusChoice];
            orderService.updateOrderStatus(orderId, newStatus);
            System.out.println("Order status updated successfully!");

        } catch (Exception e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
    }

    private void assignDelivery() {
        System.out.print("Enter Order ID to assign for delivery: ");
        Long orderId = getLongInput();

        try {
            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found."));

            if (order.getStatus() != OrderStatus.READY) {
                System.out.println("Order is not 'Ready for Pickup'. Current status: " + order.getStatus().getDisplayName());
                System.out.print("Do you want to force assign? (y/n): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    return;
                }
            }

            System.out.print("Enter Driver Name: ");
            String driverName = scanner.nextLine().trim();
            if (!InputValidator.isValidName(driverName)) {
                System.out.println("Invalid driver name.");
                return;
            }

            System.out.print("Enter Driver Phone: ");
            String driverPhone = scanner.nextLine().trim();
            if (!InputValidator.isValidPhoneNumber(driverPhone)) {
                System.out.println("Invalid driver phone number.");
                return;
            }

            Delivery delivery = deliveryService.assignDelivery(order, driverName, driverPhone);
            orderService.updateOrderStatus(orderId, OrderStatus.OUT_FOR_DELIVERY);

            System.out.println("Delivery assigned successfully! Delivery ID: " + delivery.getId());
            System.out.println("Order status updated to OUT_FOR_DELIVERY.");

        } catch (Exception e) {
            System.out.println("Error assigning delivery: " + e.getMessage());
        }
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Long getLongInput() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    private BigDecimal getBigDecimalInput() {
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid format. Please enter a valid number: ");
            }
        }
    }
}