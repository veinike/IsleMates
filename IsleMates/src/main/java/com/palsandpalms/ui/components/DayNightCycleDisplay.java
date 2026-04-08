package com.palsandpalms.ui.components;

import com.palsandpalms.engine.GameState;
import com.palsandpalms.ui.AppFonts;
import com.palsandpalms.ui.AssetLoader;
import com.palsandpalms.ui.GameAssets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Semicircle dial (sun/moon) flush left, 12h clock text beside it; placed first in {@link ResidentHudBar}.
 * Screen inset is applied via {@link javafx.scene.layout.StackPane#setMargin} on the HUD wrapper.
 * One full rotation = {@link GameState#DAY_NIGHT_CYCLE_MS}; label shows hour only (12 AM … 11 PM).
 */
public final class DayNightCycleDisplay extends HBox {

    private static final double MENU_HEIGHT = 100;
    /** Visible semicircle height (25% smaller than full bar height); diameter = 2× this. */
    private static final double HALF_H = 75;
    private static final double DIAM = 2 * HALF_H;

    private final ImageView wheel;
    private final Label timeLabel;
    /** 0–23, or -1 until first paint. */
    private int lastShownH24 = -1;

    public DayNightCycleDisplay() {
        super(10);
        setAlignment(Pos.CENTER_LEFT);
        setMinHeight(MENU_HEIGHT);
        setPrefHeight(MENU_HEIGHT);
        setMaxHeight(MENU_HEIGHT);
        setMaxWidth(Region.USE_PREF_SIZE);

        StackPane dial = new StackPane();
        dial.setMinSize(DIAM, HALF_H);
        dial.setPrefSize(DIAM, HALF_H);
        dial.setMaxSize(DIAM, HALF_H);
        Rectangle clipRect = new Rectangle(DIAM, HALF_H);
        dial.setClip(clipRect);

        wheel = new ImageView(AssetLoader.loadImageNatural(GameAssets.DAY_NIGHT_CYCLE));
        wheel.setPreserveRatio(true);
        wheel.setFitWidth(DIAM);
        wheel.setFitHeight(DIAM);
        dial.getChildren().add(wheel);
        StackPane.setAlignment(wheel, Pos.TOP_LEFT);

        timeLabel = new Label();
        timeLabel.setFont(AppFonts.uiFont(20));
        timeLabel.setTextFill(Color.WHITE);

        getChildren().addAll(dial, timeLabel);
        setCycleMs(0);
    }

    public void setCycleMs(long cycleMs) {
        long wrapped = Math.floorMod(cycleMs, GameState.DAY_NIGHT_CYCLE_MS);
        double angle = 360.0 * wrapped / GameState.DAY_NIGHT_CYCLE_MS;
        wheel.setRotate(angle);
        int h24 = virtualHour24(wrapped);
        if (h24 != lastShownH24) {
            lastShownH24 = h24;
            timeLabel.setText(formatHour12FromH24(h24));
        }
    }

    static int virtualHour24(long cycleMsWrapped) {
        double fraction = cycleMsWrapped / (double) GameState.DAY_NIGHT_CYCLE_MS;
        int minuteOfDay = (int) Math.floor(fraction * 24 * 60);
        if (minuteOfDay >= 24 * 60) {
            minuteOfDay = 24 * 60 - 1;
        }
        return minuteOfDay / 60;
    }

    static String formatHour12FromH24(int h24) {
        boolean pm = h24 >= 12;
        int h12 = h24 % 12;
        if (h12 == 0) {
            h12 = 12;
        }
        return h12 + (pm ? " PM" : " AM");
    }
}
