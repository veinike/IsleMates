package com.palsandpalms.ui;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

import java.net.URL;

/**
 * Loads custom cursors from {@code /assets/cursor/}. Falls back to stock cursors if PNGs are missing.
 * Images are scaled to fit 32×32 (max side) because many platforms ignore oversized {@link ImageCursor} art.
 * Hotspot X/Y apply to the scaled image; tune after playtesting.
 */
public final class GameCursors {

    private static Cursor normal = Cursor.DEFAULT;
    private static Cursor hover = Cursor.HAND;
    private static Cursor grab = Cursor.CLOSED_HAND;
    private static boolean initialized;

    private GameCursors() {
    }

    /**
     * Call once from {@link com.palsandpalms.GameApp#start} after {@link AppFonts#initialize()}.
     */
    public static void initialize() {
        synchronized (GameCursors.class) {
            if (initialized) {
                return;
            }
            Cursor n = loadCursor(GameAssets.CURSOR_NORMAL, 0, 0);
            Cursor h = loadCursor(GameAssets.CURSOR_HOVER, 0, 0);
            Cursor g = loadCursor(GameAssets.CURSOR_GRAB, 8, 8);
            if (n != null) {
                normal = n;
            }
            if (h != null) {
                hover = h;
            }
            if (g != null) {
                grab = g;
            }
            initialized = true;
        }
    }

    private static Cursor loadCursor(String fileName, double defaultHotspotX, double defaultHotspotY) {
        String path = fileName.startsWith("/") ? fileName : AssetLoader.PREFIX + fileName;
        URL url = GameCursors.class.getResource(path);
        if (url == null) {
            url = AssetLoader.class.getResource(path);
        }
        if (url == null) {
            return null;
        }
        try {
            // Do not use Image(InputStream) inside try-with-resources: the stream closes before decode finishes.
            // backgroundLoading=false loads synchronously on the FX thread (GameApp.start).
            // 32×32 max side: Windows and others often reject or ignore larger custom cursors.
            Image img = new Image(url.toExternalForm(), 32, 32, true, true, false);
            if (img.isError() || img.getWidth() <= 0 || img.getHeight() <= 0) {
                return null;
            }
            double hx = clampHotspot(defaultHotspotX, img.getWidth());
            double hy = clampHotspot(defaultHotspotY, img.getHeight());
            return new ImageCursor(img, hx, hy);
        } catch (Exception e) {
            return null;
        }
    }

    private static double clampHotspot(double v, double dim) {
        return Math.max(0, Math.min(v, Math.max(0, dim - 1)));
    }

    public static Cursor getNormal() {
        return normal;
    }

    public static Cursor getHover() {
        return hover;
    }

    public static Cursor getGrab() {
        return grab;
    }
}
