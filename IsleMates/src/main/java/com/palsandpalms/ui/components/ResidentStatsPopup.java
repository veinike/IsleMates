package com.palsandpalms.ui.components;

import com.palsandpalms.model.Resident;
import com.palsandpalms.model.StatusValues;
import com.palsandpalms.ui.AppFonts;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/** Small floating panel: name, sprite, hunger/tired/mood/hygiene bars. */
public final class ResidentStatsPopup {

    private ResidentStatsPopup() {
    }

    public static void show(StackPane host, Resident resident, double anchorSceneX, double anchorSceneY) {
        dismiss(host);

        StackPane dim = new StackPane();
        dim.setMinSize(0, 0);
        dim.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        dim.prefWidthProperty().bind(host.widthProperty());
        dim.prefHeightProperty().bind(host.heightProperty());
        Rectangle hit = new Rectangle();
        hit.widthProperty().bind(host.widthProperty());
        hit.heightProperty().bind(host.heightProperty());
        hit.setFill(Color.color(0, 0, 0, 0.01));
        hit.setOnMouseClicked(e -> dismiss(host));
        dim.getChildren().add(hit);

        CharacterSpriteSheet sheet = CharacterSpriteSheet.forAppearance(resident.getAppearance());
        ImageView portrait = new ImageView(sheet.standingForMood(resident.getStatus().getMood()));
        portrait.setPreserveRatio(true);
        portrait.setFitHeight(96);

        Label title = new Label(resident.getAppearance().getName());
        title.setFont(AppFonts.uiFont(18));
        title.setTextFill(Color.WHITE);

        VBox stats = new VBox(6);
        double fatigue = resident.getStatus().getTiredness();
        double sleepBar = StatusValues.clamp(100.0 - fatigue);
        stats.getChildren().addAll(
                statRow("Hunger", resident.getStatus().getHunger()),
                statRow("Schlaf", sleepBar),
                statRow("Stimmung", resident.getStatus().getMood()),
                statRow("Hygiene", resident.getStatus().getHygiene()));

        VBox panel = new VBox(10, title, portrait, stats);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPadding(new Insets(14));
        panel.setStyle("-fx-background-color: rgba(30,30,45,0.92); -fx-background-radius: 12;");
        panel.setMaxWidth(260);
        panel.setMinHeight(300);
        panel.setMaxHeight(300);
        panel.setPrefHeight(300);
        panel.setOnMouseClicked(e -> e.consume());

        StackPane.setAlignment(panel, Pos.BOTTOM_LEFT);
        Point2D local = host.sceneToLocal(anchorSceneX, anchorSceneY);
        if (local == null) {
            local = new Point2D(16, 16);
        }
        double hostH = host.getHeight();
        double bottomInset = Math.max(8, hostH - local.getY());
        StackPane.setMargin(panel, new Insets(0, 0, bottomInset, Math.max(8, local.getX())));

        dim.getChildren().add(panel);
        host.getChildren().add(dim);
        host.getProperties().put("residentStatsPopup", dim);
    }

    private static VBox statRow(String label, double value0to100) {
        Label l = new Label(label);
        l.setFont(AppFonts.uiFont(11));
        l.setTextFill(Color.LIGHTGRAY);
        ProgressBar bar = new ProgressBar(value0to100 / 100.0);
        bar.setPrefWidth(200);
        VBox row = new VBox(2, l, bar);
        return row;
    }

    public static void dismiss(StackPane host) {
        Object o = host.getProperties().remove("residentStatsPopup");
        if (o instanceof StackPane sp) {
            host.getChildren().remove(sp);
        }
    }
}
