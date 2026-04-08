package com.palsandpalms.engine;

import java.util.concurrent.atomic.AtomicBoolean;

/** Advances game time in its own thread (NFA-03). */
public final class GameClock implements Runnable {

    private final GameState state;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final long tickIntervalMs;

    public GameClock(GameState state, long tickIntervalMs) {
        this.state = state;
        this.tickIntervalMs = tickIntervalMs;
    }

    public void shutdown() {
        running.set(false);
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Thread.sleep(tickIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            state.getRwLock().writeLock().lock();
            try {
                state.incrementTick();
                state.advanceDayNightCycle(tickIntervalMs);
            } finally {
                state.getRwLock().writeLock().unlock();
            }
        }
    }
}
