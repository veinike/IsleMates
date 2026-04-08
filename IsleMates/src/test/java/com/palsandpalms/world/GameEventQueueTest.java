package com.palsandpalms.world;

import com.palsandpalms.model.GameEvent;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class GameEventQueueTest {

    @Test
    void fifoOfferPoll() {
        GameEventQueue q = new GameEventQueue();
        assertTrue(q.offer(GameEvent.CONCERT));
        assertTrue(q.offer(GameEvent.BIRTHDAY));
        assertEquals(GameEvent.CONCERT, q.poll());
        assertEquals(GameEvent.BIRTHDAY, q.poll());
    }

    @Test
    void consumeHundredFromProducer() throws Exception {
        GameEventQueue q = new GameEventQueue();
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                q.offer(GameEvent.DELIVERY);
            }
        });
        AtomicInteger n = new AtomicInteger();
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    GameEvent e = q.poll(5, java.util.concurrent.TimeUnit.SECONDS);
                    if (e != null) {
                        n.incrementAndGet();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        producer.start();
        producer.join(10_000);
        consumer.join(10_000);
        assertEquals(100, n.get());
    }
}
