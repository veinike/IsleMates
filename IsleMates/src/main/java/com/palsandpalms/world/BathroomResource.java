package com.palsandpalms.world;

import com.palsandpalms.model.Resident;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Semaphore;

/** Mutex-protected bathroom: only one resident at a time (NFA-07). Uses {@link Semaphore} so the same thread cannot re-enter. */
public final class BathroomResource {
    private final Semaphore permit = new Semaphore(1);
    private volatile Resident occupant;

    public boolean tryEnter(Resident resident) {
        Objects.requireNonNull(resident);
        if (!permit.tryAcquire()) {
            return false;
        }
        occupant = resident;
        return true;
    }

    public void leave(Resident resident) {
        Objects.requireNonNull(resident);
        if (occupant != null && occupant.equals(resident)) {
            occupant = null;
            permit.release();
        }
    }

    public Optional<Resident> getOccupant() {
        return Optional.ofNullable(occupant);
    }
}
