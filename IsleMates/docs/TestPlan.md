# Pals & Palms -- Test Plan

Every subagent **must** run and pass the relevant tests for its phase before reporting completion.
Tests use **JUnit 5** (added as a Maven test dependency). Concurrency tests use `ExecutorService`,
`CountDownLatch`, and `CyclicBarrier` to simulate multi-threaded scenarios.

All test classes live under `src/test/java/com/palsandpalms/` mirroring the main source structure.

---

## Phase 2: Core Model Layer

### T-03 -- Resident, StatusValues, Inventory, Item (Subagent A1)

**Test class:** `model/ResidentTest.java`

- [ ] **M-01** Create a Resident with all appearance fields; verify getters return correct values
- [ ] **M-02** StatusValues initialize to default values (e.g., hunger=100, tiredness=0, mood=100, hygiene=100)
- [ ] **M-03** StatusValues degrade correctly: call `tick()` and verify hunger decreases, tiredness increases
- [ ] **M-04** StatusValues clamp to [0, 100] range -- cannot go below 0 or above 100
- [ ] **M-05** Inventory starts empty; add an Item, verify size=1; remove it, verify size=0
- [ ] **M-06** Inventory does not accept null items
- [ ] **M-07** Resident `equals()` and `hashCode()` work correctly based on unique identifier

### T-04 -- Room, Island, TimeOfDay (Subagent A2)

**Test class:** `model/IslandTest.java`

- [ ] **M-10** Room enum contains island, apartment, toilet: PARK, APARTMENT, BATHROOM
- [ ] **M-11** TimeOfDay enum cycles correctly: MORNING -> NOON -> EVENING -> NIGHT -> MORNING
- [ ] **M-12** Island enforces max capacity of 4 residents -- adding a 5th throws or returns false
- [ ] **M-13** Island tracks resident positions; moving a resident updates their current room
- [ ] **M-14** Island can list all residents currently in a specific room

### T-05 -- Relationship, GameEvent, GameSaveData (Subagent A3)

**Test class:** `model/RelationshipTest.java`

- [ ] **M-20** Relationship initializes with value 0
- [ ] **M-21** Relationship value increases after positive interaction
- [ ] **M-22** Relationship value decreases after negative interaction
- [ ] **M-23** Friendship flag triggers when value crosses threshold
- [ ] **M-24** GameEvent enum contains: CONCERT, RAINY_DAY, DELIVERY, BIRTHDAY
- [ ] **M-25** GameSaveData can be constructed from GameState and contains all required fields

---

## Phase 3: Shared Resources & Synchronization

### T-06 -- BathroomResource (Subagent B1)

**Test class:** `world/BathroomResourceTest.java`

- [ ] **S-01** Single resident can enter and leave the bathroom
- [ ] **S-02** Second resident is blocked (returns false / waits) when bathroom is occupied
- [ ] **S-03** After first resident leaves, second resident can enter
- [ ] **S-04** **Concurrency test:** Launch 10 threads each trying to enter the bathroom simultaneously; assert that at any point, at most 1 thread is inside (use an AtomicInteger counter; assert it never exceeds 1)
- [ ] **S-05** **Concurrency test:** No deadlock occurs -- all 10 threads eventually complete within a timeout

### T-07 -- Fridge (Subagent B2)

**Test class:** `world/FridgeTest.java`

- [ ] **S-10** Fridge initializes with a configurable stock count
- [ ] **S-11** `takeFood()` decrements stock by 1 and returns an Item
- [ ] **S-12** `takeFood()` on empty fridge returns null or throws (no crash, no negative stock)
- [ ] **S-13** `addFood()` increments stock
- [ ] **S-14** **Concurrency test:** Launch 20 threads, each calling `takeFood()` on a fridge with 10 items; assert final stock is 0 and exactly 10 threads received food (no duplicates, no negative)
- [ ] **S-15** **Concurrency test:** Mixed read/write -- 10 threads take, 5 threads add; verify final count is consistent

### T-08 -- EventQueue (Subagent B3)

**Test class:** `world/EventQueueTest.java`

- [ ] **S-20** Produce an event, consume it -- received event matches produced event
- [ ] **S-21** Queue is FIFO -- produce A then B, consume order is A then B
- [ ] **S-22** `take()` blocks when queue is empty (verify with a timed test)
- [ ] **S-23** **Concurrency test (Producer-Consumer):** 1 producer adds 100 events; 4 consumer threads each poll; total consumed events equals 100
- [ ] **S-24** **Concurrency test:** Multiple producers (3) and multiple consumers (4); all produced events are consumed exactly once

---

## Phase 4: Game Engine Core

### T-09 -- GameState with ReadWriteLock (Subagent C1)

**Test class:** `engine/GameStateTest.java`

- [ ] **E-01** GameState can be read by multiple threads simultaneously (no blocking between readers)
- [ ] **E-02** GameState write blocks readers and other writers
- [ ] **E-03** After a write completes, readers see the updated state
- [ ] **E-04** **Concurrency test:** 10 reader threads + 2 writer threads; no data corruption after 1000 operations; assert invariants hold (e.g. resident count consistent)
- [ ] **E-05** Adding/removing residents through write lock updates correctly

### T-10 -- GameClock (Subagent C2)

**Test class:** `engine/GameClockTest.java`

- [ ] **E-10** GameClock starts at MORNING
- [ ] **E-11** After sufficient ticks, time advances to NOON, then EVENING, then NIGHT, then back to MORNING
- [ ] **E-12** GameClock runs in its own thread and does not block the test thread
- [ ] **E-13** GameClock can be stopped gracefully (interrupt or shutdown flag)
- [ ] **E-14** Time change notifications are delivered (via listener/callback)

### T-11 -- EventManager (Subagent C3)

**Test class:** `engine/EventManagerTest.java`

- [ ] **E-20** EventManager produces events into the EventQueue
- [ ] **E-21** Events produced are valid GameEvent enum values
- [ ] **E-22** EventManager runs in its own thread and produces events at random intervals
- [ ] **E-23** EventManager can be stopped gracefully
- [ ] **E-24** Over a sufficient runtime, all event types are produced at least once (statistical; run with short intervals)

---

## Phase 5: Resident AI & Interactions

### T-12 -- ResidentAI

**Test class:** `engine/ResidentAITest.java`

- [ ] **A-01** Resident AI degrades status values over time when running
- [ ] **A-02** When hunger is critical, resident moves to fridge and takes food
- [ ] **A-03** When hygiene is critical, resident moves to bathroom (acquires lock)
- [ ] **A-04** At night, resident sleeps (tiredness decreases)
- [ ] **A-05** Resident reacts to events from EventQueue (e.g., moves to park on CONCERT)
- [ ] **A-06** **Concurrency test:** 4 residents running simultaneously; bathroom never has more than 1 occupant
- [ ] **A-07** **Concurrency test:** 4 residents accessing fridge; stock never goes negative
- [ ] **A-08** Resident AI can be stopped gracefully

### T-13 -- InteractionManager

**Test class:** `engine/InteractionManagerTest.java`

- [ ] **A-10** Two residents in the same room can trigger TALK interaction
- [ ] **A-11** TALK increases relationship value
- [ ] **A-12** ARGUE decreases relationship value
- [ ] **A-13** GIVE_GIFT increases relationship value and transfers item
- [ ] **A-14** Friendship forms when relationship crosses threshold
- [ ] **A-15** After ARGUE, avoidance timer is set; residents don't interact during cooldown
- [ ] **A-16** **Concurrency test:** Two pairs of residents interacting simultaneously; relationship locks prevent corruption

---

## Phase 6: Persistence

### T-14/T-15 -- SaveManager & AutoSaveTask

**Test class:** `persistence/SaveManagerTest.java`

- [ ] **P-01** `save()` writes a valid JSON file to disk
- [ ] **P-02** `load()` reads the JSON file and reconstructs GameSaveData with correct fields
- [ ] **P-03** Round-trip: save then load produces identical state (residents, relationships, time, events)
- [ ] **P-04** `load()` returns null or empty state when no save file exists
- [ ] **P-05** Save file contains all fields specified in FA-45 (residents, appearances, status, inventory, relationships, time, events)
- [ ] **P-06** AutoSaveTask runs periodically in its own thread
- [ ] **P-07** AutoSaveTask acquires read lock (does not block game logic)
- [ ] **P-08** **Concurrency test:** AutoSaveTask saving while GameClock and ResidentAI modify state -- no corruption in saved file

---

## Phase 7: JavaFX UI

UI tests verify screen composition and navigation. These are **manual verification** tests
unless TestFX is added. Each subagent must at minimum verify that the screen launches without
exceptions and contains the required elements.

### T-16 -- HomeScreen (Subagent D2)

**Test class:** `ui/HomeScreenTest.java`

- [ ] **U-01** HomeScreen launches without exceptions
- [ ] **U-02** Title "IsleMates" label is present
- [ ] **U-03** First launch shows "Spiel starten" and "Regeln lesen" buttons
- [ ] **U-04** With existing save, shows "Spiel fortsetzen", "Neues Spiel starten", "Regeln lesen"
- [ ] **U-05** "Regeln lesen" navigates to RulesScreen
- [ ] **U-06** RulesScreen has a "Zuruck" button that returns to HomeScreen

### T-17 -- ResidentCreationScreen (Subagent D3)

**Test class:** `ui/ResidentCreationScreenTest.java`

- [ ] **U-10** Creation screen launches without exceptions
- [ ] **U-11** All selection fields present: name, gender, hair color, hair length, eye color, skin color
- [ ] **U-12** Each field has predefined selectable options
- [ ] **U-13** "Zuruck" button returns to game screen without creating a resident
- [ ] **U-14** Confirm creates a new Resident with selected attributes

### T-18 -- GameScreen + InGameMenuScreen (Subagent D4)

**Test class:** `ui/GameScreenTest.java`

- [ ] **U-20** GameScreen launches and renders without exceptions
- [ ] **U-21** Resident sprites are displayed at their room positions
- [ ] **U-22** Status bars are visible for selected/hovered resident
- [ ] **U-23** Time-of-day indicator is displayed and updates
- [ ] **U-24** "Menu" button is always visible
- [ ] **U-25** Menu overlay shows all 6 options from FA-39
- [ ] **U-26** "Neuen Bewohner erstellen" is disabled when 4 residents exist

---

## Phase 8: Player Input & Tutorial

### T-19 -- InputHandler

**Test class:** `input/InputHandlerTest.java`

- [ ] **I-01** Player can give an item to a resident (item appears in resident's inventory)
- [ ] **I-02** Player can force two residents to interact
- [ ] **I-03** Player can add a new resident (up to max capacity)
- [ ] **I-04** Adding a 5th resident is rejected with appropriate feedback
- [ ] **I-05** Input processing does not block the game loop (runs in separate thread)

### T-20 -- TutorialOverlay

**Test class:** `ui/TutorialOverlayTest.java`

- [ ] **I-10** Tutorial displays on first game start (no save file)
- [ ] **I-11** Tutorial does not display when a save file exists
- [ ] **I-12** Tutorial walks through introducing Nina and Victoria
- [ ] **I-13** Tutorial can be skipped
- [ ] **I-14** After tutorial completion, it does not repeat on subsequent launches

---

## Phase 9: Integration & Predefined Content

### T-21/T-22 -- Nina, Victoria, Full Wiring

**Test class:** `integration/IntegrationTest.java`

- [ ] **G-01** Game starts with Nina and Victoria present on the island
- [ ] **G-02** Nina has correct appearance: short blonde hair, blue eyes, light skin
- [ ] **G-03** Victoria has correct appearance: long brown hair, blue eyes, light skin
- [ ] **G-04** Their relationship starts at 0
- [ ] **G-05** All threads (GameClock, EventManager, ResidentAI x2, InputHandler, AutoSave) start successfully
- [ ] **G-06** Graceful shutdown: all threads terminate within 5 seconds after shutdown signal
- [ ] **G-07** Full loop: start -> gameplay runs for 30 seconds -> auto-save triggers -> shut down -> reload save -> state matches

---

## Subagent Compliance Rules

1. **Every subagent MUST create the test class(es) listed for its task(s).**
2. **Every subagent MUST run `mvn test -pl . -Dtest=<TestClassName>` and confirm all tests pass.**
3. **A subagent may NOT report completion if any test fails.**
4. **If a test fails, the subagent must fix the implementation, re-run the test, and iterate until green.**
5. **Concurrency tests (marked with "Concurrency test") must run at least 5 times to check for flaky race conditions.** Use Maven Surefire `rerunFailingTestsCount` or a loop.
6. **Test output (pass/fail summary) must be included in the subagent's return message.**

### Running Tests

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=BathroomResourceTest

# Run a specific test method
mvn test -Dtest=BathroomResourceTest#testMutualExclusion

# Run concurrency tests multiple times (bash loop)
for i in {1..5}; do mvn test -Dtest=BathroomResourceTest#testConcurrentAccess; done

# PowerShell equivalent
1..5 | ForEach-Object { mvn test "-Dtest=BathroomResourceTest#testConcurrentAccess" }
```

### Test Dependencies (in pom.xml)

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.4</version>
    <scope>test</scope>
</dependency>
```
