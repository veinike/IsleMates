package com.palsandpalms.engine;

import com.palsandpalms.model.GameEvent;
import com.palsandpalms.model.Room;
import com.palsandpalms.model.TimeOfDay;
import com.palsandpalms.model.Resident;
import com.palsandpalms.ui.ResidentDragGuard;
import com.palsandpalms.ui.ResidentPositionRegistry;
import com.palsandpalms.world.Fridge;
import com.palsandpalms.world.GameEventQueue;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/** One thread per resident (NFA-02). */
public final class ResidentAI implements Runnable {
    private final UUID residentId;
    private final GameState state;
    private final Fridge fridge;
    private final GameEventQueue eventQueue;
    private final Random random = new Random();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private static final long ROOM_SWITCH_COOLDOWN_MS = 15_000;
    private static final java.util.Map<UUID, Long> LAST_SWITCH_MS = new java.util.concurrent.ConcurrentHashMap<>();

    public ResidentAI(UUID residentId, GameState state, Fridge fridge, GameEventQueue eventQueue) {
        this.residentId = residentId;
        this.state = state;
        this.fridge = fridge;
        this.eventQueue = eventQueue;
    }

    public void shutdown() {
        running.set(false);
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Thread.sleep(400 + random.nextInt(200));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            GameEvent ev = eventQueue.poll();
            state.getRwLock().writeLock().lock();
            Resident self;
            try {
                self = state.findResident(residentId).orElse(null);
                if (self == null) {
                    continue;
                }
                // Passive stat decay
                self.getStatus().tick(-0.3, 0.2, -0.1, -0.15);
                TimeOfDay tod = state.getTimeOfDay();
                if (tod == TimeOfDay.NIGHT) {
                    self.getStatus().tick(0, -0.5, 0, 0);
                }
                if (ev == GameEvent.CONCERT) {
                    setRoomIfNotDragged(self, Room.PARK);
                } else if (ev == GameEvent.DELIVERY) {
                    setRoomIfNotDragged(self, Room.APARTMENT);
                }
                // Needs
                if (self.getStatus().getHunger() < 35) {
                    setRoomIfNotDragged(self, Room.APARTMENT);
                }
                if (tod == TimeOfDay.NIGHT && self.getStatus().getTiredness() > 65) {
                    setRoomIfNotDragged(self, Room.APARTMENT);
                }
                // Leave bathroom once hygiene is restored (probabilistic to spread exits)
                if (self.getCurrentRoom() == Room.BATHROOM && self.getStatus().getHygiene() >= 70) {
                    if (random.nextInt(100) < 45) {
                        setRoomIfNotDragged(self, Room.APARTMENT);
                    }
                }
                // Leisure: wander between island (park) and home when no urgent need
                boolean urgent = self.getStatus().getHunger() < 40
                        || (tod == TimeOfDay.NIGHT && self.getStatus().getTiredness() > 60);
                if (!urgent && random.nextInt(100) < 10) {
                    if (self.getCurrentRoom() == Room.PARK && random.nextInt(100) < 55) {
                        setRoomIfNotDragged(self, Room.APARTMENT);
                    } else if (self.getCurrentRoom() == Room.APARTMENT && random.nextInt(100) < 55) {
                        setRoomIfNotDragged(self, Room.PARK);
                    }
                }
            } finally {
                state.getRwLock().writeLock().unlock();
            }
            // Fridge outside state lock to avoid deadlock order issues
            state.getRwLock().readLock().lock();
            Resident r;
            try {
                r = state.findResident(residentId).orElse(null);
            } finally {
                state.getRwLock().readLock().unlock();
            }
            if (r == null) {
                continue;
            }
            if (r.getCurrentRoom() == Room.APARTMENT && r.getStatus().getHunger() < 40) {
                var food = fridge.takeFood();
                if (food != null) {
                    r.getInventory().add(food);
                    state.getRwLock().writeLock().lock();
                    try {
                        state.findResident(residentId).ifPresent(res -> {
                            res.getStatus().tick(25, 0, 2, 0);
                        });
                    } finally {
                        state.getRwLock().writeLock().unlock();
                    }
                }
            }
        }
    }

    /** Called by UI drag handlers to start the grace timer when the player moves a resident. */
    public static void notifyPlayerRoomSwitch(UUID residentId) {
        LAST_SWITCH_MS.put(residentId, System.currentTimeMillis());
    }

    private boolean cooldownElapsed() {
        long last = LAST_SWITCH_MS.getOrDefault(residentId, 0L);
        return System.currentTimeMillis() - last >= ROOM_SWITCH_COOLDOWN_MS;
    }

    private void setRoomIfNotDragged(Resident r, Room room) {
        if (ResidentDragGuard.isDragged(residentId)) {
            return;
        }
        if (!cooldownElapsed()) {
            return;
        }
        double normX = ResidentPositionRegistry.getNormX(residentId);
        // APARTMENT → PARK only allowed near the exit door (left 10%)
        if (r.getCurrentRoom() == Room.APARTMENT && room == Room.PARK) {
            if (normX > 0.10 + 1e-9) {
                return;
            }
        }
        // PARK → APARTMENT only allowed near the house (center ±5%)
        if (r.getCurrentRoom() == Room.PARK && room == Room.APARTMENT) {
            if (normX < 0.45 - 1e-9 || normX > 0.55 + 1e-9) {
                return;
            }
        }
        // When entering apartment from park, reset position to near the entry door
        if (r.getCurrentRoom() == Room.PARK && room == Room.APARTMENT) {
            ResidentPositionRegistry.update(residentId, 0.05);
        }
        // When entering park from apartment, reset position to near the house
        if (r.getCurrentRoom() == Room.APARTMENT && room == Room.PARK) {
            ResidentPositionRegistry.update(residentId, 0.50);
        }
        r.setCurrentRoom(room);
        LAST_SWITCH_MS.put(residentId, System.currentTimeMillis());
    }
}
