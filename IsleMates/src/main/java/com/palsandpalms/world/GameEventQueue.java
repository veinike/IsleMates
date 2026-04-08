package com.palsandpalms.world;

import com.palsandpalms.model.GameEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/** Producer-consumer queue for global events (NFA-09). */
public final class GameEventQueue {
    private final BlockingQueue<GameEvent> queue = new LinkedBlockingQueue<>();

    public void publish(GameEvent event) throws InterruptedException {
        queue.put(event);
    }

    public boolean offer(GameEvent event) {
        return queue.offer(event);
    }

    public GameEvent poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    public GameEvent poll() {
        return queue.poll();
    }

    public BlockingQueue<GameEvent> getQueue() {
        return queue;
    }
}
