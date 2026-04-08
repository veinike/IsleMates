package com.palsandpalms.ui.components;

import com.palsandpalms.model.Gender;
import com.palsandpalms.model.HairColor;
import com.palsandpalms.model.HairLength;
import com.palsandpalms.model.ResidentAppearance;
import com.palsandpalms.ui.AssetLoader;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads character PNGs from {@code /assets/character/} for gender x hair length x (dark|light hair).
 */
public final class CharacterSpriteSheet {

    public enum Mood {
        HAPPY, NEUTRAL, SAD
    }

    private static final String[] POSE_ORDER = {
            "standing-neutral",
            "standing-happy",
            "standing-sad",
            "walking-left-1",
            "walking-left-2",
            "walking-right-1",
            "walking-right-2"
    };

    private static final Map<String, String> PATH_BY_PREFIX_AND_POSE = new HashMap<>();
    private static final Map<String, CharacterSpriteSheet> CACHE = new ConcurrentHashMap<>();

    static {
        reg("female_long_dark",
                "female_long_dark_0007_standing-neutral.png",
                "female_long_dark_0008_standing-happy.png",
                "female_long_dark_0009_standing-sad.png",
                "female_long_dark_0010_walking-left-1.png",
                "female_long_dark_0011_walking-left-2.png",
                "female_long_dark_0012_walking-right-1.png",
                "female_long_dark_0013_walking-right-2.png");
        reg("female_long_light",
                "female_long_light_0007_standing-neutral.png",
                "female_long_light_0008_standing-happy.png",
                "female_long_light_0009_standing-sad.png",
                "female_long_light_0010_walking-left-1.png",
                "female_long_light_0011_walking-left-2.png",
                "female_long_light_0012_walking-right-1.png",
                "female_long_light_0013_walking-right-2.png");
        reg("female_short_dark",
                "female_short_dark_0000_standing-neutral.png",
                "female_short_dark_0001_standing-happy.png",
                "female_short_dark_0002_standing-sad.png",
                "female_short_dark_0003_walking-left-1.png",
                "female_short_dark_0004_walking-left-2.png",
                "female_short_dark_0005_walking-right-1.png",
                "female_short_dark_0006_walking-right-2.png");
        reg("female_short_light",
                "female_short_light_0000_standing-neutral.png",
                "female_short_light_0001_standing-happy.png",
                "female_short_light_0002_standing-sad.png",
                "female_short_light_0003_walking-left-1.png",
                "female_short_light_0004_walking-left-2.png",
                "female_short_light_0006_walking-right-1.png",
                "female_short_light_0005_walking-right-2.png");
        reg("male_long_dark",
                "male_long_dark_0013_standing-neutral.png",
                "male_long_dark_0012_standing-happy.png",
                "male_long_dark_0011_standing-sad.png",
                "male_long_dark_0009_walking-left-1.png",
                "male_long_dark_0010_walking-left-2.png",
                "male_long_dark_0007_walking-right-1.png",
                "male_long_dark_0008_walking-right-2.png");
        reg("male_long_light",
                "male_long_light_0007_standing-neutral.png",
                "male_long_light_0008_standing-happy.png",
                "male_long_light_0009_standing-sad.png",
                "male_long_light_0010_walking-left-1.png",
                "male_long_light_0011_walking-left-2.png",
                "male_long_light_0012_walking-right-1.png",
                "male_long_light_0013_walking-right-2.png");
        reg("male_short_dark",
                "male_short_dark_0006_standing-neutral.png",
                "male_short_dark_0005_standing-happy.png",
                "male_short_dark_0004_standing-sad.png",
                "male_short_dark_0002_walking-left-1.png",
                "male_short_dark_0003_walking-left-2.png",
                "male_short_dark_0000_walking-right-1.png",
                "male_short_dark_0001_walking-right-2.png");
        reg("male_short_light",
                "male_short_light_0000_standing-neutral.png",
                "male_short_light_0001_standing-happy.png",
                "male_short_light_0002_standing-sad.png",
                "male_short_light_0004_walking-left-1.png",
                "male_short_light_0003_walking-left-2.png",
                "male_short_light_0006_walking-right-1.png",
                "male_short_light_0005_walking-right-2.png");
    }

    private static void reg(String prefix, String... files) {
        for (int i = 0; i < POSE_ORDER.length; i++) {
            PATH_BY_PREFIX_AND_POSE.put(prefix + ":" + POSE_ORDER[i], "character/" + files[i]);
        }
    }

    public static String hairColorToSpriteKey(HairColor c) {
        return switch (c) {
            case BLONDE -> "light";
            case BROWN, BLACK, RED -> "dark";
        };
    }

    public static String appearancePrefix(ResidentAppearance a) {
        String g = a.getGender() == Gender.FEMALE ? "female" : "male";
        String len = a.getHairLength() == HairLength.LONG ? "long" : "short";
        return g + "_" + len + "_" + hairColorToSpriteKey(a.getHairColor());
    }

    public static CharacterSpriteSheet forAppearance(ResidentAppearance a) {
        String prefix = appearancePrefix(a);
        return CACHE.computeIfAbsent(prefix, CharacterSpriteSheet::new);
    }

    private final String prefix;
    private final Map<String, Image> images = new HashMap<>();

    private CharacterSpriteSheet(String prefix) {
        this.prefix = prefix;
        for (String pose : POSE_ORDER) {
            String rel = PATH_BY_PREFIX_AND_POSE.get(prefix + ":" + pose);
            if (rel != null) {
                images.put(pose, AssetLoader.loadImageNatural(rel));
            }
        }
    }

    private Image pose(String key) {
        Image img = images.get(key);
        return img != null ? img : images.getOrDefault("standing-neutral", images.values().stream().findFirst().orElse(null));
    }

    public Image standing(Mood mood) {
        return switch (mood) {
            case HAPPY -> pose("standing-happy");
            case SAD -> pose("standing-sad");
            case NEUTRAL -> pose("standing-neutral");
        };
    }

    /** Mood 0 (and below the neutral band) uses the sad standing pose; high values happy. */
    public Image standingForMood(double mood0to100) {
        if (mood0to100 > 60) {
            return standing(Mood.HAPPY);
        }
        if (mood0to100 < 30) {
            return standing(Mood.SAD);
        }
        return standing(Mood.NEUTRAL);
    }

    public Image walkLeft(int frame) {
        return pose(frame == 0 ? "walking-left-1" : "walking-left-2");
    }

    public Image walkRight(int frame) {
        return pose(frame == 0 ? "walking-right-1" : "walking-right-2");
    }

    /** Current pose image for HUD preview (standing reflects mood). */
    public Image previewImage(Mood mood) {
        return standing(mood);
    }

    public Image previewImageFromStatus(double mood) {
        return standingForMood(mood);
    }

    public String getPrefix() {
        return prefix;
    }
}
