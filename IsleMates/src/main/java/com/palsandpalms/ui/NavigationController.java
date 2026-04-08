package com.palsandpalms.ui;

import java.nio.file.Path;

import com.palsandpalms.engine.GameState;
import com.palsandpalms.model.Island;
import com.palsandpalms.model.Resident;
import com.palsandpalms.model.Room;
import com.palsandpalms.model.StatusValues;
import com.palsandpalms.persistence.SaveManager;
import com.palsandpalms.ui.screens.CharacterCreationView;
import com.palsandpalms.ui.screens.GameMenuOverlay;
import com.palsandpalms.ui.screens.HouseRoomView;
import com.palsandpalms.ui.screens.MainIslandView;
import com.palsandpalms.ui.screens.MainMenuView;
import com.palsandpalms.ui.screens.TutorialView;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class NavigationController {
    private final Stage stage;
    private final Path saveDir;
    private GameSession session;
    private StackPane gameStack;

    public NavigationController(Stage stage, Path saveDir) {
        this.stage = stage;
        this.saveDir = saveDir;
        this.session = new GameSession(saveDir);
    }

    public void showMainMenu() {
        session.shutdown();
        session = new GameSession(saveDir);
        StackPane root = MainMenuView.create(
                this::startOrContinue,
                this::startNewGameOnly,
                Platform::exit,
                this::showRulesFromMenu);
        showScene(root);
        requestFocus(root);
    }

    private void showRulesFromMenu() {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                "Ziehe Bewohner auf Möbel: Kühlschrank füllt Hunger, Sofa senkt Müdigkeit, Toilette stellt Hygiene her.\n\n"
                + "Ziehe einen Bewohner auf einen anderen, um ein Gespräch zu starten (Klick wechselt den Sprecher, ESC beendet).\n\n"
                + "Bewohner können das Haus nur über die Tür links verlassen.\n\n"
                + "Die KI steuert Bewohner selbstständig je nach Bedürfnissen und Tageszeit. Nachts werden sie müde und gehen nach Hause.\n\n"
                + "Interaktionen beeinflussen Freundschaft und Stimmung.");
        a.setHeaderText("Regeln");
        AppFonts.styleDialog(a.getDialogPane());
        a.showAndWait();
    }

    private void startOrContinue() {
        try {
            SaveManager sm = session.getSaveManager();
            if (sm.saveExists()) {
                session.loadExistingSave();
                showIsland();
            } else {
                session.startNewGame();
                afterStartOrLoad();
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private void startNewGameOnly() {
        try {
            session.startNewGame();
            afterStartOrLoad();
        } catch (Exception e) {
            error(e);
        }
    }

    private void afterStartOrLoad() {
        GameState st = session.getState();
        if (!st.isTutorialCompleted()) {
            StackPane tut = TutorialView.create(() -> {
                st.setTutorialCompleted(true);
                try {
                    session.getSaveManager().save(st, session.getFridge());
                } catch (Exception ignored) {
                }
                showIsland();
            });
            showScene(tut);
            requestFocus(tut);
        } else {
            showIsland();
        }
    }

    private void showIsland() {
        gameStack = new StackPane();
        StackPane island = MainIslandView.create(
                session.getState(),
                this::showHouse,
                this::openGameMenu,
                this::onHudTabClicked);
        gameStack.getChildren().add(island);
        showScene(gameStack);
        requestFocus(island);
    }

    private void showHouse() {
        if (gameStack == null) {
            return;
        }
        StackPane house = HouseRoomView.create(session.getState(), session.getBathroom(),
                this::showIslandFromHouse, this::openGameMenu, this::onHudTabClicked);
        gameStack.getChildren().set(0, house);
        requestFocus(house);
    }

    private void showIslandFromHouse() {
        if (gameStack == null) {
            return;
        }
        StackPane island = MainIslandView.create(
                session.getState(),
                this::showHouse,
                this::openGameMenu,
                this::onHudTabClicked);
        gameStack.getChildren().set(0, island);
        requestFocus(island);
    }

    /** Sync island/house view to the resident's location, then open the game menu. */
    private void onHudTabClicked(Resident r, MouseEvent ev) {
        GameState st = session.getState();
        st.getRwLock().readLock().lock();
        Room room;
        try {
            Resident fresh = st.findResident(r.getId()).orElse(null);
            if (fresh == null) {
                return;
            }
            room = fresh.getCurrentRoom();
        } finally {
            st.getRwLock().readLock().unlock();
        }
        if (room == Room.PARK) {
            showIslandFromHouse();
        } else {
            showHouse();
        }
        openGameMenu();
    }

    private void openGameMenu() {
        if (gameStack == null) {
            return;
        }
        while (gameStack.getChildren().size() > 1) {
            gameStack.getChildren().removeLast();
        }
        StackPane overlay = GameMenuOverlay.create(
                this::openCharacterCreation,
                this::saveGame,
                this::backToHomeMenu,
                this::endGame,
                this::closeGameMenu);
        gameStack.getChildren().add(overlay);
    }

    private void closeGameMenu() {
        if (gameStack != null && gameStack.getChildren().size() > 1) {
            gameStack.getChildren().removeLast();
        }
    }

    private void saveGame() {
        try {
            session.getSaveManager().save(session.getState(), session.getFridge());
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Dein Spielstand wurde gespeichert!");
            a.setHeaderText("Spiel gespeichert");
            AppFonts.styleDialog(a.getDialogPane());
            a.showAndWait();
        } catch (Exception e) {
            error(e);
        }
    }

    private void backToHomeMenu() {
        closeGameMenu();
        session.shutdown();
        session = new GameSession(saveDir);
        showMainMenu();
    }

    private void endGame() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Spiel wirklich beenden?");
        AppFonts.styleDialog(a.getDialogPane());
        a.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    session.getSaveManager().save(session.getState(), session.getFridge());
                } catch (Exception ignored) {
                }
                Platform.exit();
            }
        });
    }

    private void openCharacterCreation() {
        GameState st = session.getState();
        st.getRwLock().readLock().lock();
        int count;
        try {
            count = st.getIsland().getResidentCount();
        } finally {
            st.getRwLock().readLock().unlock();
        }
        if (count >= Island.MAX_RESIDENTS) {
            Alert cap = new Alert(Alert.AlertType.INFORMATION, "Maximal 4 Bewohner.");
            AppFonts.styleDialog(cap.getDialogPane());
            cap.showAndWait();
            return;
        }
        closeGameMenu();
        StackPane cc = CharacterCreationView.create(
                () -> gameStack.getChildren().removeLast(),
                result -> {
                    try {
                        java.util.UUID newId;
                        session.getState().getRwLock().writeLock().lock();
                        try {
                            if (session.getState().getIsland().getResidentCount() >= Island.MAX_RESIDENTS) {
                                return;
                            }
                            Resident r = new Resident(result.toAppearance(), new StatusValues(), Room.APARTMENT);
                            session.getState().addResident(r);
                            newId = r.getId();
                        } finally {
                            session.getState().getRwLock().writeLock().unlock();
                        }
                        session.spawnResidentAi(newId);
                    } catch (Exception e) {
                        error(e);
                    } finally {
                        gameStack.getChildren().removeLast();
                    }
                });
        gameStack.getChildren().add(cc);
        requestFocus(cc);
    }

    private static void requestFocus(javafx.scene.Node n) {
        Platform.runLater(() -> {
            if (n.getScene() != null) {
                n.requestFocus();
            }
        });
    }

    private void error(Throwable t) {
        Alert a = new Alert(Alert.AlertType.ERROR, t.getMessage());
        AppFonts.styleDialog(a.getDialogPane());
        a.showAndWait();
    }

    private void showScene(Parent root) {
        Scene scene = new Scene(root, GameViewport.DEFAULT_WIDTH, GameViewport.DEFAULT_HEIGHT);
        AppFonts.applyToScene(scene);
        scene.setCursor(GameCursors.getNormal());
        stage.setScene(scene);
    }

    public void shutdown() {
        if (session != null) {
            session.shutdown();
        }
    }
}
