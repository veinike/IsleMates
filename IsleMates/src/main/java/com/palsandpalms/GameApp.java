package com.palsandpalms;

import com.palsandpalms.ui.AppFonts;
import com.palsandpalms.ui.GameCursors;
import com.palsandpalms.ui.GameViewport;
import com.palsandpalms.ui.NavigationController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Path;

public final class GameApp extends Application {
    private NavigationController navigation;
    private final Path saveDir = Path.of(System.getProperty("user.dir"), "saves");

    @Override
    public void start(Stage stage) {
        AppFonts.initialize();
        GameCursors.initialize();
        stage.setTitle("IsleMates");
        navigation = new NavigationController(stage, saveDir);
        GameViewport.enforceSixteenByNine(stage);
        navigation.showMainMenu();
        stage.setWidth(GameViewport.DEFAULT_WIDTH);
        stage.setHeight(GameViewport.DEFAULT_HEIGHT);
        stage.show();
    }

    @Override
    public void stop() {
        if (navigation != null) {
            navigation.shutdown();
        }
    }
}
