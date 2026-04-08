package com.palsandpalms.persistence;

import com.palsandpalms.engine.GameState;
import com.palsandpalms.world.Fridge;

import java.util.concurrent.atomic.AtomicBoolean;

/** Background auto-save (NFA-19). */
public final class AutoSaveTask implements Runnable {
    private final SaveManager saveManager;
    private final GameState state;
    private final Fridge fridge;
    private final long intervalMs;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public AutoSaveTask(SaveManager saveManager, GameState state, Fridge fridge, long intervalMs) {
        this.saveManager = saveManager;
        this.state = state;
        this.fridge = fridge;
        this.intervalMs = intervalMs;
    }

    public void shutdown() {
        running.set(false);
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            try {
                saveManager.save(state, fridge);
            } catch (Exception ignored) {
                // log in production
            }
        }
    }
}
