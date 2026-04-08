package com.palsandpalms.model;

import java.util.concurrent.locks.ReentrantLock;

/** Relationship value between two residents; guarded by a ReentrantLock for writes (NFA-08). */
public final class Relationship {
    public static final double FRIENDSHIP_THRESHOLD = 70;

    private final ReentrantLock lock = new ReentrantLock();
    private double value;
    private boolean friends;
    /** Game tick until which resident A avoids B interaction (simplified: one direction). */
    private long avoidUntilTick;

    public Relationship() {
        this(0, false, 0);
    }

    public Relationship(double value, boolean friends, long avoidUntilTick) {
        this.value = StatusValues.clamp(value);
        this.friends = friends;
        this.avoidUntilTick = avoidUntilTick;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = StatusValues.clamp(value);
        this.friends = this.value >= FRIENDSHIP_THRESHOLD;
    }

    public boolean isFriends() {
        return friends;
    }

    public long getAvoidUntilTick() {
        return avoidUntilTick;
    }

    public void setAvoidUntilTick(long avoidUntilTick) {
        this.avoidUntilTick = avoidUntilTick;
    }

    public void addValue(double delta) {
        setValue(value + delta);
    }
}
