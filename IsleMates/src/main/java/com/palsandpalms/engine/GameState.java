package com.palsandpalms.engine;

import com.palsandpalms.model.GameEvent;
import com.palsandpalms.model.Inventory;
import com.palsandpalms.model.Island;
import com.palsandpalms.model.Relationship;
import com.palsandpalms.model.RelationshipPair;
import com.palsandpalms.model.Resident;
import com.palsandpalms.model.Room;
import com.palsandpalms.model.TimeOfDay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/** Global game state with read/write lock (NFA-10). */
public final class GameState {
    /** One full day–night rotation (real-time); first half is “day”, second half “night”. */
    public static final long DAY_NIGHT_CYCLE_MS = 6L * 60_000;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final List<Resident> residents = new ArrayList<>();
    private final Island island = new Island();
    private final Map<RelationshipPair, Relationship> relationships = new ConcurrentHashMap<>();
    private TimeOfDay timeOfDay = TimeOfDay.MORNING;
    /** Position in {@link #DAY_NIGHT_CYCLE_MS}; drives {@link #timeOfDay} and the HUD cycle graphic. */
    private long dayNightCycleMs;
    private long gameTick;
    private final List<GameEvent> activeGlobalEvents = new ArrayList<>();
    private boolean tutorialCompleted;

    public ReadWriteLock getRwLock() {
        return rwLock;
    }

    public void addResident(Resident r) {
        residents.add(r);
        island.addResident(r.getId());
    }

    public void removeResident(UUID id) {
        residents.removeIf(res -> res.getId().equals(id));
        island.removeResident(id);
        relationships.keySet().removeIf(p -> p.a().equals(id) || p.b().equals(id));
    }

    public List<Resident> getResidentsSnapshot() {
        return residents.stream().map(this::copyResidentShallow).collect(Collectors.toCollection(ArrayList::new));
    }

    /** Snapshot without deep copy of inventory for performance in UI. */
    public List<Resident> getResidentsReadOnly() {
        return Collections.unmodifiableList(residents);
    }

    public Optional<Resident> findResident(UUID id) {
        return residents.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public Island getIsland() {
        return island;
    }

    public Relationship getOrCreateRelationship(UUID x, UUID y) {
        RelationshipPair key = RelationshipPair.of(x, y);
        return relationships.computeIfAbsent(key, k -> new Relationship());
    }

    public Map<RelationshipPair, Relationship> getRelationships() {
        return relationships;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
        this.dayNightCycleMs = timeOfDay.ordinal() * (DAY_NIGHT_CYCLE_MS / 4);
    }

    public long getDayNightCycleMs() {
        return dayNightCycleMs;
    }

    /** Sets cycle phase and updates {@link #timeOfDay} to the matching quarter. */
    public void setDayNightCycleMs(long ms) {
        this.dayNightCycleMs = Math.floorMod(ms, DAY_NIGHT_CYCLE_MS);
        syncTimeOfDayFromCycle();
    }

    /** Advances the day–night cycle; updates {@link #timeOfDay} from phase. */
    public void advanceDayNightCycle(long deltaMs) {
        dayNightCycleMs = Math.floorMod(dayNightCycleMs + deltaMs, DAY_NIGHT_CYCLE_MS);
        syncTimeOfDayFromCycle();
    }

    private void syncTimeOfDayFromCycle() {
        int q = (int) Math.min(3, (dayNightCycleMs * 4) / DAY_NIGHT_CYCLE_MS);
        this.timeOfDay = TimeOfDay.values()[q];
    }

    public long getGameTick() {
        return gameTick;
    }

    public void setGameTick(long gameTick) {
        this.gameTick = gameTick;
    }

    public void incrementTick() {
        gameTick++;
    }

    public void clearAllResidentsAndRelationships() {
        residents.clear();
        island.clearResidents();
        relationships.clear();
    }

    public List<GameEvent> getActiveGlobalEvents() {
        return activeGlobalEvents;
    }

    public boolean isTutorialCompleted() {
        return tutorialCompleted;
    }

    public void setTutorialCompleted(boolean tutorialCompleted) {
        this.tutorialCompleted = tutorialCompleted;
    }

    public List<Resident> residentsInRoom(Room room) {
        return residents.stream().filter(r -> r.getCurrentRoom() == room).collect(Collectors.toList());
    }

    private Resident copyResidentShallow(Resident r) {
        return new Resident(r.getId(), r.getAppearance().copy(), r.getStatus().copy(),
                Inventory.copyOf(r.getInventory()), r.getCurrentRoom());
    }
}
