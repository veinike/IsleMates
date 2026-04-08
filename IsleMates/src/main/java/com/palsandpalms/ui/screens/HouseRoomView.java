package com.palsandpalms.ui.screens;

import com.palsandpalms.engine.ResidentAI;
import com.palsandpalms.engine.GameState;
import com.palsandpalms.model.Resident;
import com.palsandpalms.model.Room;
import com.palsandpalms.world.BathroomResource;
import com.palsandpalms.ui.AssetLoader;
import com.palsandpalms.ui.GameAssets;
import com.palsandpalms.ui.GameViewport;
import com.palsandpalms.ui.ResidentDragGuard;
import com.palsandpalms.ui.ResidentPositionRegistry;
import com.palsandpalms.ui.SpriteButton;
import com.palsandpalms.ui.components.AnimatedResident;
import com.palsandpalms.ui.components.ConversationOverlay;
import com.palsandpalms.ui.components.ResidentHudBar;
import com.palsandpalms.ui.components.ResidentPositionDebugPane;
import com.palsandpalms.ui.components.ResidentStatsPopup;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public final class HouseRoomView {

    private HouseRoomView() {
    }

    public static StackPane create(GameState state, BathroomResource bathroom,
                                   Runnable onExitToIsland, Runnable onOpenGameMenu,
                                   BiConsumer<Resident, MouseEvent> onHudTabClick) {
        StackPane root = new StackPane();
        root.setMinSize(GameViewport.MIN_WIDTH, GameViewport.MIN_HEIGHT);
        root.setFocusTraversable(true);

        ImageView bg = new ImageView(AssetLoader.loadImageNatural(GameAssets.HOUSE_BG));
        bg.fitWidthProperty().bind(root.widthProperty());
        bg.fitHeightProperty().bind(root.heightProperty());
        bg.setPreserveRatio(false);
        root.getChildren().add(bg);

        Pane walkLayer = new Pane();
        walkLayer.setPickOnBounds(false);
        walkLayer.prefWidthProperty().bind(root.widthProperty());
        walkLayer.prefHeightProperty().bind(root.heightProperty().multiply(0.42));
        StackPane.setAlignment(walkLayer, Pos.BOTTOM_CENTER);

        Map<UUID, AnimatedResident> sprites = new HashMap<>();

        final Image imgToiletOff = AssetLoader.loadImageNatural(GameAssets.HOUSE_TOILET_OFF);
        final Image imgToiletOn = AssetLoader.loadImageNatural(GameAssets.HOUSE_TOILET_ON);
        final boolean[] lastBathOccupied = {false};

        AnchorPane layer = new AnchorPane();
        layer.prefWidthProperty().bind(root.widthProperty());
        layer.prefHeightProperty().bind(root.heightProperty());

        double exitDoorH = 0.5;
        ImageView exitBtn = new ImageView(AssetLoader.loadImageNatural(GameAssets.HOUSE_EXIT));
        exitBtn.setPreserveRatio(true);
        exitBtn.fitHeightProperty().bind(root.heightProperty().multiply(exitDoorH));
        SpriteButton.styleAsButton(exitBtn);
        exitBtn.setOnMouseClicked(e -> {
            state.getRwLock().writeLock().lock();
            try {
                for (Resident r : state.getResidentsReadOnly()) {
                    if (r.getCurrentRoom() != Room.APARTMENT) {
                        continue;
                    }
                    AnimatedResident ar = sprites.get(r.getId());
                    if (ar != null && ar.getNormX() <= 0.10 + 1e-9
                            && !ResidentDragGuard.isDragged(r.getId())) {
                        r.setCurrentRoom(Room.PARK);
                        ResidentAI.notifyPlayerRoomSwitch(r.getId());
                        ResidentPositionRegistry.update(r.getId(), 0.50);
                    }
                }
            } finally {
                state.getRwLock().writeLock().unlock();
            }
            onExitToIsland.run();
        });
        AnchorPane.setLeftAnchor(exitBtn, 0.0);
        AnchorPane.setBottomAnchor(exitBtn, 40.0);

        double bathroomDoorH = 0.45;
        ImageView toilet = new ImageView(imgToiletOff);
        toilet.setPreserveRatio(true);
        toilet.fitHeightProperty().bind(root.heightProperty().multiply(bathroomDoorH));
        SpriteButton.styleAsButton(toilet);
        toilet.setOnMouseClicked(e -> {
            state.getRwLock().writeLock().lock();
            try {
                var inBath = state.residentsInRoom(Room.BATHROOM);
                if (!inBath.isEmpty()) {
                    Resident occ = inBath.get(0);
                    if (ResidentDragGuard.isDragged(occ.getId())) {
                        return;
                    }
                    bathroom.leave(occ);
                    occ.setCurrentRoom(Room.APARTMENT);
                    ResidentAI.notifyPlayerRoomSwitch(occ.getId());
                    // Spawn near bathroom door on re-entry to apartment
                    AnimatedResident arBath = sprites.get(occ.getId());
                    if (arBath != null) {
                        arBath.randomizeNormXInRange(0.85, 0.95);
                    }
                }
            } finally {
                state.getRwLock().writeLock().unlock();
            }
        });
        AnchorPane.setRightAnchor(toilet, 140.0);
        AnchorPane.setBottomAnchor(toilet, 52.5);

        ImageView sofa = new ImageView(AssetLoader.loadImageNatural(GameAssets.HOUSE_SOFA));
        sofa.setPreserveRatio(true);
        sofa.fitHeightProperty().bind(root.heightProperty().multiply(0.8));
        SpriteButton.styleAsButton(sofa);
        AnchorPane.setLeftAnchor(sofa, 150.0);
        AnchorPane.setBottomAnchor(sofa, 50.0);

        layer.getChildren().addAll(exitBtn, sofa, toilet);
        root.getChildren().add(layer);

        StackPane kitchenRow = new StackPane();
        kitchenRow.prefWidthProperty().bind(root.widthProperty());
        kitchenRow.prefHeightProperty().bind(root.heightProperty());
        kitchenRow.setMouseTransparent(true);
        kitchenRow.translateYProperty().bind(root.heightProperty().multiply(-0.08));
        ImageView kitchen = new ImageView(AssetLoader.loadImageNatural(GameAssets.HOUSE_KITCHEN));
        kitchen.setPreserveRatio(true);
        kitchen.fitHeightProperty().bind(root.heightProperty().multiply(0.45));
        SpriteButton.styleAsButton(kitchen);
        kitchen.setMouseTransparent(false);
        kitchen.setTranslateX(40);
        kitchen.setTranslateY(5);
        StackPane.setAlignment(kitchen, Pos.BOTTOM_CENTER);
        kitchenRow.getChildren().add(kitchen);
        root.getChildren().add(kitchenRow);

        root.getChildren().add(walkLayer);

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

        final Map<UUID, Boolean> prevAtToilet = new HashMap<>();

        javafx.animation.AnimationTimer ticker = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                double lw = walkLayer.getWidth();
                double lh = walkLayer.getHeight();
                if (lw <= 0 || lh <= 0) {
                    return;
                }
                // Scale refHeight so that charH = refHeight * 0.18 equals root.height * 0.35
                double rh = root.getHeight() * (0.35 / 0.18);
                state.getRwLock().readLock().lock();
                try {
                    boolean bathOccupied = !state.residentsInRoom(Room.BATHROOM).isEmpty();
                    if (bathOccupied != lastBathOccupied[0]) {
                        lastBathOccupied[0] = bathOccupied;
                        toilet.setImage(bathOccupied ? imgToiletOn : imgToiletOff);
                    }

                    Set<UUID> want = new HashSet<>();
                    for (Resident r : state.getResidentsReadOnly()) {
                        if (!AnimatedResident.showInHouse(r)) {
                            continue;
                        }
                        want.add(r.getId());
                        boolean atToilet = AnimatedResident.isAtToilet(r);
                        boolean wasToilet = Boolean.TRUE.equals(prevAtToilet.get(r.getId()));
                        AnimatedResident ar = sprites.computeIfAbsent(r.getId(), id -> {
                            AnimatedResident a = new AnimatedResident(id);
                            double saved = ResidentPositionRegistry.getNormX(id);
                            if (saved >= 0 && saved <= 1) {
                                a.setNormXImmediate(saved);
                            } else {
                                a.randomizeNormXInRange(0, 0.10);
                            }
                            a.setWalkLayer(walkLayer);
                            a.setOnInspect((res, ev) ->
                                    ResidentStatsPopup.show(root, res, ev.getSceneX(), ev.getSceneY()));
                            a.setOnDrop(ev -> {
                                // Resident-to-resident check FIRST (priority over furniture)
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
                                                        && AnimatedResident.showInHouse(self)
                                                        && AnimatedResident.showInHouse(oth)) {
                                                    ConversationOverlay.show(root, self, oth);
                                                    return;
                                                }
                                            } finally {
                                                state.getRwLock().readLock().unlock();
                                            }
                                        }
                                    }
                                }
                                // Furniture checks
                                Point2D tp = toilet.sceneToLocal(ev.getSceneX(), ev.getSceneY());
                                if (tp != null && toilet.contains(tp)) {
                                    state.getRwLock().writeLock().lock();
                                    try {
                                        state.findResident(id).ifPresent(res -> {
                                            if (bathroom.tryEnter(res)) {
                                                res.setCurrentRoom(Room.BATHROOM);
                                                res.getStatus().setHygiene(100);
                                                ResidentAI.notifyPlayerRoomSwitch(id);
                                            }
                                        });
                                    } finally {
                                        state.getRwLock().writeLock().unlock();
                                    }
                                    return;
                                }
                                Point2D kp = kitchen.sceneToLocal(ev.getSceneX(), ev.getSceneY());
                                if (kp != null && kitchen.contains(kp)) {
                                    state.getRwLock().writeLock().lock();
                                    try {
                                        state.findResident(id).ifPresent(res ->
                                                res.getStatus().setHunger(100));
                                    } finally {
                                        state.getRwLock().writeLock().unlock();
                                    }
                                    return;
                                }
                                Point2D sp = sofa.sceneToLocal(ev.getSceneX(), ev.getSceneY());
                                if (sp != null && sofa.contains(sp)) {
                                    state.getRwLock().writeLock().lock();
                                    try {
                                        state.findResident(id).ifPresent(res ->
                                                res.getStatus().setTiredness(0));
                                    } finally {
                                        state.getRwLock().writeLock().unlock();
                                    }
                                    return;
                                }
                                // Exit door check
                                if (a.getNormX() <= 0.10 + 1e-9) {
                                    state.getRwLock().writeLock().lock();
                                    try {
                                        state.findResident(id).ifPresent(res -> {
                                            if (res.getCurrentRoom() == Room.APARTMENT) {
                                                res.setCurrentRoom(Room.PARK);
                                                ResidentAI.notifyPlayerRoomSwitch(id);
                                                ResidentPositionRegistry.update(id, 0.50);
                                            }
                                        });
                                    } finally {
                                        state.getRwLock().writeLock().unlock();
                                    }
                                }
                            });
                            walkLayer.getChildren().add(a);
                            return a;
                        });
                        if (!atToilet && wasToilet && r.getCurrentRoom() == Room.APARTMENT) {
                            ar.randomizeNormXInRange(0.85, 0.95);
                        }
                        prevAtToilet.put(r.getId(), atToilet);
                        if (atToilet) {
                            ar.setVisible(false);
                        } else {
                            ar.setVisible(true);
                            ar.tick(r, lw, lh, rh, now, true, null);
                        }
                    }
                    prevAtToilet.keySet().retainAll(want);
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
}
