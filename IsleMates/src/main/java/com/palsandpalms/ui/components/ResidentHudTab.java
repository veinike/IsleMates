package com.palsandpalms.ui.components;

import com.palsandpalms.model.Resident;
import com.palsandpalms.ui.AppFonts;
import com.palsandpalms.ui.GameCursors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/** Colored name chip with top-half sprite preview. */
public final class ResidentHudTab extends HBox {

    private static final String[] BG = {"#FFB5B5", "#B5FFD9", "#D9B5FF", "#FFE0B5"};

    private final ImageView preview = new ImageView();
    private final Label name = new Label();
    private Resident resident;

    public ResidentHudTab(int slotIndex) {
        super(6);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(4, 10, 4, 6));
        int i = Math.floorMod(slotIndex, BG.length);
        setStyle("-fx-background-color: " + BG[i] + "; -fx-background-radius: 10;");
        setMaxWidth(Region.USE_PREF_SIZE);
        setMaxHeight(Region.USE_PREF_SIZE);
        setCursor(GameCursors.getHover());

        preview.setPreserveRatio(true);
        preview.setFitHeight(32);
        preview.setSmooth(true);

        name.setFont(AppFonts.uiFont(12));
        getChildren().addAll(preview, name);
    }

    public void bindResident(Resident r, AnimatedResident anim) {
        this.resident = r;
        name.setText(r.getAppearance().getName());
        CharacterSpriteSheet sheet = CharacterSpriteSheet.forAppearance(r.getAppearance());
        Image img;
        if (anim != null && anim.getSheet() != null) {
            if (anim.isWalking()) {
                img = anim.isFaceRight()
                        ? sheet.walkRight(anim.getWalkFrame())
                        : sheet.walkLeft(anim.getWalkFrame());
            } else {
                img = sheet.standingForMood(r.getStatus().getMood());
            }
        } else {
            img = sheet.standingForMood(r.getStatus().getMood());
        }
        preview.setImage(img);
        if (img != null && img.getWidth() > 0 && img.getHeight() > 0) {
            double half = img.getHeight() * 0.5;
            preview.setViewport(new javafx.geometry.Rectangle2D(0, 0, img.getWidth(), half));
        } else {
            preview.setViewport(null);
        }
    }

    public Resident getResident() {
        return resident;
    }
}
