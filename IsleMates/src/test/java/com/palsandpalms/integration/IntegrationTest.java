package com.palsandpalms.integration;

import com.palsandpalms.engine.GameState;
import com.palsandpalms.model.StarterResidents;
import com.palsandpalms.model.RelationshipPair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    @Test
    void ninaVictoriaStart() {
        GameState state = new GameState();
        state.getRwLock().writeLock().lock();
        try {
            var n = StarterResidents.createNina();
            var v = StarterResidents.createVictoria();
            state.addResident(n);
            state.addResident(v);
            RelationshipPair p = RelationshipPair.of(n.getId(), v.getId());
            state.getRelationships().put(p, new com.palsandpalms.model.Relationship());
            assertEquals(0, state.getOrCreateRelationship(n.getId(), v.getId()).getValue());
        } finally {
            state.getRwLock().writeLock().unlock();
        }
    }
}
