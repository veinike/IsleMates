package com.palsandpalms.model;

public enum TimeOfDay {
    MORNING,
    NOON,
    EVENING,
    NIGHT;

    public TimeOfDay next() {
        return switch (this) {
            case MORNING -> NOON;
            case NOON -> EVENING;
            case EVENING -> NIGHT;
            case NIGHT -> MORNING;
        };
    }
}
