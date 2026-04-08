package com.palsandpalms.ui;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.Objects;

/** Loads PNGs from classpath; generates placeholders when missing (T-15b). */
public final class AssetLoader {
    public static final String PREFIX = "/assets/";

    private AssetLoader() {
    }

    /** Load image at natural size (best for sprite UI). */
    public static Image loadImageNatural(String fileName) {
        Objects.requireNonNull(fileName);
        String path = fileName.startsWith("/") ? fileName : PREFIX + fileName;
        try (InputStream in = AssetLoader.class.getResourceAsStream(path)) {
            if (in != null) {
                return new Image(in);
            }
        } catch (Exception ignored) {
        }
        return new WritableImage(1, 1);
    }

    public static Image loadImage(String resourcePath, double w, double h, Color fallbackColor) {
        Objects.requireNonNull(resourcePath);
        String path = resourcePath.startsWith("/") ? resourcePath : PREFIX + resourcePath;
        try (InputStream in = AssetLoader.class.getResourceAsStream(path)) {
            if (in != null) {
                return new Image(in, w, h, true, true);
            }
        } catch (Exception ignored) {
        }
        WritableImage img = new WritableImage((int) Math.max(1, w), (int) Math.max(1, h));
        var pw = img.getPixelWriter();
        for (int x = 0; x < (int) w; x++) {
            for (int y = 0; y < (int) h; y++) {
                pw.setColor(x, y, fallbackColor);
            }
        }
        return img;
    }
}
