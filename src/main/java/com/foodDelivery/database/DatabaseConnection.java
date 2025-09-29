package com.foodDelivery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    static  final String DB_URL = "jdbc:sqlite:food_delivery.db";
    private  static  Connection connection;

    public static Connection getConnection() throws SQLException{

        if(connection == null || connection.isClosed()){
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void startDatabase(){
        try(Connection conn = getConnection()){

            String createMenuTable = """
                    CREATE TABLE IF NOT EXISTS menu_items(
                        id INTEGER PRIMARY KEY AUTOINCREMENT;
                        name TEXT NOT NULL,
                        description TEXT,
                        price DECIMAL(10,2) NOT NULL,
                        category TEXT NOT NULL,
                        available BOOLEAN DEFAULT TRUE,
                        stock_quantity INTEGER DEFAULT 0
                        )
                    """;

            String createOrdersTable = """
                    CREATE TABLE IF NOT EXISTS orders(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        customer_name TEXT NOT NULL,
                        customer_phone TEXT NOT NULL,
                        delivery_address TEXT NOT NULL,
                        status TEXT NOT NULL,
                        order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        estimated_delivery TIMESTAMP,
                        total_amount DECIMAL(10,2)
                        )
                   """;

            String createOrderItemsTable = """
                    CREATE TABLE IF NOT EXISTS order_items(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        order_id INTEGER,
                        menu_item_id INTEGER,
                        quantity INTEGER NOT NULL,
                        price DECIMAL(10,2) NOT NULL,
                        FOREIGN KEY (order_id) REFERENCES orders(id),
                        FOREIGN KEY (menu_item-id) REFERENCES menu_items(id)
                        )
                    """;
            String createDeliveriesTable = """
                    CREATE TABLE IF NOT EXISTS deliveries(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        order_id INTEGER,
                        driver_name TEXT,
                        driver_phone TEXT,
                        status TEXT NOT NULL,
                        assigned_time TIMESTAMP,
                        pickup_time TIMESTAMP,
                        delivered_time TIMESTAMP,
                        tracking_notes TEXT,
                        FOREIGN KEY (order_id) REFERENCES orders(id)
                        )
                    """;

            conn.createStatement().execute(createMenuTable);
            conn.createStatement().execute(createOrdersTable);
            conn.createStatement().execute(createOrderItemsTable);
            conn.createStatement().execute(createDeliveriesTable);

        } catch (SQLException e){
            throw new RuntimeException("Failed to initialize database",e);
        }
    }
}
