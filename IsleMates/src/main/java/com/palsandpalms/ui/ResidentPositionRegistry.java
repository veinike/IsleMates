package com.palsandpalms.ui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry of each resident's normalized X position (0–1) in their current view.
 * Updated by the UI each animation frame, read by {@link com.palsandpalms.engine.ResidentAI}
 * to enforce position-based room-exit rules.
 */
public final class ResidentPositionRegistry {

    private static final Map<UUID, Double> NORM_X = new ConcurrentHashMap<>();

    private ResidentPositionRegistry() {
    }

    public static void update(UUID residentId, double normX) {
        if (residentId != null) {
            NORM_X.put(residentId, normX);
        }
    }

    public static double getNormX(UUID residentId) {
        return residentId != null ? NORM_X.getOrDefault(residentId, 0.5) : 0.5;
    }

    public static void remove(UUID residentId) {
        if (residentId != null) {
            NORM_X.remove(residentId);
        }
    }
}
