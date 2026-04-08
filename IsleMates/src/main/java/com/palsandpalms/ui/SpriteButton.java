package com.palsandpalms.ui;

import com.palsandpalms.ui.GameCursors;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/** Hover: 300ms smooth translate Y -5px (up) relative to a base offset. */
public final class SpriteButton {
    private static final Duration HOVER_DURATION = Duration.millis(300);
    private static final double HOVER_DY = -5;

    private SpriteButton() {
    }

    public static void styleAsButton(ImageView iv) {
        styleAsButton(iv, 0);
    }

    /** Do not bind {@code translateY} on {@code iv}; use a parent wrapper for layout offsets. */
    public static void styleAsButton(ImageView iv, double baseTranslateY) {
        iv.setPickOnBounds(true);
        iv.setCursor(GameCursors.getHover());
        iv.setTranslateY(baseTranslateY);
        attachHover(iv, baseTranslateY);
    }

    public static void attachHover(ImageView iv) {
        attachHover(iv, 0);
    }

    public static void attachHover(ImageView iv, double baseTranslateY) {
        TranslateTransition up = new TranslateTransition(HOVER_DURATION, iv);
        up.setInterpolator(Interpolator.EASE_BOTH);
        TranslateTransition down = new TranslateTransition(HOVER_DURATION, iv);
        down.setInterpolator(Interpolator.EASE_BOTH);

        iv.setOnMouseEntered(e -> {
            down.stop();
            up.setFromY(iv.getTranslateY());
            up.setToY(baseTranslateY + HOVER_DY);
            up.playFromStart();
        });
        iv.setOnMouseExited(e -> {
            up.stop();
            down.setFromY(iv.getTranslateY());
            down.setToY(baseTranslateY);
            down.playFromStart();
        });
    }
}
