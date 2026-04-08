package com.palsandpalms.ui.screens;

import com.palsandpalms.engine.ResidentAI;
import com.palsandpalms.engine.GameState;
import com.palsandpalms.model.Resident;
import com.palsandpalms.model.Room;
import com.palsandpalms.ui.AssetLoader;
import com.palsandpalms.ui.GameAssets;
import com.palsandpalms.ui.GameViewport;
import com.palsandpalms.ui.ResidentPositionRegistry;
import com.palsandpalms.ui.SpriteButton;
import com.palsandpalms.ui.components.AnimatedResident;
import com.palsandpalms.ui.components.ConversationOverlay;
import com.palsandpalms.ui.components.ResidentHudBar;
import com.palsandpalms.ui.components.ResidentPositionDebugPane;
import com.palsandpalms.ui.components.ResidentStatsPopup;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public final class MainIslandView {

    private MainIslandView() {
    }

    public static StackPane create(GameState state, Runnable onHouseClicked, Runnable onOpenGameMenu,
                                   BiConsumer<Resident, MouseEvent> onHudTabClick) {
        StackPane root = new StackPane();
        root.setMinSize(GameViewport.MIN_WIDTH, GameViewport.MIN_HEIGHT);
        root.setFocusTraversable(true);

        ImageView plain = new ImageView(AssetLoader.loadImageNatural(GameAssets.ISLAND_BG_PLAIN));
        plain.fitWidthProperty().bind(root.widthProperty());
        plain.fitHeightProperty().bind(root.heightProperty());
        plain.setPreserveRatio(false);
        root.getChildren().add(plain);

        ImageView mountain = new ImageView(AssetLoader.loadImageNatural(GameAssets.ISLAND_MOUNTAIN));
        mountain.setPreserveRatio(true);
        mountain.fitWidthProperty().bind(root.widthProperty().multiply(0.70));
        StackPane.setAlignment(mountain, Pos.BOTTOM_RIGHT);
        mountain.setTranslateY(20);
        root.getChildren().add(mountain);

        ImageView ground = new ImageView(AssetLoader.loadImageNatural(GameAssets.ISLAND_GROUND));
        ground.setPreserveRatio(true);
        ground.fitWidthProperty().bind(root.widthProperty());
        StackPane.setAlignment(ground, Pos.BOTTOM_CENTER);
        root.getChildren().add(ground);

        Rectangle nightOverlay = new Rectangle();
        nightOverlay.widthProperty().bind(root.widthProperty());
        nightOverlay.heightProperty().bind(root.heightProperty());
        nightOverlay.setMouseTransparent(true);
        nightOverlay.setFill(Color.TRANSPARENT);
        root.getChildren().add(nightOverlay);

        Pane walkLayer = new Pane();
        walkLayer.setPickOnBounds(false);
        walkLayer.prefWidthProperty().bind(root.widthProperty());
        walkLayer.prefHeightProperty().bind(root.heightProperty().multiply(0.36));
        StackPane.setAlignment(walkLayer, Pos.BOTTOM_CENTER);

        Map<UUID, AnimatedResident> sprites = new HashMap<>();

        ImageView house = new ImageView(AssetLoader.loadImageNatural(GameAssets.ISLAND_HOUSE));
        house.setPreserveRatio(true);
        house.fitWidthProperty().bind(root.widthProperty().multiply(0.35));
        StackPane.setAlignment(house, Pos.BOTTOM_CENTER);
        SpriteButton.styleAsButton(house, -25);
        house.setOnMouseClicked(e -> onHouseClicked.run());
        root.getChildren().add(house);

        StackPane cloudLayer = new StackPane();
        cloudLayer.setMouseTransparent(true);
        cloudLayer.setPickOnBounds(false);
        double cloudBand = 0.42;
        cloudLayer.setMaxWidth(Region.USE_PREF_SIZE);
        cloudLayer.setMaxHeight(Region.USE_PREF_SIZE);
        cloudLayer.prefWidthProperty().bind(root.widthProperty());
        cloudLayer.prefHeightProperty().bind(root.heightProperty().multiply(cloudBand));
        Rectangle cloudClip = new Rectangle();
        cloudClip.widthProperty().bind(root.widthProperty());
        cloudClip.heightProperty().bind(root.heightProperty().multiply(cloudBand));
        cloudLayer.setClip(cloudClip);
        StackPane.setAlignment(cloudLayer, Pos.TOP_CENTER);

        ImageView clouds = new ImageView(AssetLoader.loadImageNatural(GameAssets.ISLAND_CLOUDS));
        clouds.setMouseTransparent(true);
        clouds.setPreserveRatio(false);
        clouds.fitWidthProperty().bind(root.widthProperty());
        clouds.fitHeightProperty().bind(root.heightProperty().multiply(cloudBand));
        StackPane.setAlignment(clouds, Pos.TOP_CENTER);
        cloudLayer.getChildren().add(clouds);
        root.getChildren().add(cloudLayer);

        root.getChildren().add(walkLayer);

        Timeline cloudDrift = new Timeline();
        cloudDrift.setCycleCount(Timeline.INDEFINITE);
        cloudDrift.setAutoReverse(true);
        Runnable rebuildCloudMotion = () -> {
            double w = root.getWidth();
            if (w <= 0) {
                return;
            }
            double amp = Math.max(24, w * 0.08);
            cloudDrift.stop();
            cloudDrift.getKeyFrames().setAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(clouds.translateXProperty(), -amp, Interpolator.EASE_BOTH)),
                    new KeyFrame(Duration.seconds(16),
                            new KeyValue(clouds.translateXProperty(), amp, Interpolator.EASE_BOTH)));
            cloudDrift.play();
        };
        root.widthProperty().addListener((o, ov, nv) -> rebuildCloudMotion.run());
        Platform.runLater(rebuildCloudMotion);

        ResidentHudBar hud = new ResidentHudBar(onHudTabClick);
        HBox hudWrap = new HBox(hud);
        hudWrap.setAlignment(Pos.CENTER_LEFT);
        hudWrap.setMaxWidth(Region.USE_PREF_SIZE);
        hudWrap.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(hudWrap, Pos.TOP_LEFT);
        StackPane.setMargin(hudWrap, new Insets(15));
        root.getChildren().add(hudWrap);

        ResidentPositionDebugPane debugPane = new ResidentPositionDebugPane();
        StackPane.setAlignment(debugPane, Pos.TOP_RIGHT);
        StackPane.setMargin(debugPane, new Insets(15));
        root.getChildren().add(debugPane);

        javafx.animation.AnimationTimer ticker = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                double lw = walkLayer.getWidth();
                double lh = walkLayer.getHeight();
                if (lw <= 0 || lh <= 0) {
                    return;
                }
                double rh = root.getHeight();
                state.getRwLock().readLock().lock();
                try {
                    nightOverlay.setFill(islandOverlayColor(state.getDayNightCycleMs()));

                    Set<UUID> want = new HashSet<>();
                    for (Resident r : state.getResidentsReadOnly()) {
                        if (!AnimatedResident.showOnIsland(r)) {
                            continue;
                        }
                        want.add(r.getId());
                        AnimatedResident ar = sprites.computeIfAbsent(r.getId(), id -> {
                            AnimatedResident a = new AnimatedResident(id);
                            double saved = ResidentPositionRegistry.getNormX(id);
                            if (saved >= 0 && saved <= 1) {
                                a.setNormXImmediate(saved);
                            } else {
                                a.randomizeNormXInRange(0.45, 0.55);
                            }
                            a.setWalkLayer(walkLayer);
                            a.setOnInspect((res, ev) ->
                                    ResidentStatsPopup.show(root, res, ev.getSceneX(), ev.getSceneY()));
                            a.setOnDrop(ev -> {
                                // Resident-to-resident check FIRST (priority over house)
                                Resident self;
                                state.getRwLock().readLock().lock();
                                try {
                                    self = state.findResident(id).orElse(null);
                                } finally {
                                    state.getRwLock().readLock().unlock();
                                }
                                if (self != null) {
                                    for (AnimatedResident other : sprites.values()) {
                                        if (other.getResidentId().equals(id)) {
                                            continue;
                                        }
                                        Bounds b = other.localToScene(other.getBoundsInLocal());
                                        if (b.contains(ev.getSceneX(), ev.getSceneY())) {
                                            state.getRwLock().readLock().lock();
                                            try {
                                                Resident oth = state.findResident(other.getResidentId()).orElse(null);
                                                if (oth != null
                                                        && AnimatedResident.showOnIsland(self)
                                                        && AnimatedResident.showOnIsland(oth)) {
                                                    ConversationOverlay.show(root, self, oth);
                                                    return;
                                                }
                                            } finally {
                                                state.getRwLock().readLock().unlock();
                                            }
                                        }
                                    }
                                }
                                // House check
                                Point2D hp = house.sceneToLocal(ev.getSceneX(), ev.getSceneY());
                                if (hp != null && house.contains(hp)) {
                                    double nx = a.getNormX();
                                    if (nx >= 0.45 - 1e-9 && nx <= 0.55 + 1e-9) {
                                        state.getRwLock().writeLock().lock();
                                        try {
                                            state.findResident(id).ifPresent(res -> {
                                                    res.setCurrentRoom(Room.APARTMENT);
                                                    ResidentAI.notifyPlayerRoomSwitch(id);
                                                    ResidentPositionRegistry.update(id, 0.05);
                                            });
                                        } finally {
                                            state.getRwLock().writeLock().unlock();
                                        }
                                    }
                                }
                            });
                            walkLayer.getChildren().add(a);
                            return a;
                        });
                        ar.tick(r, lw, lh, rh, now, true, null);
                    }
                    Iterator<Map.Entry<UUID, AnimatedResident>> it = sprites.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<UUID, AnimatedResident> e = it.next();
                        if (!want.contains(e.getKey())) {
                            walkLayer.getChildren().remove(e.getValue());
                            it.remove();
                        }
                    }
                    hud.update(state, sprites);
                    debugPane.update(state, sprites);
                } finally {
                    state.getRwLock().readLock().unlock();
                }
            }
        };
        ticker.start();

        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (ConversationOverlay.isShowing(root)) {
                    ConversationOverlay.dismiss(root);
                    e.consume();
                    return;
                }
                if (root.getProperties().containsKey("residentStatsPopup")) {
                    ResidentStatsPopup.dismiss(root);
                    e.consume();
                    return;
                }
                onOpenGameMenu.run();
                e.consume();
            }
        });

        return root;
    }

    /**
     * Smooth tint over the island background from day–night cycle (sunrise / day / sunset / night).
     */
    static Color islandOverlayColor(long cycleMs) {
        double T = GameState.DAY_NIGHT_CYCLE_MS;
        double f = Math.floorMod(cycleMs, (long) T) / T;

        Color sunrise = Color.color(255 / 255.0, 140 / 255.0, 0, 0.15);
        Color clear = Color.color(0, 0, 0, 0);
        Color sunset = Color.color(255 / 255.0, 90 / 255.0, 0, 0.20);
        Color night = Color.color(0, 0, 40 / 255.0, 0.55);

        if (f < 0.125) {
            double t = f / 0.125;
            return lerpColor(sunrise, clear, t);
        }
        if (f < 0.375) {
            return clear;
        }
        if (f < 0.5) {
            double t = (f - 0.375) / (0.5 - 0.375);
            return lerpColor(clear, sunset, t);
        }
        if (f < 0.625) {
            double t = (f - 0.5) / 0.125;
            return lerpColor(sunset, night, t);
        }
        if (f < 0.875) {
            return night;
        }
        double t = (f - 0.875) / 0.125;
        return lerpColor(night, sunrise, t);
    }

    private static Color lerpColor(Color a, Color b, double t) {
        t = Math.max(0, Math.min(1, t));
        return Color.color(
                a.getRed() + (b.getRed() - a.getRed()) * t,
                a.getGreen() + (b.getGreen() - a.getGreen()) * t,
                a.getBlue() + (b.getBlue() - a.getBlue()) * t,
                a.getOpacity() + (b.getOpacity() - a.getOpacity()) * t);
    }
}
