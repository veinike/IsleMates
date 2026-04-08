package com.palsandpalms.engine;

import java.util.concurrent.atomic.AtomicBoolean;

public final class InteractionTicker implements Runnable {
    private final InteractionManager interactionManager;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public InteractionTicker(InteractionManager interactionManager) {
        this.interactionManager = interactionManager;
    }

    public void shutdown() {
        running.set(false);
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            interactionManager.tickPossibleInteractions();
        }
    }
}
