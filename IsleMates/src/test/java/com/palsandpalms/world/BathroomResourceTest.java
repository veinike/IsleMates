package com.palsandpalms.world;

import com.palsandpalms.model.Resident;
import com.palsandpalms.model.ResidentAppearance;
import com.palsandpalms.model.Gender;
import com.palsandpalms.model.HairColor;
import com.palsandpalms.model.HairLength;
import com.palsandpalms.model.EyeColor;
import com.palsandpalms.model.SkinTone;
import com.palsandpalms.model.Room;
import com.palsandpalms.model.StatusValues;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BathroomResourceTest {

    private static Resident r(String name) {
        return new Resident(
                new ResidentAppearance(name, Gender.FEMALE, HairColor.BLONDE, HairLength.SHORT, EyeColor.BLUE, SkinTone.LIGHT),
                new StatusValues(), Room.BATHROOM);
    }

    @Test
    void singleOccupant() {
        BathroomResource b = new BathroomResource();
        Resident a = r("A");
        assertTrue(b.tryEnter(a));
        assertFalse(b.tryEnter(r("B")));
        b.leave(a);
        assertTrue(b.tryEnter(r("B")));
    }

    @Test
    void concurrentAtMostOne() throws Exception {
        BathroomResource b = new BathroomResource();
        AtomicInteger inside = new AtomicInteger();
        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch done = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            final int idx = i;
            pool.submit(() -> {
                Resident res = r("R" + idx);
                if (b.tryEnter(res)) {
                    try {
                    int cur = inside.incrementAndGet();
                    assertTrue(cur <= 1);
                    Thread.sleep(10);
                    inside.decrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        b.leave(res);
                    }
                }
                done.countDown();
            });
        }
        assertTrue(done.await(30, TimeUnit.SECONDS));
        pool.shutdown();
    }
}
