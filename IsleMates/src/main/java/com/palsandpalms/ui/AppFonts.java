package com.palsandpalms.ui;

import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.text.Font;

import java.io.InputStream;
import java.util.Objects;

/**
 * Loads the embedded primary font from {@link GameAssets#FONT_PRIMARY} and exposes it for all UI text
 * (labels, fields, HUD, dialogs, {@link javafx.scene.text.Text} such as names / speech bubbles).
 */
public final class AppFonts {

    private static final Object LOCK = new Object();
    private static volatile String primaryFamily = Font.getDefault().getFamily();
    private static boolean initialized;

    private AppFonts() {
    }

    /**
     * Loads the font once. Call from {@link javafx.application.Application#start} before building scenes.
     */
    public static void initialize() {
        synchronized (LOCK) {
            if (initialized) {
                return;
            }
            String path = GameAssets.path(GameAssets.FONT_PRIMARY);
            try (InputStream in = AppFonts.class.getResourceAsStream(path)) {
                if (in != null) {
                    Font loaded = Font.loadFont(in, 12);
                    if (loaded != null) {
                        primaryFamily = loaded.getFamily();
                    }
                }
            } catch (Exception ignored) {
                // keep primaryFamily as system default
            }
            initialized = true;
        }
    }

    public static String getPrimaryFamily() {
        return primaryFamily;
    }

    /** Default UI font at the given pixel size. */
    public static Font uiFont(double size) {
        return Font.font(primaryFamily, size);
    }

    /**
     * Sets the default font for the scene graph (cascades to {@link javafx.scene.control.Labeled} and most controls).
     */
    public static void applyToScene(Scene scene) {
        Objects.requireNonNull(scene);
        String decl = "-fx-font-family: " + quoteCssFontFamily(primaryFamily) + ";";
        javafx.scene.Node root = scene.getRoot();
        String cur = root.getStyle();
        if (cur == null || cur.isBlank()) {
            root.setStyle(decl);
        } else if (!cur.contains("-fx-font-family")) {
            root.setStyle(cur + " " + decl);
        }
    }

    public static void styleDialog(DialogPane pane) {
        Objects.requireNonNull(pane);
        pane.setStyle("-fx-font-family: " + quoteCssFontFamily(primaryFamily) + ";");
    }

    private static String quoteCssFontFamily(String family) {
        String escaped = family.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }
}
