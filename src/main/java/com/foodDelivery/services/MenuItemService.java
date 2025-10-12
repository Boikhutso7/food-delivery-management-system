package com.foodDelivery.services;

import com.foodDelivery.database.MenuItemDAO;
import com.foodDelivery.model.MenuItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class MenuItemService {
    private MenuItemDAO menuItemDAO;

    public MenuItemService() {
        this.menuItemDAO = new MenuItemDAO();
    }

    public MenuItem createMenuItem(String name, String description, BigDecimal price, String category) {
        MenuItem item = new MenuItem(name, description, category, price);
        return menuItemDAO.save(item);
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemDAO.findAll();
    }

    public List<MenuItem> getAvailableMenuItems() {
        return menuItemDAO.findAll().stream()
                .filter(MenuItem::isAvailable)
                .toList();
    }

    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemDAO.findByCategory(category);
    }

    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemDAO.findById(id);
    }

    public MenuItem updateMenuItem(Long id, String name, String description, BigDecimal price,
                                   String category, boolean available) {
        Optional<MenuItem> itemOpt = menuItemDAO.findById(id);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Menu item not found with id: " + id);
        }

        MenuItem item = itemOpt.get();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(category);
        item.setAvailable(available);

        return menuItemDAO.save(item);
    }

    public void deleteMenuItem(Long id) {
        menuItemDAO.delete(id);
    }

    public List<String> getCategories() {
        return menuItemDAO.findAll().stream()
                .map(MenuItem::getCategory)
                .distinct()
                .toList();
    }
}
