package com.palsandpalms.ui;

import com.palsandpalms.model.RelationshipPair;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which resident pairs have already been introduced to each other.
 * {@link com.palsandpalms.ui.components.ConversationOverlay} reads this to decide whether
 * to play an intro script (first meeting) or a regular dialog script.
 */
public final class IntroductionRegistry {

    private static final Set<RelationshipPair> INTRODUCED = ConcurrentHashMap.newKeySet();

    private IntroductionRegistry() {
    }

    public static boolean hasBeenIntroduced(UUID a, UUID b) {
        return INTRODUCED.contains(RelationshipPair.of(a, b));
    }

    public static void markIntroduced(UUID a, UUID b) {
        INTRODUCED.add(RelationshipPair.of(a, b));
    }

    public static void clear() {
        INTRODUCED.clear();
    }
}
