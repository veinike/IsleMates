package com.palsandpalms.ui;

/** Classpath paths under {@code /assets/} for sprite UI (flat layout). */
public final class GameAssets {
    private GameAssets() {
    }

    /** Main menu / tutorial flow backdrop (not character creation). */
    public static final String MAIN_MENU_BACKGROUND = "main-menu_background.png";

    /** Primary UI font (classpath under {@code /assets/fonts/}). */
    public static final String FONT_PRIMARY = "fonts/DreamLife-V1.0.0_by_MaxiGamer.ttf";

    /** Custom cursors under {@code /assets/cursor/}; hotspots in {@link GameCursors}. */
    public static final String CURSOR_NORMAL = "cursor/cursor_normal.png";
    public static final String CURSOR_HOVER = "cursor/cursor_hover.png";
    public static final String CURSOR_GRAB = "cursor/cursor_grab.png";

    public static final String MAIN_MENU_START_CONTINUE = "main-menu_0000_button-start-or-continue-game.png";
    public static final String MAIN_MENU_START_NEW = "main-menu_0001_button-start-new-game.png";
    public static final String MAIN_MENU_EXIT = "main-menu_0002_button-exit-game.png";

    public static final String ISLAND_BG_PLAIN = "main-island_0004_background-plain.png";
    public static final String ISLAND_CLOUDS = "main-island_0000_clouds.png";
    public static final String ISLAND_HOUSE = "main-island_0001_button-house.png";
    public static final String ISLAND_GROUND = "main-island_0002_ground.png";
    public static final String ISLAND_MOUNTAIN = "main-island_0003_background-mountain.png";

    /** Day/night dial: 0° = day (sun) at top; rotates 360° per full in-game day (see {@link com.palsandpalms.engine.GameState#DAY_NIGHT_CYCLE_MS}). */
    public static final String DAY_NIGHT_CYCLE = "cycle.png";

    public static final String HOUSE_BG = "house_room_0005_room-background.png";
    public static final String HOUSE_SOFA = "house_room_0000_room-sofa.png";
    public static final String HOUSE_KITCHEN = "house_room_0001_room-kitchen.png";
    public static final String HOUSE_EXIT = "house_room_0002_button_room-exit.png";
    public static final String HOUSE_TOILET_ON = "house_room_0003_button_room-toilet-door-on.png";
    public static final String HOUSE_TOILET_OFF = "house_room_0004_button_room-toilet-door-off.png";

    public static final String GAME_MENU_BG = "game_menu_0000_menu-game-menu.png";
    public static final String GAME_MENU_CLOSE = "game_menu_0001_button-close-X.png";
    public static final String GAME_MENU_END = "game_menu_0002_button-end-game.png";
    public static final String GAME_MENU_SAVE = "game_menu_0003_button-save-game.png";
    public static final String GAME_MENU_BACK_HOME = "game_menu_0004_button-back-to-home-menu.png";
    public static final String GAME_MENU_CREATE = "game_menu_0005_button-create-new-resident.png";

    public static final String CC_BG = "menu-character-creation_0011-menu-background.png";
    public static final String CC_FEMALE = "menu-character-creation_0000_button-female.png";
    public static final String CC_MALE = "menu-character-creation_0001_button-male.png";
    public static final String CC_HAIR_LONG = "menu-character-creation_0002_button-hair-long.png";
    public static final String CC_HAIR_SHORT = "menu-character-creation_0003_button-hair-short.png";
    public static final String CC_SAVE = "menu-character-creation_0004_button-save-and-start.png";
    public static final String CC_CANCEL = "menu-character-creation_0005_button-cancel.png";
    public static final String CC_TITLE_GENDER = "menu-character-creation_0006_title-gender.png";
    public static final String CC_TITLE_HAIR_LEN = "menu-character-creation_0007_title-hair-length.png";
    public static final String CC_TITLE_HAIR_COLOR = "menu-character-creation_0008_button-hair-color.png";
    public static final String CC_BROWN = "menu-character-creation_0009_button-brown.png";
    public static final String CC_BLONDE = "menu-character-creation_0010_button-blonde.png";

    public static String path(String fileName) {
        return AssetLoader.PREFIX + fileName;
    }
}
