package com.foodDelivery.userInterface;

import com.foodDelivery.model.*;
import com.foodDelivery.services.MenuItemService;
import com.foodDelivery.services.OrderService;
import com.foodDelivery.services.DeliveryService;
import com.foodDelivery.util.InputValidator;
import com.foodDelivery.util.ReceiptGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class CustomerInterface {
    private Scanner scanner;
    private MenuItemService menuItemService;
    private OrderService orderService;
    private DeliveryService deliveryService;
    private Order currentOrder;

    public CustomerInterface() {
        this.scanner = new Scanner(System.in);
        this.menuItemService = new MenuItemService();
        this.orderService = new OrderService();
        this.deliveryService = new DeliveryService();
    }

    public void showMainMenu() {
        while (true) {
            System.out.println("\n=== FOOD DELIVERY SYSTEM ===");
            System.out.println("1. Browse Menu");
            System.out.println("2. Start New Order");
            System.out.println("3. Track Order");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> browseMenu();
                case 2 -> startNewOrder();
                case 3 -> trackOrder();
                case 4 -> {
                    System.out.println("Thank you for using our service!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void browseMenu() {
        System.out.println("\n=== OUR MENU ===");
        List<String> categories = menuItemService.getCategories();

        for (String category : categories) {
            System.out.println("\n--- " + category.toUpperCase() + " ---");
            List<MenuItem> items = menuItemService.getMenuItemsByCategory(category);
            for (MenuItem item : items) {
                System.out.printf("%d. %s - R%.2f\n", item.getId(), item.getName(), item.getPrice());
                System.out.println("   " + item.getDescription());
                if (!item.isAvailable()) {
                    System.out.println("   [Currently Unavailable]");
                }
                System.out.println();
            }
        }
    }

    private void startNewOrder() {
        System.out.println("\n=== NEW ORDER ===");

        // Get customer information
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        if (!InputValidator.isValidName(name)) {
            System.out.println("Invalid name. Please enter a valid name.");
            return;
        }

        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine().trim();
        if (!InputValidator.isValidPhoneNumber(phone)) {
            System.out.println("Invalid phone number. Please enter 10-15 digits.");
            return;
        }

        System.out.print("Enter delivery address: ");
        String address = scanner.nextLine().trim();
        if (!InputValidator.isValidAddress(address)) {
            System.out.println("Invalid address. Please enter a complete address.");
            return;
        }

        // Create new order
        currentOrder = orderService.createOrder(name, phone, address);
        System.out.println("Order created! Order ID: " + currentOrder.getId());

        // Add items to order
        addItemsToOrder();
    }

    private void addItemsToOrder() {
        while (true) {
            System.out.println("\nCurrent Order Items:");
            if (currentOrder.getItems().isEmpty()) {
                System.out.println("  No items added yet.");
            } else {
                for (Order.OrderItem item : currentOrder.getItems()) {
                    System.out.printf("  %s x%d - R%.2f\n",
                            item.getMenuItem().getName(), item.getQuantity(), item.getSubtotal());
                }
                System.out.printf("Current Total: R%.2f\n", currentOrder.getTotalAmount());
            }

            System.out.println("\n1. Add item to order");
            System.out.println("2. Confirm order");
            System.out.println("3. Cancel order");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> addMenuItem();
                case 2 -> confirmOrder();
                case 3 -> {
                    orderService.cancelOrder(currentOrder.getId());
                    System.out.println("Order cancelled.");
                    currentOrder = null;
                    return;
                }
                default -> System.out.println("Invalid option.");
            }

            if (currentOrder == null) return; // Order was confirmed or cancelled
        }
    }

    private void addMenuItem() {
        browseMenu();
        System.out.print("Enter menu item ID to add: ");
        Long itemId = getLongInput();

        System.out.print("Enter quantity: ");
        int quantity = getIntInput();

        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return;
        }

        try {
            currentOrder = orderService.addItemToOrder(currentOrder.getId(), itemId, quantity);
            System.out.println("Item added to order!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void confirmOrder() {
        if (currentOrder.getItems().isEmpty()) {
            System.out.println("Cannot confirm empty order.");
            return;
        }

        try {
            currentOrder = orderService.updateOrderStatus(currentOrder.getId(), OrderStatus.CONFIRMED);
            String receipt = ReceiptGenerator.generateReceipt(currentOrder);
            System.out.println("\n" + receipt);
            System.out.println("Order confirmed! Your food is being prepared.");
            currentOrder = null;
        } catch (Exception e) {
            System.out.println("Error confirming order: " + e.getMessage());
        }
    }

    private void trackOrder() {
        System.out.print("Enter your order ID: ");
        Long orderId = getLongInput();

        try {
            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found."));

            System.out.println("\n=== ORDER TRACKING ===");
            System.out.println("Order ID: " + order.getId());
            System.out.println("Status: " + order.getStatus().getDisplayName());
            System.out.println("Total: R" + order.getTotalAmount());

            if (order.getEstimatedDelivery() != null) {
                System.out.println("Estimated Delivery: " +
                        order.getEstimatedDelivery().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
            }

            String trackingInfo = deliveryService.getDeliveryTrackingInfo(orderId);
            System.out.println("Delivery: " + trackingInfo);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
}