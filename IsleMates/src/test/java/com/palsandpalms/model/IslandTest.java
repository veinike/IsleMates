package com.palsandpalms.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IslandTest {

    @Test
    void maxFourResidents() {
        Island island = new Island();
        for (int i = 0; i < 4; i++) {
            island.addResident(UUID.randomUUID());
        }
        assertThrows(IllegalStateException.class, () -> island.addResident(UUID.randomUUID()));
    }

    @Test
    void timeOfDayCycles() {
        assertEquals(TimeOfDay.NOON, TimeOfDay.MORNING.next());
        assertEquals(TimeOfDay.NIGHT, TimeOfDay.EVENING.next());
        assertEquals(TimeOfDay.MORNING, TimeOfDay.NIGHT.next());
    }
}
