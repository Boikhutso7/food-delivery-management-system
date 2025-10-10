package com.foodDelivery;

import com.foodDelivery.database.DatabaseConnection;
import com.foodDelivery.userInterface.CustomerInterface;
import com.foodDelivery.userInterface.AdminInterface;
import com.foodDelivery.services.MenuItemService;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Food Delivery Management System ===");
        System.out.println("Initializing database...");

        // Initialize database
        DatabaseConnection.startDatabase();

        // Add sample data if needed
        initializeSampleData();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Customer Interface");
            System.out.println("2. Admin Interface");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    CustomerInterface customerUI = new CustomerInterface();
                    customerUI.showMainMenu();
                }
                case "2" -> {
                    AdminInterface adminUI = new AdminInterface();
                    adminUI.showAdminMenu();
                }
                case "3" -> {
                    System.out.println("Thank you for using Food Delivery System!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void initializeSampleData() {
        MenuItemService menuService = new MenuItemService();

        // Check if database is empty and add sample data
        if (menuService.getAllMenuItems().isEmpty()) {
            System.out.println("Adding sample menu items...");

            menuService.createMenuItem("MOGODU", "Delicious African tripe",
                    new BigDecimal("69.99"), "African Meal");
            menuService.createMenuItem("MALANA", "Delicious African chicken intestines",
                    new BigDecimal("14.99"), "African Meal");
            menuService.createMenuItem("Potato Salad", "Fresh potatoes with Mayonnaise",
                    new BigDecimal("8.99"), "Salads");
            menuService.createMenuItem("Chicken Wings", "Crispy Chicken wings",
                    new BigDecimal("10.99"), "Appetizers");
            menuService.createMenuItem("Chocolate Cake", "Rich chocolate dessert",
                    new BigDecimal("6.99"), "Desserts");

            System.out.println("Sample data added successfully!");
        }
    }
}