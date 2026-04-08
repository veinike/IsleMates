package com.palsandpalms.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Island {
    public static final int MAX_RESIDENTS = 4;

    private final List<UUID> residentIds = new ArrayList<>();

    public boolean canAddResident() {
        return residentIds.size() < MAX_RESIDENTS;
    }

    public void addResident(UUID id) {
        Objects.requireNonNull(id);
        if (!canAddResident()) {
            throw new IllegalStateException("Island is full (max " + MAX_RESIDENTS + " residents)");
        }
        if (residentIds.contains(id)) {
            return;
        }
        residentIds.add(id);
    }

    public void removeResident(UUID id) {
        residentIds.remove(id);
    }

    public List<UUID> getResidentIds() {
        return Collections.unmodifiableList(residentIds);
    }

    public int getResidentCount() {
        return residentIds.size();
    }

    public void clearResidents() {
        residentIds.clear();
    }
}
