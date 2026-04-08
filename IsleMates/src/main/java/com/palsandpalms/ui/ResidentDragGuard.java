package com.palsandpalms.ui;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which resident sprite is actively being dragged so background logic (e.g. {@link com.palsandpalms.engine.ResidentAI})
 * does not change their room until the gesture ends.
 */
public final class ResidentDragGuard {

    private static final Set<UUID> DRAGGING = ConcurrentHashMap.newKeySet();

    private ResidentDragGuard() {
    }

    public static void beginDrag(UUID residentId) {
        if (residentId != null) {
            DRAGGING.add(residentId);
        }
    }

    public static void endDrag(UUID residentId) {
        if (residentId != null) {
            DRAGGING.remove(residentId);
        }
    }

    public static boolean isDragged(UUID residentId) {
        return residentId != null && DRAGGING.contains(residentId);
    }
}
