package com.palsandpalms.ui.screens;

import com.palsandpalms.ui.AssetLoader;
import com.palsandpalms.ui.GameAssets;
import com.palsandpalms.ui.GameViewport;
import com.palsandpalms.ui.SpriteButton;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class GameMenuOverlay {

    private GameMenuOverlay() {
    }

    public static StackPane create(
            Runnable onCreateResident,
            Runnable onSave,
            Runnable onBackHome,
            Runnable onEndGame,
            Runnable onClose) {
        StackPane overlay = new StackPane();
        overlay.setMinSize(GameViewport.MIN_WIDTH, GameViewport.MIN_HEIGHT);

        Rectangle dim = new Rectangle();
        dim.widthProperty().bind(overlay.widthProperty());
        dim.heightProperty().bind(overlay.heightProperty());
        dim.setFill(Color.color(0, 0, 0, 0.55));
        overlay.getChildren().add(dim);

        StackPane menuStack = new StackPane();
        var menuW = Bindings.min(overlay.widthProperty().multiply(0.92), 880);
        var menuH = overlay.heightProperty().multiply(0.92);
        menuStack.maxWidthProperty().bind(menuW);
        menuStack.maxHeightProperty().bind(menuH);
        menuStack.prefWidthProperty().bind(menuW);
        menuStack.prefHeightProperty().bind(menuH);
        menuStack.minWidthProperty().bind(menuW);
        menuStack.minHeightProperty().bind(menuH);

        ImageView panel = new ImageView(AssetLoader.loadImageNatural(GameAssets.GAME_MENU_BG));
        panel.setPreserveRatio(true);
        panel.fitWidthProperty().bind(menuStack.maxWidthProperty());
        panel.setMouseTransparent(true);
        menuStack.getChildren().add(panel);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        var gridGap = menuStack.maxWidthProperty().multiply(0.018);
        grid.hgapProperty().bind(gridGap);
        grid.vgapProperty().bind(gridGap);
        ColumnConstraints colA = new ColumnConstraints();
        colA.setPercentWidth(50);
        ColumnConstraints colB = new ColumnConstraints();
        colB.setPercentWidth(50);
        grid.getColumnConstraints().addAll(colA, colB);

        double cellBtnW = 0.36;
        // game_menu_0005 = create | 0003 = save | 0004 = back home | 0002 = end (per asset filenames)
        ImageView create = btn(menuStack, GameAssets.GAME_MENU_CREATE, cellBtnW);
        SpriteButton.styleAsButton(create);
        create.setOnMouseClicked(e -> onCreateResident.run());
        grid.add(create, 0, 0);
        GridPane.setHalignment(create, HPos.CENTER);
        GridPane.setValignment(create, VPos.CENTER);

        ImageView save = btn(menuStack, GameAssets.GAME_MENU_SAVE, cellBtnW);
        SpriteButton.styleAsButton(save);
        save.setOnMouseClicked(e -> onSave.run());
        grid.add(save, 1, 0);
        GridPane.setHalignment(save, HPos.CENTER);
        GridPane.setValignment(save, VPos.CENTER);

        ImageView backHome = btn(menuStack, GameAssets.GAME_MENU_BACK_HOME, cellBtnW);
        SpriteButton.styleAsButton(backHome);
        backHome.setOnMouseClicked(e -> onBackHome.run());
        grid.add(backHome, 0, 1);
        GridPane.setHalignment(backHome, HPos.CENTER);
        GridPane.setValignment(backHome, VPos.CENTER);

        ImageView end = btn(menuStack, GameAssets.GAME_MENU_END, cellBtnW);
        SpriteButton.styleAsButton(end);
        end.setOnMouseClicked(e -> onEndGame.run());
        grid.add(end, 1, 1);
        GridPane.setHalignment(end, HPos.CENTER);
        GridPane.setValignment(end, VPos.CENTER);

        StackPane.setAlignment(grid, Pos.CENTER);
        menuStack.getChildren().add(grid);

        ImageView close = btn(menuStack, GameAssets.GAME_MENU_CLOSE, 0.09);
        StackPane closeWrap = new StackPane(close);
        closeWrap.setMaxWidth(Region.USE_PREF_SIZE);
        closeWrap.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(closeWrap, Pos.TOP_RIGHT);
        StackPane.setMargin(closeWrap, new Insets(12, 12, 0, 0));
        closeWrap.setTranslateX(45);
        closeWrap.setTranslateY(70);
        SpriteButton.styleAsButton(close);
        close.setOnMouseClicked(e -> onClose.run());
        menuStack.getChildren().add(closeWrap);
        StackPane.setAlignment(menuStack, Pos.CENTER);
        overlay.getChildren().add(menuStack);

        return overlay;
    }

    private static ImageView btn(StackPane menuStack, String asset, double widthFrac) {
        ImageView iv = new ImageView(AssetLoader.loadImageNatural(asset));
        iv.setPreserveRatio(true);
        iv.fitWidthProperty().bind(menuStack.maxWidthProperty().multiply(widthFrac));
        return iv;
    }
}
