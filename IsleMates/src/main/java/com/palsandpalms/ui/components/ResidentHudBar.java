package com.palsandpalms.ui.components;

import com.palsandpalms.engine.GameState;
import com.palsandpalms.model.Resident;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public final class ResidentHudBar extends HBox {

    private final DayNightCycleDisplay dayNight = new DayNightCycleDisplay();
    private final ResidentHudTab[] tabs = new ResidentHudTab[4];

    public ResidentHudBar(BiConsumer<Resident, MouseEvent> onHudTabClick) {
        super(8);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(6, 10, 6, 10));
        setStyle("-fx-background-color: rgba(0,0,0,0.35); -fx-background-radius: 10;");
        setMaxWidth(Region.USE_PREF_SIZE);
        setMinHeight(100);
        setMaxHeight(Region.USE_PREF_SIZE);
        getChildren().add(dayNight);
        for (int i = 0; i < 4; i++) {
            ResidentHudTab tab = new ResidentHudTab(i);
            int slot = i;
            tab.setOnMouseClicked(e -> {
                Resident r = tab.getResident();
                if (r != null) {
                    onHudTabClick.accept(r, e);
                }
            });
            tabs[i] = tab;
            getChildren().add(tab);
        }
    }

    public void update(GameState state, Map<UUID, AnimatedResident> animById) {
        state.getRwLock().readLock().lock();
        try {
            List<Resident> list = state.getResidentsReadOnly();
            dayNight.setCycleMs(state.getDayNightCycleMs());
            for (int i = 0; i < 4; i++) {
                if (i < list.size()) {
                    Resident r = list.get(i);
                    tabs[i].setVisible(true);
                    tabs[i].bindResident(r, animById.get(r.getId()));
                } else {
                    tabs[i].setVisible(false);
                }
            }
        } finally {
            state.getRwLock().readLock().unlock();
        }
    }
}
