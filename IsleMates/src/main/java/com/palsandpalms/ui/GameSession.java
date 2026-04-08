package com.palsandpalms.ui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.palsandpalms.engine.EventManager;
import com.palsandpalms.engine.GameClock;
import com.palsandpalms.engine.GameState;
import com.palsandpalms.engine.InteractionManager;
import com.palsandpalms.engine.InteractionTicker;
import com.palsandpalms.engine.ResidentAI;
import com.palsandpalms.input.InputHandler;
import com.palsandpalms.model.Resident;
import com.palsandpalms.model.StarterResidents;
import com.palsandpalms.persistence.AutoSaveTask;
import com.palsandpalms.persistence.SaveManager;
import com.palsandpalms.world.BathroomResource;
import com.palsandpalms.world.Fridge;
import com.palsandpalms.world.GameEventQueue;

/** Owns game state and background threads (T-22). */
public final class GameSession {
    private final Path saveDir;
    private final GameState state = new GameState();
    private final Fridge fridge = new Fridge(20);
    private final BathroomResource bathroom = new BathroomResource();
    private final GameEventQueue eventQueue = new GameEventQueue();
    private final SaveManager saveManager;
    private final InteractionManager interactionManager = new InteractionManager(state);
    private final InputHandler inputHandler = new InputHandler(state, interactionManager);

    private final List<Thread> threads = new ArrayList<>();
    private final List<ResidentAI> residentAis = new ArrayList<>();
    private ExecutorService aiPool = Executors.newCachedThreadPool();

    private GameClock clock;
    private EventManager eventManager;
    private AutoSaveTask autoSaveTask;
    private InteractionTicker interactionTicker;

    public GameSession(Path saveDir) {
        this.saveDir = saveDir;
        this.saveManager = new SaveManager(saveDir);
    }

    public GameState getState() {
        return state;
    }

    public Fridge getFridge() {
        return fridge;
    }

    public BathroomResource getBathroom() {
        return bathroom;
    }

    public GameEventQueue getEventQueue() {
        return eventQueue;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public InteractionManager getInteractionManager() {
        return interactionManager;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public void startNewGame() throws Exception {
        Files.createDirectories(saveDir);
        stopBackgroundThreads();
        IntroductionRegistry.clear();
        state.getRwLock().writeLock().lock();
        try {
            state.clearAllResidentsAndRelationships();
            state.getActiveGlobalEvents().clear();
            Resident nina = StarterResidents.createNina();
            Resident victoria = StarterResidents.createVictoria();
            state.addResident(nina);
            state.addResident(victoria);
            state.getOrCreateRelationship(nina.getId(), victoria.getId()).setValue(0);
            state.setTutorialCompleted(false);
            state.setTimeOfDay(com.palsandpalms.model.TimeOfDay.MORNING);
            state.setGameTick(0);
        } finally {
            state.getRwLock().writeLock().unlock();
        }
        fridge.setFoodUnits(20);
        gameStarted = true;
        startBackgroundThreads();
    }

    public void loadExistingSave() throws Exception {
        Files.createDirectories(saveDir);
        stopBackgroundThreads();
        var data = saveManager.loadRaw();
        if (data != null) {
            SaveManager.apply(data, state, fridge);
        } else {
            startNewGame();
            return;
        }
        gameStarted = true;
        startBackgroundThreads();
    }

    private void startBackgroundThreads() {
        inputHandler.reset();
        clock = new GameClock(state, 100);
        Thread clockThread = new Thread(clock, "GameClock");
        clockThread.setDaemon(true);
        clockThread.start();
        threads.add(clockThread);

        eventManager = new EventManager(eventQueue, state, 3000, 8000);
        Thread emThread = new Thread(eventManager, "EventManager");
        emThread.setDaemon(true);
        emThread.start();
        threads.add(emThread);

        autoSaveTask = new AutoSaveTask(saveManager, state, fridge, 30_000);
        Thread autoThread = new Thread(autoSaveTask, "AutoSave");
        autoThread.setDaemon(true);
        autoThread.start();
        threads.add(autoThread);

        interactionTicker = new InteractionTicker(interactionManager);
        Thread itThread = new Thread(interactionTicker, "InteractionTicker");
        itThread.setDaemon(true);
        itThread.start();
        threads.add(itThread);

        Thread inThread = new Thread(inputHandler, "InputHandler");
        inThread.setDaemon(true);
        inThread.start();
        threads.add(inThread);

        aiPool = Executors.newCachedThreadPool();
        residentAis.clear();
        state.getRwLock().readLock().lock();
        List<Resident> residents;
        try {
            residents = new ArrayList<>(state.getResidentsReadOnly());
        } finally {
            state.getRwLock().readLock().unlock();
        }
        for (Resident r : residents) {
            String threadName = "ResidentAI-" + r.getAppearance().getName();
            ResidentAI ai = new ResidentAI(r.getId(), state, fridge, bathroom, eventQueue);
            residentAis.add(ai);
            aiPool.submit(() -> {
                Thread.currentThread().setName(threadName);
                ai.run();
            });
        }
    }

    private void stopBackgroundThreads() {
        if (clock != null) {
            clock.shutdown();
        }
        if (eventManager != null) {
            eventManager.shutdown();
        }
        if (autoSaveTask != null) {
            autoSaveTask.shutdown();
        }
        if (interactionTicker != null) {
            interactionTicker.shutdown();
        }
        inputHandler.shutdown();
        for (ResidentAI ai : residentAis) {
            ai.shutdown();
        }
        residentAis.clear();
        aiPool.shutdownNow();
        for (Thread t : threads) {
            t.interrupt();
        }
        threads.clear();
    }

    private boolean gameStarted = false;

    public void shutdown() {
        stopBackgroundThreads();
        if (gameStarted) {
            try {
                saveManager.save(state, fridge);
            } catch (Exception ignored) {
            }
        }
    }

    /** Spawn AI thread for a resident added at runtime (NFA-14). */
    public void spawnResidentAi(java.util.UUID residentId) {
        state.getRwLock().readLock().lock();
        String threadName;
        try {
            Resident r = state.findResident(residentId).orElse(null);
            if (r == null) {
                return;
            }
            threadName = "ResidentAI-" + r.getAppearance().getName();
        } finally {
            state.getRwLock().readLock().unlock();
        }
        ResidentAI ai = new ResidentAI(residentId, state, fridge, bathroom, eventQueue);
        residentAis.add(ai);
        aiPool.submit(() -> {
            Thread.currentThread().setName(threadName);
            ai.run();
        });
    }
}
