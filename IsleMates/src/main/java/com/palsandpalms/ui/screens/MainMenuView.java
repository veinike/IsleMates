package com.palsandpalms.ui.screens;

import com.palsandpalms.ui.AssetLoader;
import com.palsandpalms.ui.GameAssets;
import com.palsandpalms.ui.GameCursors;
import com.palsandpalms.ui.GameViewport;
import com.palsandpalms.ui.SpriteButton;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public final class MainMenuView {

    private MainMenuView() {
    }

    public static StackPane create(
            Runnable onContinue,
            Runnable onStartNew,
            Runnable onExit,
            Runnable onRules) {
        StackPane root = new StackPane();
        root.setMinSize(GameViewport.MIN_WIDTH, GameViewport.MIN_HEIGHT);

        ImageView bg = new ImageView(AssetLoader.loadImageNatural(GameAssets.MAIN_MENU_BACKGROUND));
        bg.fitWidthProperty().bind(root.widthProperty());
        bg.fitHeightProperty().bind(root.heightProperty());
        bg.setPreserveRatio(false);
        root.getChildren().add(bg);

        HBox buttonBar = new HBox(20);
        buttonBar.setAlignment(Pos.BOTTOM_CENTER);
        StackPane.setAlignment(buttonBar, Pos.BOTTOM_CENTER);
        StackPane.setMargin(buttonBar, new Insets(0, 0, 32, 0));

        var btnW = Bindings.createDoubleBinding(
                () -> Math.max(48, root.getWidth() * 0.20),
                root.widthProperty());
        var btnH = Bindings.createDoubleBinding(
                () -> Math.max(24, root.getHeight() * 0.08 + 10),
                root.heightProperty());

        ImageView btnContinue = new ImageView(AssetLoader.loadImageNatural(GameAssets.MAIN_MENU_START_CONTINUE));
        btnContinue.setPreserveRatio(false);
        btnContinue.fitWidthProperty().bind(btnW);
        btnContinue.fitHeightProperty().bind(btnH);
        SpriteButton.styleAsButton(btnContinue);
        btnContinue.setOnMouseClicked(e -> onContinue.run());

        ImageView btnNew = new ImageView(AssetLoader.loadImageNatural(GameAssets.MAIN_MENU_START_NEW));
        btnNew.setPreserveRatio(false);
        btnNew.fitWidthProperty().bind(btnW);
        btnNew.fitHeightProperty().bind(btnH);
        SpriteButton.styleAsButton(btnNew);
        btnNew.setOnMouseClicked(e -> onStartNew.run());

        ImageView btnExit = new ImageView(AssetLoader.loadImageNatural(GameAssets.MAIN_MENU_EXIT));
        btnExit.setPreserveRatio(false);
        btnExit.fitWidthProperty().bind(btnW);
        btnExit.fitHeightProperty().bind(btnH);
        SpriteButton.styleAsButton(btnExit);
        btnExit.setOnMouseClicked(e -> onExit.run());

        buttonBar.getChildren().addAll(btnContinue, btnNew, btnExit);
        root.getChildren().add(buttonBar);

        Label rules = new Label("Regeln lesen");
        rules.setTextFill(Color.WHITE);
        rules.setStyle("-fx-font-size: 14px; -fx-underline: true;");
        rules.setCursor(GameCursors.getHover());
        rules.setOnMouseClicked(e -> onRules.run());
        StackPane.setAlignment(rules, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(rules, new javafx.geometry.Insets(0, 24, 24, 0));
        root.getChildren().add(rules);

        return root;
    }
}
