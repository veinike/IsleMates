package com.palsandpalms.engine;

import com.palsandpalms.model.InteractionType;
import com.palsandpalms.model.Item;
import com.palsandpalms.model.Relationship;
import com.palsandpalms.model.RelationshipPair;
import com.palsandpalms.model.Resident;
import com.palsandpalms.model.Room;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class InteractionManager {
    private static final double TALK_DELTA = 5;
    private static final double ARGUE_DELTA = -15;
    private static final double GIFT_DELTA = 10;
    private static final long AVOID_TICKS = 50;

    private final GameState state;
    private final Random random = new Random();

    public InteractionManager(GameState state) {
        this.state = state;
    }

    public void tickPossibleInteractions() {
        state.getRwLock().writeLock().lock();
        try {
            for (Room room : Room.values()) {
                List<Resident> list = state.residentsInRoom(room);
                if (list.size() < 2) {
                    continue;
                }
                for (int i = 0; i < list.size(); i++) {
                    for (int j = i + 1; j < list.size(); j++) {
                        Resident a = list.get(i);
                        Resident b = list.get(j);
                        if (random.nextDouble() < 0.05) {
                            InteractionType type = randomInteraction();
                            applyInteraction(a.getId(), b.getId(), type, null);
                        }
                    }
                }
            }
        } finally {
            state.getRwLock().writeLock().unlock();
        }
    }

    public void applyInteraction(UUID aId, UUID bId, InteractionType type, Item giftItem) {
        state.getRwLock().writeLock().lock();
        try {
            Relationship rel = state.getOrCreateRelationship(aId, bId);
            rel.getLock().lock();
            try {
                if (state.getGameTick() < rel.getAvoidUntilTick()) {
                    return;
                }
                switch (type) {
                    case TALK -> rel.addValue(TALK_DELTA);
                    case ARGUE -> {
                        rel.addValue(ARGUE_DELTA);
                        rel.setAvoidUntilTick(state.getGameTick() + AVOID_TICKS);
                    }
                    case GIVE_GIFT -> {
                        if (giftItem != null) {
                            rel.addValue(GIFT_DELTA);
                        }
                    }
                }
            } finally {
                rel.getLock().unlock();
            }
        } finally {
            state.getRwLock().writeLock().unlock();
        }
    }

    private InteractionType randomInteraction() {
        return InteractionType.values()[random.nextInt(InteractionType.values().length)];
    }
}
