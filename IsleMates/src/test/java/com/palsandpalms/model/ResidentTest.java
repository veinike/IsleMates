package com.palsandpalms.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResidentTest {

    @Test
    void statusClamps() {
        StatusValues s = new StatusValues();
        s.setHunger(150);
        assertEquals(100, s.getHunger());
        s.setHunger(-10);
        assertEquals(0, s.getHunger());
    }

    @Test
    void inventoryAddRemoveByReference() {
        Inventory inv = new Inventory();
        Item a = new Item(ItemType.APPLE);
        inv.add(a);
        assertTrue(inv.remove(a));
        assertEquals(0, inv.size());
    }

    @Test
    void inventoryRejectsNull() {
        Inventory inv = new Inventory();
        assertThrows(IllegalArgumentException.class, () -> inv.add(null));
    }
}
