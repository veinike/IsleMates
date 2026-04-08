package com.palsandpalms.input;

import com.palsandpalms.engine.GameState;
import com.palsandpalms.engine.InteractionManager;
import com.palsandpalms.model.Item;
import com.palsandpalms.model.ItemType;
import com.palsandpalms.model.Resident;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** Processes player commands on a dedicated thread (NFA-03, NFA-17). */
public final class InputHandler implements Runnable {
    private final BlockingQueue<PlayerCommand> queue = new LinkedBlockingQueue<>();
    private final GameState state;
    private final InteractionManager interactionManager;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public InputHandler(GameState state, InteractionManager interactionManager) {
        this.state = state;
        this.interactionManager = interactionManager;
    }

    public void submit(PlayerCommand cmd) {
        queue.offer(cmd);
    }

    public void shutdown() {
        running.set(false);
    }

    /** Allow restarting the handler loop after {@link #shutdown()} (new game session). */
    public void reset() {
        running.set(true);
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                PlayerCommand cmd = queue.poll(200, TimeUnit.MILLISECONDS);
                if (cmd == null) {
                    continue;
                }
                switch (cmd) {
                    case PlayerCommand.GiveItem gi -> handleGive(gi.residentId(), gi.itemType());
                    case PlayerCommand.ForceInteraction fi ->
                            interactionManager.applyInteraction(fi.a(), fi.b(), fi.type(), null);
                    case PlayerCommand.AddResidentRequest ignored -> { /* UI creates residents directly */ }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void handleGive(UUID residentId, ItemType type) {
        state.getRwLock().writeLock().lock();
        try {
            Resident r = state.findResident(residentId).orElse(null);
            if (r != null) {
                r.getInventory().add(new Item(type));
            }
        } finally {
            state.getRwLock().writeLock().unlock();
        }
    }
}
