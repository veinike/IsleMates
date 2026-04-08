package com.palsandpalms.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.palsandpalms.engine.GameState;
import com.palsandpalms.model.EyeColor;
import com.palsandpalms.model.Gender;
import com.palsandpalms.model.HairColor;
import com.palsandpalms.model.HairLength;
import com.palsandpalms.model.Inventory;
import com.palsandpalms.model.Item;
import com.palsandpalms.model.ItemType;
import com.palsandpalms.model.Relationship;
import com.palsandpalms.model.RelationshipPair;
import com.palsandpalms.model.Resident;
import com.palsandpalms.model.ResidentAppearance;
import com.palsandpalms.model.Room;
import com.palsandpalms.model.SkinTone;
import com.palsandpalms.model.StatusValues;
import com.palsandpalms.world.Fridge;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SaveManager {
    private static final String SAVE_FILE = "game_save.json";

    private final Path savePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public SaveManager(Path baseDir) {
        this.savePath = baseDir.resolve(SAVE_FILE);
    }

    public Path getSavePath() {
        return savePath;
    }

    public boolean saveExists() {
        return Files.isRegularFile(savePath);
    }

    public void save(GameState state, Fridge fridge) throws IOException {
        GameSaveData data = toSaveData(state, fridge);
        Files.writeString(savePath, gson.toJson(data), StandardCharsets.UTF_8);
    }

    public GameSaveData loadRaw() throws IOException {
        if (!saveExists()) {
            return null;
        }
        String json = Files.readString(savePath, StandardCharsets.UTF_8);
        return gson.fromJson(json, GameSaveData.class);
    }

    public void loadInto(GameState state, Fridge fridge) throws IOException {
        GameSaveData data = loadRaw();
        if (data == null) {
            return;
        }
        apply(data, state, fridge);
    }

    public static void apply(GameSaveData data, GameState state, Fridge fridge) {
        state.getRwLock().writeLock().lock();
        try {
            state.clearAllResidentsAndRelationships();
            state.getActiveGlobalEvents().clear();
            List<GameSaveData.ResidentData> resList = data.getResidents();
            if (resList != null) {
                for (GameSaveData.ResidentData rd : resList) {
                    state.addResident(fromResidentData(rd));
                }
            }
            Map<String, GameSaveData.RelationshipData> rels = data.getRelationships();
            if (rels != null) {
                for (Map.Entry<String, GameSaveData.RelationshipData> e : rels.entrySet()) {
                    String[] parts = e.getKey().split("_");
                    UUID a = UUID.fromString(parts[0]);
                    UUID b = UUID.fromString(parts[1]);
                    RelationshipPair pair = RelationshipPair.of(a, b);
                    GameSaveData.RelationshipData rd = e.getValue();
                    state.getRelationships().put(pair,
                            new Relationship(rd.getValue(), rd.isFriends(), rd.getAvoidUntilTick()));
                }
            }
            if (data.getDayNightCycleMs() != null) {
                state.setDayNightCycleMs(data.getDayNightCycleMs());
            } else {
                state.setTimeOfDay(data.getTimeOfDay());
            }
            state.setGameTick(data.getGameTick());
            if (data.getActiveGlobalEvents() != null) {
                state.getActiveGlobalEvents().addAll(data.getActiveGlobalEvents());
            }
            state.setTutorialCompleted(data.isTutorialCompleted());
        } finally {
            state.getRwLock().writeLock().unlock();
        }
        fridge.setFoodUnits(data.getFridgeFoodUnits());
    }

    private static GameSaveData toSaveData(GameState state, Fridge fridge) {
        GameSaveData data = new GameSaveData();
        state.getRwLock().readLock().lock();
        try {
            for (Resident r : state.getResidentsReadOnly()) {
                data.getResidents().add(toResidentData(r));
            }
            for (Map.Entry<RelationshipPair, Relationship> e : state.getRelationships().entrySet()) {
                String key = e.getKey().a() + "_" + e.getKey().b();
                Relationship rel = e.getValue();
                GameSaveData.RelationshipData rd = new GameSaveData.RelationshipData();
                rd.setValue(rel.getValue());
                rd.setFriends(rel.isFriends());
                rd.setAvoidUntilTick(rel.getAvoidUntilTick());
                data.getRelationships().put(key, rd);
            }
            data.setTimeOfDay(state.getTimeOfDay());
            data.setDayNightCycleMs(state.getDayNightCycleMs());
            data.setGameTick(state.getGameTick());
            data.setActiveGlobalEvents(new ArrayList<>(state.getActiveGlobalEvents()));
            data.setTutorialCompleted(state.isTutorialCompleted());
        } finally {
            state.getRwLock().readLock().unlock();
        }
        data.setFridgeFoodUnits(fridge.getStock());
        return data;
    }

    private static GameSaveData.ResidentData toResidentData(Resident r) {
        GameSaveData.ResidentData d = new GameSaveData.ResidentData();
        d.setId(r.getId().toString());
        d.setName(r.getAppearance().getName());
        d.setGender(r.getAppearance().getGender().name());
        d.setHairColor(r.getAppearance().getHairColor().name());
        d.setHairLength(r.getAppearance().getHairLength().name());
        d.setEyeColor(r.getAppearance().getEyeColor().name());
        d.setSkinTone(r.getAppearance().getSkinTone().name());
        d.setHunger(r.getStatus().getHunger());
        d.setTiredness(r.getStatus().getTiredness());
        d.setMood(r.getStatus().getMood());
        d.setHygiene(r.getStatus().getHygiene());
        d.setCurrentRoom(r.getCurrentRoom().name());
        List<String> types = new ArrayList<>();
        for (Item it : r.getInventory().getItems()) {
            types.add(it.getType().name());
        }
        d.setInventoryItemTypes(types);
        return d;
    }

    private static Resident fromResidentData(GameSaveData.ResidentData d) {
        UUID id = UUID.fromString(d.getId());
        ResidentAppearance app = new ResidentAppearance(
                d.getName(),
                Gender.valueOf(d.getGender()),
                HairColor.valueOf(d.getHairColor()),
                HairLength.valueOf(d.getHairLength()),
                EyeColor.valueOf(d.getEyeColor()),
                SkinTone.valueOf(d.getSkinTone())
        );
        StatusValues st = new StatusValues(d.getHunger(), d.getTiredness(), d.getMood(), d.getHygiene());
        Inventory inv = new Inventory();
        if (d.getInventoryItemTypes() != null) {
            for (String t : d.getInventoryItemTypes()) {
                inv.add(new Item(ItemType.valueOf(t)));
            }
        }
        Room room = parseSavedRoom(d.getCurrentRoom());
        return new Resident(id, app, st, inv, room);
    }

    /** Loads legacy saves that used removed rooms (e.g. {@code MALL}) or bad values. */
    private static Room parseSavedRoom(String name) {
        if (name == null || name.isBlank()) {
            return Room.PARK;
        }
        try {
            return Room.valueOf(name);
        } catch (IllegalArgumentException e) {
            if ("MALL".equals(name)) {
                return Room.APARTMENT;
            }
            return Room.PARK;
        }
    }
}
