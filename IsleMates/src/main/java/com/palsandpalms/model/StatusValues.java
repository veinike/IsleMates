package com.palsandpalms.model;

/** Hunger, tiredness, mood, hygiene in range [0, 100]. */
public final class StatusValues {
    private double hunger;
    private double tiredness;
    private double mood;
    private double hygiene;

    public static final double MIN = 0;
    public static final double MAX = 100;

    public StatusValues() {
        this(80, 10, 80, 80);
    }

    public StatusValues(double hunger, double tiredness, double mood, double hygiene) {
        this.hunger = clamp(hunger);
        this.tiredness = clamp(tiredness);
        this.mood = clamp(mood);
        this.hygiene = clamp(hygiene);
    }

    public static double clamp(double v) {
        return Math.max(MIN, Math.min(MAX, v));
    }

    public void tick(double hungerDelta, double tirednessDelta, double moodDelta, double hygieneDelta) {
        hunger = clamp(hunger + hungerDelta);
        tiredness = clamp(tiredness + tirednessDelta);
        mood = clamp(mood + moodDelta);
        hygiene = clamp(hygiene + hygieneDelta);
    }

    public double getHunger() {
        return hunger;
    }

    public void setHunger(double hunger) {
        this.hunger = clamp(hunger);
    }

    public double getTiredness() {
        return tiredness;
    }

    public void setTiredness(double tiredness) {
        this.tiredness = clamp(tiredness);
    }

    public double getMood() {
        return mood;
    }

    public void setMood(double mood) {
        this.mood = clamp(mood);
    }

    public double getHygiene() {
        return hygiene;
    }

    public void setHygiene(double hygiene) {
        this.hygiene = clamp(hygiene);
    }

    public StatusValues copy() {
        return new StatusValues(hunger, tiredness, mood, hygiene);
    }
}
