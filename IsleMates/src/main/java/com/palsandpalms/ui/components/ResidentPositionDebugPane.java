package com.palsandpalms.ui.components;

import com.palsandpalms.engine.GameState;
import com.palsandpalms.model.Resident;
import com.palsandpalms.ui.AppFonts;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Map;
import java.util.UUID;

/**
 * Top-right HUD: resident room, normalized X, visibility, and layout X/Y for diagnosing “disappearing” sprites.
 */
public final class ResidentPositionDebugPane extends VBox {

    private final Label text = new Label();

    public ResidentPositionDebugPane() {
        setAlignment(Pos.TOP_RIGHT);
        setPadding(new Insets(6, 8, 6, 8));
        setStyle("-fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 8;");
        // StackPane stretches children to fill; cap size so this stays a small top-right panel.
        final double w = 260;
        setPrefWidth(w);
        setMaxWidth(w);
        setMaxHeight(Region.USE_PREF_SIZE);
        text.setFont(AppFonts.uiFont(9));
        text.setTextFill(Color.LIGHTGRAY);
        text.setWrapText(true);
        text.setMaxWidth(w - 16);
        getChildren().add(text);
    }

    public void update(GameState state, Map<UUID, AnimatedResident> sprites) {
        StringBuilder sb = new StringBuilder();
        sb.append("[debug positions]\n");
        state.getRwLock().readLock().lock();
        try {
            for (Resident r : state.getResidentsReadOnly()) {
                AnimatedResident ar = sprites.get(r.getId());
                sb.append(r.getAppearance().getName())
                        .append(" | ")
                        .append(r.getCurrentRoom())
                        .append(" | nx=");
                if (ar == null) {
                    sb.append("--");
                } else {
                    sb.append(String.format("%.3f", ar.getNormX()));
                }
                sb.append(" | vis=");
                sb.append(ar != null && ar.isVisible());
                if (ar != null) {
                    sb.append(String.format(" | L(%.0f,%.0f)", ar.getLayoutX(), ar.getLayoutY()));
                }
                sb.append('\n');
            }
        } finally {
            state.getRwLock().readLock().unlock();
        }
        text.setText(sb.toString());
    }
}
