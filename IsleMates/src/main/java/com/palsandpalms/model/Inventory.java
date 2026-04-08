package com.palsandpalms.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Inventory {
    private final List<Item> items = new ArrayList<>();

    public void add(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }
        items.add(item);
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }

    public Item removeFirstOfType(ItemType type) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType() == type) {
                return items.remove(i);
            }
        }
        return null;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int size() {
        return items.size();
    }

    public static Inventory copyOf(Inventory other) {
        Inventory inv = new Inventory();
        for (Item it : other.items) {
            inv.items.add(new Item(it.getType(), it.getId()));
        }
        return inv;
    }
}
