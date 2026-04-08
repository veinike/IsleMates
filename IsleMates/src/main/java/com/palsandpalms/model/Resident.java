package com.palsandpalms.model;

import java.util.Objects;
import java.util.UUID;

public final class Resident {
    private final UUID id;
    private ResidentAppearance appearance;
    private StatusValues status;
    private final Inventory inventory;
    private Room currentRoom;

    public Resident(ResidentAppearance appearance, StatusValues status, Room startRoom) {
        this(UUID.randomUUID(), appearance, status, new Inventory(), startRoom);
    }

    public Resident(UUID id, ResidentAppearance appearance, StatusValues status,
                    Inventory inventory, Room currentRoom) {
        this.id = Objects.requireNonNull(id);
        this.appearance = Objects.requireNonNull(appearance);
        this.status = Objects.requireNonNull(status);
        this.inventory = Objects.requireNonNull(inventory);
        this.currentRoom = Objects.requireNonNull(currentRoom);
    }

    public UUID getId() {
        return id;
    }

    public ResidentAppearance getAppearance() {
        return appearance;
    }

    public void setAppearance(ResidentAppearance appearance) {
        this.appearance = Objects.requireNonNull(appearance);
    }

    public StatusValues getStatus() {
        return status;
    }

    public void setStatus(StatusValues status) {
        this.status = Objects.requireNonNull(status);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = Objects.requireNonNull(currentRoom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resident resident = (Resident) o;
        return id.equals(resident.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
