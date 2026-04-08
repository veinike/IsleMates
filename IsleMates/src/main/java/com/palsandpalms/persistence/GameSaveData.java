package com.palsandpalms.persistence;

import com.palsandpalms.model.GameEvent;
import com.palsandpalms.model.TimeOfDay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Serializable snapshot for JSON save (FA-45). */
public final class GameSaveData {
    private List<ResidentData> residents = new ArrayList<>();
    private Map<String, RelationshipData> relationships = new HashMap<>();
    private TimeOfDay timeOfDay = TimeOfDay.MORNING;
    /** Null in older saves: derive cycle from {@link #timeOfDay}. */
    private Long dayNightCycleMs;
    private long gameTick;
    private List<GameEvent> activeGlobalEvents = new ArrayList<>();
    private boolean tutorialCompleted;
    /** Food units in shared fridge. */
    private int fridgeFoodUnits;

    public List<ResidentData> getResidents() {
        return residents;
    }

    public void setResidents(List<ResidentData> residents) {
        this.residents = residents;
    }

    public Map<String, RelationshipData> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, RelationshipData> relationships) {
        this.relationships = relationships;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public Long getDayNightCycleMs() {
        return dayNightCycleMs;
    }

    public void setDayNightCycleMs(Long dayNightCycleMs) {
        this.dayNightCycleMs = dayNightCycleMs;
    }

    public long getGameTick() {
        return gameTick;
    }

    public void setGameTick(long gameTick) {
        this.gameTick = gameTick;
    }

    public List<GameEvent> getActiveGlobalEvents() {
        return activeGlobalEvents;
    }

    public void setActiveGlobalEvents(List<GameEvent> activeGlobalEvents) {
        this.activeGlobalEvents = activeGlobalEvents;
    }

    public boolean isTutorialCompleted() {
        return tutorialCompleted;
    }

    public void setTutorialCompleted(boolean tutorialCompleted) {
        this.tutorialCompleted = tutorialCompleted;
    }

    public int getFridgeFoodUnits() {
        return fridgeFoodUnits;
    }

    public void setFridgeFoodUnits(int fridgeFoodUnits) {
        this.fridgeFoodUnits = fridgeFoodUnits;
    }

    public static final class ResidentData {
        private String id;
        private String name;
        private String gender;
        private String hairColor;
        private String hairLength;
        private String eyeColor;
        private String skinTone;
        private double hunger;
        private double tiredness;
        private double mood;
        private double hygiene;
        private String currentRoom;
        private List<String> inventoryItemTypes = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getHairColor() {
            return hairColor;
        }

        public void setHairColor(String hairColor) {
            this.hairColor = hairColor;
        }

        public String getHairLength() {
            return hairLength;
        }

        public void setHairLength(String hairLength) {
            this.hairLength = hairLength;
        }

        public String getEyeColor() {
            return eyeColor;
        }

        public void setEyeColor(String eyeColor) {
            this.eyeColor = eyeColor;
        }

        public String getSkinTone() {
            return skinTone;
        }

        public void setSkinTone(String skinTone) {
            this.skinTone = skinTone;
        }

        public double getHunger() {
            return hunger;
        }

        public void setHunger(double hunger) {
            this.hunger = hunger;
        }

        public double getTiredness() {
            return tiredness;
        }

        public void setTiredness(double tiredness) {
            this.tiredness = tiredness;
        }

        public double getMood() {
            return mood;
        }

        public void setMood(double mood) {
            this.mood = mood;
        }

        public double getHygiene() {
            return hygiene;
        }

        public void setHygiene(double hygiene) {
            this.hygiene = hygiene;
        }

        public String getCurrentRoom() {
            return currentRoom;
        }

        public void setCurrentRoom(String currentRoom) {
            this.currentRoom = currentRoom;
        }

        public List<String> getInventoryItemTypes() {
            return inventoryItemTypes;
        }

        public void setInventoryItemTypes(List<String> inventoryItemTypes) {
            this.inventoryItemTypes = inventoryItemTypes;
        }
    }

    public static final class RelationshipData {
        private double value;
        private boolean friends;
        private boolean introduced;
        private boolean romanticFeelings;
        private boolean romanticRejected;
        private boolean romanticAccepted;
        private long avoidUntilTick;

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public boolean isFriends() {
            return friends;
        }

        public void setFriends(boolean friends) {
            this.friends = friends;
        }

        public boolean isIntroduced() {
            return introduced;
        }

        public void setIntroduced(boolean introduced) {
            this.introduced = introduced;
        }

        public boolean isRomanticFeelings() {
            return romanticFeelings;
        }

        public void setRomanticFeelings(boolean romanticFeelings) {
            this.romanticFeelings = romanticFeelings;
        }

        public boolean isRomanticRejected() {
            return romanticRejected;
        }

        public void setRomanticRejected(boolean romanticRejected) {
            this.romanticRejected = romanticRejected;
        }

        public boolean isRomanticAccepted() {
            return romanticAccepted;
        }

        public void setRomanticAccepted(boolean romanticAccepted) {
            this.romanticAccepted = romanticAccepted;
        }

        public long getAvoidUntilTick() {
            return avoidUntilTick;
        }

        public void setAvoidUntilTick(long avoidUntilTick) {
            this.avoidUntilTick = avoidUntilTick;
        }
    }
}
