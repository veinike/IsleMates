package com.palsandpalms.model;

/** FA-33 predefined residents. */
public final class StarterResidents {
    private StarterResidents() {
    }

    public static Resident createNina() {
        ResidentAppearance app = new ResidentAppearance(
                "Nina",
                Gender.FEMALE,
                HairColor.BLONDE,
                HairLength.SHORT,
                EyeColor.BLUE,
                SkinTone.LIGHT
        );
        return new Resident(app, new StatusValues(), Room.PARK);
    }

    public static Resident createVictoria() {
        ResidentAppearance app = new ResidentAppearance(
                "Victoria",
                Gender.FEMALE,
                HairColor.BLACK,
                HairLength.SHORT,
                EyeColor.BLUE,
                SkinTone.LIGHT
        );
        return new Resident(app, new StatusValues(), Room.PARK);
    }
}
