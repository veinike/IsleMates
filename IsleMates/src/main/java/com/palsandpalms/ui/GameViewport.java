package com.palsandpalms.ui;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Fixed 16:9 logical viewport: initial scene size, minimum window size, and stage resize behavior.
 */
public final class GameViewport {

    public static final double ASPECT_WIDTH = 16.0;
    public static final double ASPECT_HEIGHT = 9.0;

    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;

    public static final int MIN_WIDTH = 640;
    public static final int MIN_HEIGHT = 360;

    private GameViewport() {
    }

    /**
     * Keeps the stage outer width and height in a 16:9 ratio whenever the user resizes the window.
     */
    public static void enforceSixteenByNine(Stage stage) {
        Objects.requireNonNull(stage);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        final int[] guard = {0};
        stage.widthProperty().addListener((obs, ov, nv) -> {
            if (guard[0] != 0) {
                return;
            }
            double w = nv.doubleValue();
            if (w <= 0) {
                return;
            }
            guard[0]++;
            try {
                double targetH = w * ASPECT_HEIGHT / ASPECT_WIDTH;
                if (Math.abs(stage.getHeight() - targetH) > 0.5) {
                    stage.setHeight(targetH);
                }
            } finally {
                guard[0]--;
            }
        });
        stage.heightProperty().addListener((obs, ov, nv) -> {
            if (guard[0] != 0) {
                return;
            }
            double h = nv.doubleValue();
            if (h <= 0) {
                return;
            }
            guard[0]++;
            try {
                double targetW = h * ASPECT_WIDTH / ASPECT_HEIGHT;
                if (Math.abs(stage.getWidth() - targetW) > 0.5) {
                    stage.setWidth(targetW);
                }
            } finally {
                guard[0]--;
            }
        });
        stage.maximizedProperty().addListener((obs, wasMax, nowMax) -> {
            if (!nowMax) {
                return;
            }
            Platform.runLater(() -> {
                stage.setMaximized(false);
                Rectangle2D vis = Screen.getPrimary().getVisualBounds();
                double maxW = vis.getWidth();
                double maxH = vis.getHeight();
                double w = maxW;
                double h = w * ASPECT_HEIGHT / ASPECT_WIDTH;
                if (h > maxH) {
                    h = maxH;
                    w = h * ASPECT_WIDTH / ASPECT_HEIGHT;
                }
                w = Math.max(MIN_WIDTH, w);
                h = Math.max(MIN_HEIGHT, h);
                stage.setWidth(w);
                stage.setHeight(h);
                stage.setX(vis.getMinX() + (maxW - w) * 0.5);
                stage.setY(vis.getMinY() + (maxH - h) * 0.5);
            });
        });
    }
}
