package com.palsandpalms.ui.screens;

import com.palsandpalms.model.EyeColor;
import com.palsandpalms.model.Gender;
import com.palsandpalms.model.HairColor;
import com.palsandpalms.model.HairLength;
import com.palsandpalms.model.SkinTone;
import com.palsandpalms.ui.AssetLoader;
import com.palsandpalms.ui.GameAssets;
import com.palsandpalms.ui.GameViewport;
import com.palsandpalms.ui.SpriteButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public final class CharacterCreationView {

    private CharacterCreationView() {
    }

    public static StackPane create(Runnable onCancel, Consumer<CreationResult> onSave) {
        StackPane root = new StackPane();
        root.setMinSize(GameViewport.MIN_WIDTH, GameViewport.MIN_HEIGHT);

        ImageView bg = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_BG));
        bg.fitWidthProperty().bind(root.widthProperty());
        bg.fitHeightProperty().bind(root.heightProperty());
        bg.setPreserveRatio(false);
        root.getChildren().add(bg);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setMaxWidth(280);
        nameField.setStyle("-fx-font-size: 16px;");

        ImageView titleGender = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_TITLE_GENDER));
        titleGender.setPreserveRatio(true);
        titleGender.setFitWidth(200);

        ImageView female = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_FEMALE));
        female.setPreserveRatio(true);
        female.setFitWidth(140);
        ImageView male = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_MALE));
        male.setPreserveRatio(true);
        male.setFitWidth(140);
        SpriteButton.styleAsButton(female);
        SpriteButton.styleAsButton(male);
        Gender[] genderRef = {Gender.FEMALE};
        wirePair(female, male, true, () -> genderRef[0] = Gender.FEMALE, () -> genderRef[0] = Gender.MALE);

        ImageView titleLen = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_TITLE_HAIR_LEN));
        titleLen.setPreserveRatio(true);
        titleLen.setFitWidth(220);

        ImageView longH = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_HAIR_LONG));
        longH.setPreserveRatio(true);
        longH.setFitWidth(140);
        ImageView shortH = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_HAIR_SHORT));
        shortH.setPreserveRatio(true);
        shortH.setFitWidth(140);
        SpriteButton.styleAsButton(longH);
        SpriteButton.styleAsButton(shortH);
        HairLength[] lenRef = {HairLength.LONG};
        wirePair(longH, shortH, true, () -> lenRef[0] = HairLength.LONG, () -> lenRef[0] = HairLength.SHORT);

        ImageView titleColor = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_TITLE_HAIR_COLOR));
        titleColor.setPreserveRatio(true);
        titleColor.setFitWidth(240);

        ImageView brown = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_BROWN));
        brown.setPreserveRatio(true);
        brown.setFitWidth(140);
        ImageView blonde = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_BLONDE));
        blonde.setPreserveRatio(true);
        blonde.setFitWidth(140);
        SpriteButton.styleAsButton(brown);
        SpriteButton.styleAsButton(blonde);
        HairColor[] colorRef = {HairColor.BROWN};
        wirePair(brown, blonde, true, () -> colorRef[0] = HairColor.BROWN, () -> colorRef[0] = HairColor.BLONDE);

        HBox genderRow = new HBox(24, female, male);
        genderRow.setAlignment(Pos.CENTER);
        HBox lenRow = new HBox(24, longH, shortH);
        lenRow.setAlignment(Pos.CENTER);
        HBox colorRow = new HBox(24, brown, blonde);
        colorRow.setAlignment(Pos.CENTER);

        ImageView saveBtn = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_SAVE));
        saveBtn.setPreserveRatio(true);
        saveBtn.setFitWidth(200);
        SpriteButton.styleAsButton(saveBtn);
        saveBtn.setOnMouseClicked(e -> {
            String n = nameField.getText().isBlank() ? "Bewohner" : nameField.getText();
            onSave.accept(new CreationResult(n, genderRef[0], lenRef[0], colorRef[0]));
        });

        ImageView cancelBtn = new ImageView(AssetLoader.loadImageNatural(GameAssets.CC_CANCEL));
        cancelBtn.setPreserveRatio(true);
        cancelBtn.setFitWidth(160);
        SpriteButton.styleAsButton(cancelBtn);
        cancelBtn.setOnMouseClicked(e -> onCancel.run());

        HBox actions = new HBox(24, cancelBtn, saveBtn);
        actions.setAlignment(Pos.CENTER);

        VBox form = new VBox(16,
                nameField,
                titleGender, genderRow,
                titleLen, lenRow,
                titleColor, colorRow,
                actions);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(40));
        StackPane.setAlignment(form, Pos.CENTER);
        root.getChildren().add(form);

        return root;
    }

    private static void wirePair(ImageView a, ImageView b, boolean aSelected, Runnable selectA, Runnable selectB) {
        ColorAdjust dim = new ColorAdjust();
        dim.setBrightness(-0.2);
        ColorAdjust full = new ColorAdjust();
        full.setBrightness(0);
        a.setEffect(aSelected ? full : dim);
        b.setEffect(aSelected ? dim : full);
        a.setOnMouseClicked(e -> {
            selectA.run();
            a.setEffect(full);
            b.setEffect(dim);
        });
        b.setOnMouseClicked(e -> {
            selectB.run();
            b.setEffect(full);
            a.setEffect(dim);
        });
    }

    public record CreationResult(String name, Gender gender, HairLength hairLength, HairColor hairColor) {
        public com.palsandpalms.model.ResidentAppearance toAppearance() {
            return new com.palsandpalms.model.ResidentAppearance(
                    name, gender, hairColor, hairLength, EyeColor.BLUE, SkinTone.LIGHT);
        }
    }
}
