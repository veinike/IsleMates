package com.palsandpalms.world;

import com.palsandpalms.model.Item;
import com.palsandpalms.model.ItemType;

import java.util.ArrayList;
import java.util.List;

/** Shared fridge with synchronized access (NFA-05). */
public final class Fridge {
    private int foodUnits;
    private final List<ItemType> stockTypes = new ArrayList<>();

    public Fridge(int initialFoodUnits) {
        this.foodUnits = Math.max(0, initialFoodUnits);
        for (int i = 0; i < foodUnits; i++) {
            stockTypes.add(ItemType.APPLE);
        }
    }

    public synchronized int getStock() {
        return foodUnits;
    }

    public synchronized Item takeFood() {
        if (foodUnits <= 0) {
            return null;
        }
        foodUnits--;
        if (!stockTypes.isEmpty()) {
            stockTypes.removeLast();
        }
        return new Item(ItemType.APPLE);
    }

    public synchronized void addFood(int count) {
        for (int i = 0; i < count; i++) {
            foodUnits++;
            stockTypes.add(ItemType.APPLE);
        }
    }

    public synchronized void addDeliveryItems(List<ItemType> types) {
        for (ItemType t : types) {
            foodUnits++;
            stockTypes.add(t);
        }
    }

    /** Replace stock (e.g. after load). */
    public synchronized void setFoodUnits(int units) {
        this.foodUnits = Math.max(0, units);
        stockTypes.clear();
        for (int i = 0; i < foodUnits; i++) {
            stockTypes.add(ItemType.APPLE);
        }
    }
}
