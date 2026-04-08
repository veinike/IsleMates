package com.palsandpalms.ui.components;

import com.palsandpalms.model.Resident;
import com.palsandpalms.model.Room;
import com.palsandpalms.ui.GameCursors;
import com.palsandpalms.ui.ResidentDragGuard;
import com.palsandpalms.ui.ResidentPositionRegistry;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.Random;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Wandering character sprite with drag-and-drop, clamped walk X, free Y while dragging.
 * Y is held on the walk baseline unless the player drops the sprite above it, then gravity applies until landing.
 */
public final class AnimatedResident extends StackPane {

    public static final long WALK_FRAME_NANOS = 300_000_000L;
    private static final double BOTTOM_MARGIN = 25;
    private static final double WALK_STEP_FACTOR = 0.0002;
    private static final double WALK_STEP_MIN = 0.3;
    private static final double DRAG_LERP = 0.42;
    private static final double MOMENTUM_DECAY = 0.88;
    private static final double DRAG_START_THRESHOLD = 6.0;
    /** Pixels per frame² while falling (~60 FPS feel). */
    private static final double GRAVITY = 0.55;

    private final UUID residentId;
    private final Random rng = new Random();
    private final ImageView iv = new ImageView();

    private CharacterSpriteSheet sheet;
    private boolean walking;
    private boolean faceRight = true;
    private int walkFrame;
    private long lastFrameSwapNanos;
    private double normX = 0.5;
    private double normTarget = 0.5;
    private boolean pinned;

    private BiConsumer<Resident, MouseEvent> onInspect;
    private Consumer<MouseEvent> onDrop;

    private Pane walkLayer;
    private double lastLayerW;
    private double lastLayerWalkH;
    private double lastCharH;
    private double lastRefHeight;

    private boolean dragGesture;
    private double pressSceneX;
    private double pressSceneY;
    private double dragTargetNormX;
    private double momentumNormPerFrame;
    private double dragLastNormX;
    private double dragSecondLastNormX;
    private boolean sceneShowingGrabCursor;
    /** Last pointer position while dragging (scene coords). */
    private double dragSceneX;
    private double dragSceneY;
    /** Walk-layer Y of press minus {@link #getLayoutY()} so the sprite follows the cursor vertically. */
    private double dragGrabOffsetY;
    /** Downward velocity while falling after a drop from above the walk line (pixels per frame). */
    private double fallVelY;
    /** True only after releasing a drag while above the ground; otherwise Y is snapped to the walk baseline each tick. */
    private boolean fallingFromDrop;

    public AnimatedResident(UUID residentId) {
        this.residentId = residentId;
        iv.setPickOnBounds(true);
        iv.setPreserveRatio(true);
        getChildren().add(iv);
        setPickOnBounds(true);
        setCursor(GameCursors.getHover());

        addEventHandler(MouseEvent.MOUSE_PRESSED, this::handlePressed);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleDragged);
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleReleased);
    }

    public UUID getResidentId() {
        return residentId;
    }

    public void setWalkLayer(Pane walkLayer) {
        this.walkLayer = walkLayer;
    }

    public void setOnInspect(BiConsumer<Resident, MouseEvent> onInspect) {
        this.onInspect = onInspect;
    }

    public void setOnDrop(Consumer<MouseEvent> onDrop) {
        this.onDrop = onDrop;
    }

    private void handlePressed(MouseEvent e) {
        if (pinned || walkLayer == null) {
            return;
        }
        pressSceneX = e.getSceneX();
        pressSceneY = e.getSceneY();
        dragSceneX = pressSceneX;
        dragSceneY = pressSceneY;
        dragGesture = false;
        walking = false;
        momentumNormPerFrame = 0;
        dragSecondLastNormX = normX;
        dragLastNormX = normX;
        updateDragTargetFromScene(e.getSceneX(), e.getSceneY());
        Point2D lp = walkLayer.sceneToLocal(pressSceneX, pressSceneY);
        dragGrabOffsetY = lp != null ? lp.getY() - getLayoutY() : 0;
        e.consume();
    }

    private void handleDragged(MouseEvent e) {
        if (pinned || walkLayer == null) {
            return;
        }
        double dx = e.getSceneX() - pressSceneX;
        double dy = e.getSceneY() - pressSceneY;
        if (!dragGesture && (dx * dx + dy * dy) > DRAG_START_THRESHOLD * DRAG_START_THRESHOLD) {
            dragGesture = true;
            ResidentDragGuard.beginDrag(residentId);
            fallingFromDrop = false;
            fallVelY = 0;
            Scene sc = getScene();
            if (sc != null) {
                sc.setCursor(GameCursors.getGrab());
                sceneShowingGrabCursor = true;
            }
        }
        if (dragGesture) {
            dragSceneX = e.getSceneX();
            dragSceneY = e.getSceneY();
            updateDragTargetFromScene(dragSceneX, dragSceneY);
            e.consume();
        }
    }

    private void handleReleased(MouseEvent e) {
        if (pinned || walkLayer == null) {
            return;
        }
        try {
            Resident r = (Resident) getProperties().get("lastResident");
            if (dragGesture) {
                momentumNormPerFrame = Math.max(-0.042, Math.min(0.042,
                        (dragLastNormX - dragSecondLastNormX) * 14.0));
                double lh = walkLayer != null ? walkLayer.getHeight() : lastLayerWalkH;
                double ch = lastCharH > 0 ? lastCharH : 80;
                double yG = groundY(lh, ch);
                fallingFromDrop = getLayoutY() < yG - 1.0;
                fallVelY = 0;
                // End guard before drop handlers run so intentional room changes on release still apply.
                ResidentDragGuard.endDrag(residentId);
                if (onDrop != null) {
                    onDrop.accept(e);
                }
                e.consume();
            } else if (r != null && onInspect != null) {
                onInspect.accept(r, e);
                e.consume();
            }
            dragGesture = false;
            clearGrabSceneCursorIfNeeded();
        } finally {
            ResidentDragGuard.endDrag(residentId);
        }
    }

    private void clearGrabSceneCursorIfNeeded() {
        if (!sceneShowingGrabCursor) {
            return;
        }
        Scene sc = getScene();
        if (sc != null) {
            sc.setCursor(GameCursors.getNormal());
        }
        sceneShowingGrabCursor = false;
    }

    private void updateDragTargetFromScene(double sceneX, double sceneY) {
        Point2D local = walkLayer.sceneToLocal(sceneX, sceneY);
        if (local == null) {
            return;
        }
        double lw = lastLayerW > 0 ? lastLayerW : walkLayer.getWidth();
        double ch = lastCharH > 0 ? lastCharH : 80;
        double w = estimateWidth(ch);
        double usableW = Math.max(40, lw - w);
        double centerX = local.getX() - w * 0.5;
        dragTargetNormX = clamp01(centerX / usableW);
    }

    private static double clamp01(double v) {
        return Math.max(0, Math.min(1, v));
    }

    /**
     * @param layerW        walk layer width
     * @param layerWalkH    walk layer height (for bottom alignment)
     * @param refHeight     scene/root height for sprite scale
     * @param nowNanos      animation timer time
     * @param allowWander   random walk when idle
     * @param pinNormX      if non-null, pin to this normalized x (toilet)
     */
    public void tick(Resident resident, double layerW, double layerWalkH, double refHeight, long nowNanos,
                     boolean allowWander, Double pinNormX) {
        if (resident == null) {
            return;
        }
        getProperties().put("lastResident", resident);
        lastLayerW = layerW;
        lastLayerWalkH = layerWalkH;
        lastRefHeight = refHeight;

        sheet = CharacterSpriteSheet.forAppearance(resident.getAppearance());
        double charH = Math.max(80, refHeight * 0.18);
        lastCharH = charH;
        iv.setFitHeight(charH);
        iv.setFitWidth(0);

        if (pinNormX != null) {
            pinned = true;
            normX = clamp01(pinNormX);
            normTarget = normX;
            walking = false;
            dragGesture = false;
            fallVelY = 0;
            fallingFromDrop = false;
            iv.setImage(sheet.standingForMood(resident.getStatus().getMood()));
            layoutFromNorm(layerW, layerWalkH, charH);
            ResidentPositionRegistry.update(residentId, normX);
            return;
        }
        pinned = false;

        double usableW = Math.max(40, layerW - estimateWidth(charH));
        double yGround = groundY(layerWalkH, charH);

        if (dragGesture) {
            normX += (dragTargetNormX - normX) * DRAG_LERP;
            normX = clamp01(normX);
            boolean moving = Math.abs(dragTargetNormX - normX) > 0.004;
            if (moving) {
                if (nowNanos - lastFrameSwapNanos >= WALK_FRAME_NANOS) {
                    walkFrame = 1 - walkFrame;
                    lastFrameSwapNanos = nowNanos;
                }
                faceRight = dragTargetNormX >= normX;
                iv.setImage(faceRight ? sheet.walkRight(walkFrame) : sheet.walkLeft(walkFrame));
            } else {
                iv.setImage(sheet.standingForMood(resident.getStatus().getMood()));
            }
            dragSecondLastNormX = dragLastNormX;
            dragLastNormX = normX;
            applyHorizontalLayout(layerW, charH, usableW);
            Point2D local = walkLayer.sceneToLocal(dragSceneX, dragSceneY);
            if (local != null) {
                double ty = local.getY() - dragGrabOffsetY;
                ty = Math.max(0, Math.min(ty, yGround));
                setLayoutY(ty);
            }
            return;
        }

        updateVerticalPosition(yGround);

        if (Math.abs(momentumNormPerFrame) > 1e-5) {
            normX += momentumNormPerFrame;
            normX = clamp01(normX);
            momentumNormPerFrame *= MOMENTUM_DECAY;
            if (Math.abs(momentumNormPerFrame) < 1e-4) {
                momentumNormPerFrame = 0;
            }
            if (nowNanos - lastFrameSwapNanos >= WALK_FRAME_NANOS) {
                walkFrame = 1 - walkFrame;
                lastFrameSwapNanos = nowNanos;
            }
            faceRight = momentumNormPerFrame >= 0;
            iv.setImage(faceRight ? sheet.walkRight(walkFrame) : sheet.walkLeft(walkFrame));
            applyHorizontalLayout(layerW, charH, usableW);
            return;
        }

        if (!walking) {
            iv.setImage(sheet.standingForMood(resident.getStatus().getMood()));
            if (allowWander && rng.nextDouble() < 0.008) {
                normTarget = clamp01(0.12 + rng.nextDouble() * 0.76);
                walking = true;
                faceRight = normTarget > normX;
                walkFrame = 0;
                lastFrameSwapNanos = nowNanos;
            }
        } else {
            if (nowNanos - lastFrameSwapNanos >= WALK_FRAME_NANOS) {
                walkFrame = 1 - walkFrame;
                lastFrameSwapNanos = nowNanos;
            }
            iv.setImage(faceRight ? sheet.walkRight(walkFrame) : sheet.walkLeft(walkFrame));

            double px = normX * usableW;
            double tgt = normTarget * usableW;
            double step = Math.max(WALK_STEP_MIN, layerW * WALK_STEP_FACTOR);
            if (Math.abs(px - tgt) <= step) {
                normX = normTarget;
                normX = clamp01(normX);
                walking = false;
                iv.setImage(sheet.standingForMood(resident.getStatus().getMood()));
            } else {
                if (tgt > px) {
                    normX += step / usableW;
                    faceRight = true;
                } else {
                    normX -= step / usableW;
                    faceRight = false;
                }
                normX = clamp01(normX);
            }
        }

        applyHorizontalLayout(layerW, charH, usableW);
        ResidentPositionRegistry.update(residentId, normX);
    }

    private static double groundY(double layerWalkH, double charH) {
        return Math.max(0, layerWalkH - charH - BOTTOM_MARGIN);
    }

    private void updateVerticalPosition(double yGround) {
        if (!fallingFromDrop) {
            setLayoutY(yGround);
            fallVelY = 0;
            return;
        }
        if (getLayoutY() < yGround - 0.75) {
            fallVelY += GRAVITY;
            double ny = getLayoutY() + fallVelY;
            if (ny >= yGround) {
                setLayoutY(yGround);
                fallVelY = 0;
                fallingFromDrop = false;
            } else {
                setLayoutY(ny);
            }
        } else {
            setLayoutY(yGround);
            fallVelY = 0;
            fallingFromDrop = false;
        }
    }

    private void applyHorizontalLayout(double layerW, double charH, double usableW) {
        double w = estimateWidth(charH);
        setLayoutX(normX * usableW);
        setMinSize(w, charH);
        setPrefSize(w, charH);
        setMaxSize(w, charH);
    }

    private double estimateWidth(double charH) {
        if (iv.getImage() == null) {
            return charH * 0.5;
        }
        double iw = iv.getImage().getWidth();
        double ih = iv.getImage().getHeight();
        if (ih <= 0) {
            return charH * 0.5;
        }
        return charH * iw / ih;
    }

    private void layoutFromNorm(double layerW, double layerWalkH, double charH) {
        double w = estimateWidth(charH);
        double usableW = Math.max(40, layerW - w);
        setLayoutX(normX * usableW);
        setLayoutY(groundY(layerWalkH, charH));
        fallVelY = 0;
        fallingFromDrop = false;
        setMinSize(w, charH);
        setPrefSize(w, charH);
        setMaxSize(w, charH);
    }

    public double getNormX() {
        return normX;
    }

    /** Stops motion and sets horizontal position in walk layer [0,1] (left–right). */
    public void setNormXImmediate(double nx) {
        normX = clamp01(nx);
        normTarget = normX;
        walking = false;
        momentumNormPerFrame = 0;
        dragGesture = false;
        fallVelY = 0;
        fallingFromDrop = false;
    }

    /** Random {@link #setNormXImmediate(double)} in [{@code minNorm}, {@code maxNorm}] (clamped to [0,1]). */
    public void randomizeNormXInRange(double minNorm, double maxNorm) {
        double lo = clamp01(Math.min(minNorm, maxNorm));
        double hi = clamp01(Math.max(minNorm, maxNorm));
        double span = hi - lo;
        setNormXImmediate(lo + (span > 0 ? rng.nextDouble() * span : 0));
    }

    public boolean isWalking() {
        return walking;
    }

    public boolean isFaceRight() {
        return faceRight;
    }

    public int getWalkFrame() {
        return walkFrame;
    }

    public CharacterSpriteSheet getSheet() {
        return sheet;
    }

    public ImageView getImageView() {
        return iv;
    }

    public static boolean showOnIsland(Resident r) {
        return r.getCurrentRoom() == Room.PARK;
    }

    public static boolean showInHouse(Resident r) {
        Room room = r.getCurrentRoom();
        return room == Room.APARTMENT || room == Room.BATHROOM;
    }

    public static boolean isAtToilet(Resident r) {
        return r.getCurrentRoom() == Room.BATHROOM;
    }
}
