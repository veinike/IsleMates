package com.palsandpalms.engine;

import com.palsandpalms.model.GameEvent;
import com.palsandpalms.world.GameEventQueue;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/** Produces random global events (NFA-03). */
public final class EventManager implements Runnable {
    private final GameEventQueue eventQueue;
    private final GameState state;
    private final Random random = new Random();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final long minSleepMs;
    private final long maxSleepMs;

    public EventManager(GameEventQueue eventQueue, GameState state, long minSleepMs, long maxSleepMs) {
        this.eventQueue = eventQueue;
        this.state = state;
        this.minSleepMs = minSleepMs;
        this.maxSleepMs = maxSleepMs;
    }

    public void shutdown() {
        running.set(false);
    }

    @Override
    public void run() {
        GameEvent[] all = GameEvent.values();
        while (running.get()) {
            try {
                long sleep = minSleepMs + (long) (random.nextDouble() * (maxSleepMs - minSleepMs));
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            GameEvent ev = all[random.nextInt(all.length)];
            eventQueue.offer(ev);
            state.getRwLock().writeLock().lock();
            try {
                if (!state.getActiveGlobalEvents().contains(ev)) {
                    state.getActiveGlobalEvents().add(ev);
                }
            } finally {
                state.getRwLock().writeLock().unlock();
            }
        }
    }
}
