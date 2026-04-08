package com.palsandpalms.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GameAssetsLoadsTest {

    @Test
    void knownSpriteAssetsExistOnClasspath() {
        for (String file : new String[]{
                GameAssets.MAIN_MENU_START_CONTINUE,
                GameAssets.ISLAND_CLOUDS,
                GameAssets.HOUSE_BG,
                GameAssets.GAME_MENU_BG,
                GameAssets.CC_FEMALE,
                GameAssets.MAIN_MENU_BACKGROUND,
                GameAssets.FONT_PRIMARY
        }) {
            assertNotNull(AssetLoader.class.getResource(GameAssets.path(file)), file);
        }
    }
}
