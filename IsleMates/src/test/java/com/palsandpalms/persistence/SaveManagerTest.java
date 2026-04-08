package com.palsandpalms.persistence;

import com.palsandpalms.engine.GameState;
import com.palsandpalms.model.StarterResidents;
import com.palsandpalms.world.Fridge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SaveManagerTest {

    @Test
    void roundTrip(@TempDir Path dir) throws Exception {
        GameState state = new GameState();
        Fridge fridge = new Fridge(5);
        state.getRwLock().writeLock().lock();
        try {
            var n = StarterResidents.createNina();
            state.addResident(n);
        } finally {
            state.getRwLock().writeLock().unlock();
        }
        SaveManager sm = new SaveManager(dir);
        sm.save(state, fridge);

        GameState state2 = new GameState();
        Fridge fridge2 = new Fridge(0);
        SaveManager.apply(sm.loadRaw(), state2, fridge2);
        state2.getRwLock().readLock().lock();
        try {
            assertEquals(1, state2.getResidentsReadOnly().size());
            assertEquals(5, fridge2.getStock());
        } finally {
            state2.getRwLock().readLock().unlock();
        }
    }
}
