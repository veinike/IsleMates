package com.palsandpalms.ui.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.palsandpalms.model.Relationship;
import com.palsandpalms.model.Resident;
import com.palsandpalms.ui.AppFonts;
import com.palsandpalms.ui.IntroductionRegistry;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Full-screen dimmed conversation: two portraits in the middle, dialog bar at
 * the bottom with a typing effect. Click advances through a scripted 4-message
 * exchange; ESC dismisses at any time.
 *
 * <p>First-meeting pairs play an intro script (with name substitution) and are
 * registered in {@link IntroductionRegistry} upon completing the full script.
 * Subsequent meetings play a random dialog script.</p>
 */
public final class ConversationOverlay {

    public static final String PROPERTY_KEY = "conversationOverlay";

    private static final String FRAU_NERZ = "Frau Nerz";

    private static List<String[]> introScripts;
    private static List<String[]> dialogScripts;
    private static List<String[]> frauNerzIntroScripts;
    private static List<String[]> acquaintanceScripts;
    private static List<String[]> friendsScripts;
    private static List<String[]> closeFriendsScripts;
    private static List<String[]> argueScripts;
    private static List<String[]> confessionScripts;
    private static List<String[]> acceptedScripts;
    private static List<String[]> rejectedScripts;
    private static List<String[]> coupleScripts;

    private ConversationOverlay() {
    }

    // -------------------------------------------------------------------------
    // Script loading
    // -------------------------------------------------------------------------

    private static void ensureScriptsLoaded() {
        if (introScripts != null) {
            return;
        }
        try (InputStream is = ConversationOverlay.class
                .getResourceAsStream("/assets/conversation_scripts.json")) {
            if (is == null) {
                loadFallback();
                return;
            }
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            var root = JsonParser.parseString(json).getAsJsonObject();
            introScripts      = parseScriptList(root.getAsJsonArray("intro"));
            dialogScripts     = parseScriptList(root.getAsJsonArray("dialogs"));
            frauNerzIntroScripts = parseScriptList(root.getAsJsonArray("frau_nerz_intro"));
            acquaintanceScripts = parseScriptList(root.getAsJsonArray("acquaintance"));
            friendsScripts = parseScriptList(root.getAsJsonArray("friends"));
            closeFriendsScripts = parseScriptList(root.getAsJsonArray("close_friends"));
            argueScripts = parseScriptList(root.getAsJsonArray("argue"));
            confessionScripts = parseScriptList(root.getAsJsonArray("romantic_confession"));
            acceptedScripts = parseScriptList(root.getAsJsonArray("romantic_accepted"));
            rejectedScripts = parseScriptList(root.getAsJsonArray("romantic_rejected"));
            coupleScripts = parseScriptList(root.getAsJsonArray("romantic_couple"));
        } catch (Exception e) {
            loadFallback();
        }
    }

    private static List<String[]> parseScriptList(JsonArray arr) {
        List<String[]> list = new ArrayList<>(arr.size());
        for (var el : arr) {
            JsonArray inner = el.getAsJsonArray();
            String[] msgs = new String[inner.size()];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = inner.get(i).getAsString();
            }
            list.add(msgs);
        }
        return Collections.unmodifiableList(list);
    }

    private static void loadFallback() {
        frauNerzIntroScripts = Collections.singletonList(new String[]{
            "Entschuldigung, wir kennen uns noch gar nicht – ich bin {nameOther}.",
            "Oh! Ich bin Frau Nerz, sehr erfreut.",
            "Schön, Frau Nerz. Herzlich willkommen auf der Insel!",
            "Danke, ich glaube ich bin noch etwas verwirrt, wo ich hier gelandet bin."
        });
        introScripts = Collections.singletonList(new String[]{
            "Ich glaub wir haben uns noch nie vorgestellt – ich bin {nameLeft}.",
            "Ich heiß {nameRight}, freut mich!",
            "Schön dich kennenzulernen, {nameRight}.",
            "Genauso, {nameLeft} – lass uns öfter reden!"
        });
        acquaintanceScripts = Collections.singletonList(new String[]{
            "Hey {nameRight}, wie geht's?",
            "Ganz gut, danke! Und dir?",
            "Auch, chillen halt.",
            "Nice, nice."
        });
        friendsScripts = Collections.singletonList(new String[]{
            "Ey {nameRight}, ich muss dir was erzählen!",
            "Oh Gott was, erzähl!",
            "War mega lustig vorhin.",
            "HAHA bro, typisch du!"
        });
        closeFriendsScripts = Collections.singletonList(new String[]{
            "Ich kann mit dir über alles reden, {nameRight}.",
            "Geht mir genauso!",
            "Das ist so wholesome.",
            "Hab dich lieb, Bro."
        });
        argueScripts = Collections.singletonList(new String[]{
            "Was sollte das vorhin, {nameRight}?",
            "Was meinst du?",
            "Das war nicht okay.",
            "Whatever."
        });
        confessionScripts = Collections.singletonList(new String[]{
            "{nameRight}… ich hab Gefühle für dich.",
            "Oh… wirklich?",
            "Ja, mehr als nur Freundschaft.",
            "..."
        });
        acceptedScripts = Collections.singletonList(new String[]{
            "Ich fühl genauso.",
            "Echt?!",
            "Ja, schon länger.",
            "Das ist der schönste Moment!"
        });
        rejectedScripts = Collections.singletonList(new String[]{
            "Das ist mutig von dir, aber…",
            "Aber…?",
            "Ich seh dich eher als Freund.",
            "Okay… das tut weh, aber danke."
        });
        coupleScripts = Collections.singletonList(new String[]{
            "Hab ich dir heute schon gesagt dass du toll bist?",
            "Hör auf, ich werd rot!",
            "Nie, du verdienst das jeden Tag.",
            "Hab dich so lieb."
        });
        dialogScripts = Collections.singletonList(new String[]{
            "Bro, wie geht's dir so?",
            "Ganz gut, danke. Ein bisschen müde aber.",
            "Das kenn ich, der Tag war lang.",
            "Ja. Aber schön dass wir kurz geredet haben!"
        });
    }

    private static String[] randomScript(List<String[]> pool) {
        return pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
    }

    private static String[] applyNames(String[] script, String nameLeft, String nameRight) {
        String[] result = new String[script.length];
        for (int i = 0; i < script.length; i++) {
            result[i] = script[i]
                    .replace("{nameLeft}",  nameLeft)
                    .replace("{nameRight}", nameRight);
        }
        return result;
    }

    private static String[] applyFrauNerzNames(String[] script, String nameOther) {
        String[] result = new String[script.length];
        for (int i = 0; i < script.length; i++) {
            result[i] = script[i].replace("{nameOther}", nameOther);
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public static void show(StackPane host, Resident left, Resident right) {
        show(host, left, right, null);
    }

    public static void show(StackPane host, Resident left, Resident right, Relationship rel) {
        dismiss(host);
        ensureScriptsLoaded();

        boolean isIntro;
        if (rel != null) {
            isIntro = !rel.isIntroduced();
        } else {
            isIntro = !IntroductionRegistry.hasBeenIntroduced(left.getId(), right.getId());
        }

        // For Frau Nerz intro scripts the other resident always speaks first (left).
        // Swap so Frau Nerz is always on the right (odd message indices).
        if (isIntro && FRAU_NERZ.equals(left.getAppearance().getName())) {
            Resident tmp = left;
            left  = right;
            right = tmp;
        }

        String nameLeft  = left.getAppearance().getName();
        String nameRight = right.getAppearance().getName();

        boolean hasFrauNerz = FRAU_NERZ.equals(nameLeft) || FRAU_NERZ.equals(nameRight);

        // Determine romantic event for this conversation
        boolean confessionHappening = false;
        boolean confessionAccepted = false;

        String[] script;
        if (isIntro) {
            if (hasFrauNerz) {
                String nameOther = FRAU_NERZ.equals(nameLeft) ? nameRight : nameLeft;
                script = applyFrauNerzNames(randomScript(frauNerzIntroScripts), nameOther);
            } else {
                script = applyNames(randomScript(introScripts), nameLeft, nameRight);
            }
        } else if (rel != null && rel.isRomanticAccepted()) {
            // They're a couple
            script = applyNames(randomScript(coupleScripts), nameLeft, nameRight);
        } else if (rel != null && !rel.hasRomanticFeelings() && !rel.isRomanticRejected()
                   && rel.getValue() >= 60
                   && ThreadLocalRandom.current().nextDouble() < 0.12) {
            // Random chance to develop romantic feelings at high relationship
            rel.setRomanticFeelings(true);
            confessionHappening = true;
            confessionAccepted = ThreadLocalRandom.current().nextDouble() < 0.55;
            // Build a two-part script: confession + result
            String[] confPart = applyNames(randomScript(confessionScripts), nameLeft, nameRight);
            String[] resultPart;
            if (confessionAccepted) {
                resultPart = applyNames(randomScript(acceptedScripts), nameLeft, nameRight);
                rel.setRomanticAccepted(true);
                rel.addValue(25);
            } else {
                resultPart = applyNames(randomScript(rejectedScripts), nameLeft, nameRight);
                rel.setRomanticRejected(true);
                rel.addValue(-30);
            }
            String[] combined = new String[confPart.length + resultPart.length];
            System.arraycopy(confPart, 0, combined, 0, confPart.length);
            System.arraycopy(resultPart, 0, combined, confPart.length, resultPart.length);
            script = combined;
        } else if (rel != null && rel.hasRomanticFeelings() && !rel.isRomanticAccepted()
                   && !rel.isRomanticRejected()
                   && ThreadLocalRandom.current().nextDouble() < 0.20) {
            // Feelings exist but no confession yet — trigger it
            confessionHappening = true;
            confessionAccepted = ThreadLocalRandom.current().nextDouble() < 0.55;
            String[] confPart = applyNames(randomScript(confessionScripts), nameLeft, nameRight);
            String[] resultPart;
            if (confessionAccepted) {
                resultPart = applyNames(randomScript(acceptedScripts), nameLeft, nameRight);
                rel.setRomanticAccepted(true);
                rel.addValue(25);
            } else {
                resultPart = applyNames(randomScript(rejectedScripts), nameLeft, nameRight);
                rel.setRomanticRejected(true);
                rel.addValue(-30);
            }
            String[] combined = new String[confPart.length + resultPart.length];
            System.arraycopy(confPart, 0, combined, 0, confPart.length);
            System.arraycopy(resultPart, 0, combined, confPart.length, resultPart.length);
            script = combined;
        } else {
            // Tier-based dialogue selection
            int tier = (rel != null) ? rel.getDialogueTier() : 1;
            // Small chance of arguing when relationship is lower
            boolean arguing = rel != null && rel.getValue() < 40
                              && ThreadLocalRandom.current().nextDouble() < 0.08;
            if (arguing) {
                script = applyNames(randomScript(argueScripts), nameLeft, nameRight);
                if (rel != null) {
                    rel.addValue(-10);
                }
            } else {
                List<String[]> pool = switch (tier) {
                    case 1 -> acquaintanceScripts;
                    case 2 -> friendsScripts;
                    case 3 -> closeFriendsScripts;
                    case 4 -> closeFriendsScripts;
                    default -> dialogScripts;
                };
                script = applyNames(randomScript(pool), nameLeft, nameRight);
                if (rel != null) {
                    rel.addValue(5);
                }
            }
        }
        int maxMessages = script.length;

        // --- Build UI ---
        StackPane root = new StackPane();
        root.setMinSize(0, 0);
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.prefWidthProperty().bind(host.widthProperty());
        root.prefHeightProperty().bind(host.heightProperty());

        Rectangle dim = new Rectangle();
        dim.widthProperty().bind(host.widthProperty());
        dim.heightProperty().bind(host.heightProperty());
        dim.setFill(Color.color(0, 0, 0, 0.72));

        CharacterSpriteSheet sheetL = CharacterSpriteSheet.forAppearance(left.getAppearance());
        CharacterSpriteSheet sheetR = CharacterSpriteSheet.forAppearance(right.getAppearance());

        ImageView portraitL = new ImageView(sheetL.standingForMood(left.getStatus().getMood()));
        portraitL.setPreserveRatio(true);
        portraitL.setFitHeight(280);

        ImageView portraitR = new ImageView(sheetR.standingForMood(right.getStatus().getMood()));
        portraitR.setPreserveRatio(true);
        portraitR.setFitHeight(280);

        StackPane cellL = new StackPane(portraitL);
        cellL.setAlignment(Pos.CENTER);
        StackPane.setMargin(portraitL, new Insets(24, 12, 24, 24));

        StackPane cellR = new StackPane(portraitR);
        cellR.setAlignment(Pos.CENTER);
        StackPane.setMargin(portraitR, new Insets(24, 24, 24, 12));

        HBox portraits = new HBox();
        portraits.setAlignment(Pos.CENTER);
        HBox.setHgrow(cellL, Priority.ALWAYS);
        HBox.setHgrow(cellR, Priority.ALWAYS);
        portraits.getChildren().addAll(cellL, cellR);
        StackPane.setAlignment(portraits, Pos.CENTER);

        VBox dialog = new VBox(8);
        dialog.setAlignment(Pos.TOP_LEFT);
        dialog.setPadding(new Insets(16, 28, 20, 28));
        dialog.setStyle("-fx-background-color: rgba(20,20,35,0.92); -fx-background-radius: 12;");
        var dialogH = host.heightProperty().multiply(0.20);
        dialog.prefHeightProperty().bind(dialogH);
        dialog.maxHeightProperty().bind(dialogH);
        dialog.setMinHeight(0);
        StackPane.setAlignment(dialog, Pos.BOTTOM_CENTER);
        StackPane.setMargin(dialog, new Insets(0, 24, 24, 24));

        Label nameLine = new Label();
        nameLine.setFont(AppFonts.uiFont(18));
        nameLine.setTextFill(Color.WHITE);

        Label bodyLine = new Label();
        bodyLine.setFont(AppFonts.uiFont(15));
        bodyLine.setTextFill(Color.LIGHTGRAY);
        bodyLine.setWrapText(true);
        bodyLine.setMaxWidth(Double.MAX_VALUE);
        bodyLine.setMinHeight(54);

        dialog.getChildren().addAll(nameLine, bodyLine);

        // Capture final references for lambda use after potential swap
        final Resident finalLeft  = left;
        final Resident finalRight = right;
        final Relationship finalRel = rel;

        // Speaker is determined by messageIndex % 2 (0 = left, 1 = right)
        final Resident[] speakers = {finalLeft, finalRight};
        final int[] messageIndex = {0};

        TranslateTransition bobL = makeBob(portraitL);
        TranslateTransition bobR = makeBob(portraitR);
        bobR.stop();

        Timeline typing = new Timeline();

        Runnable setActiveBob = () -> {
            bobL.stop();
            bobR.stop();
            portraitL.setTranslateY(0);
            portraitR.setTranslateY(0);
            if (messageIndex[0] % 2 == 0) {
                bobL.play();
            } else {
                bobR.play();
            }
        };

        Runnable restartTyping = () -> {
            typing.stop();
            Resident sp = speakers[messageIndex[0] % 2];
            sp.getStatus().setMood(Math.min(100, sp.getStatus().getMood() + 10));
            String full = script[messageIndex[0]];
            nameLine.setText(sp.getAppearance().getName() + ":");
            bodyLine.setText("");
            typing.getKeyFrames().setAll();
            int[] ci = {0};
            typing.getKeyFrames().add(new KeyFrame(Duration.millis(40), ev -> {
                if (ci[0] < full.length()) {
                    bodyLine.setText(full.substring(0, ++ci[0]));
                }
            }));
            typing.setCycleCount(Math.max(1, full.length()));
            typing.playFromStart();
        };

        Runnable doDismiss = () -> {
            typing.stop();
            bobL.stop();
            bobR.stop();
            ConversationOverlay.dismiss(host);
        };

        Runnable advance = () -> {
            messageIndex[0]++;
            if (messageIndex[0] >= maxMessages) {
                if (isIntro) {
                    IntroductionRegistry.markIntroduced(finalLeft.getId(), finalRight.getId());
                    if (finalRel != null) {
                        finalRel.setIntroduced(true);
                    }
                }
                doDismiss.run();
                return;
            }
            setActiveBob.run();
            restartTyping.run();
        };

        setActiveBob.run();
        restartTyping.run();

        root.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> advance.run());
        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                doDismiss.run();
                e.consume();
            }
        });

        Label escHint = new Label("ESC zum Beenden");
        escHint.setFont(AppFonts.uiFont(12));
        escHint.setTextFill(Color.gray(0.6));
        StackPane.setAlignment(escHint, Pos.TOP_RIGHT);
        StackPane.setMargin(escHint, new Insets(12, 16, 0, 0));

        StackPane content = new StackPane(dim, portraits, dialog, escHint);
        root.getChildren().add(content);

        host.getChildren().add(root);
        host.getProperties().put(PROPERTY_KEY, root);
        root.requestFocus();
    }

    private static TranslateTransition makeBob(ImageView iv) {
        TranslateTransition t = new TranslateTransition(Duration.millis(600), iv);
        t.setFromY(0);
        t.setToY(-4);
        t.setAutoReverse(true);
        t.setCycleCount(Animation.INDEFINITE);
        return t;
    }

    public static void dismiss(StackPane host) {
        Object o = host.getProperties().remove(PROPERTY_KEY);
        if (o instanceof StackPane sp) {
            host.getChildren().remove(sp);
        }
    }

    public static boolean isShowing(StackPane host) {
        return host.getProperties().containsKey(PROPERTY_KEY);
    }
}
