package com.palsandpalms.world;

import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class FridgeTest {

    @Test
    void takeUntilEmpty() {
        Fridge f = new Fridge(3);
        assertNotNull(f.takeFood());
        assertNotNull(f.takeFood());
        assertNotNull(f.takeFood());
        assertNull(f.takeFood());
    }

    @Test
    void concurrentTakes() throws Exception {
        Fridge f = new Fridge(10);
        AtomicInteger got = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            pool.submit(() -> {
                if (f.takeFood() != null) {
                    got.incrementAndGet();
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
        assertEquals(10, got.get());
        assertEquals(0, f.getStock());
    }
}
