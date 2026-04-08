# Pals and Palms

A Tomodachi Life-inspired 2D multithreaded island life simulation game built with JavaFX.

## Prerequisites

- **Java 21** or newer ([Adoptium](https://adoptium.net/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.9+**

### Installing prerequisites

**macOS (Homebrew):**

```bash
brew install openjdk maven
```

**Windows (winget):**

```powershell
winget install EclipseAdoptium.Temurin.21.JDK
winget install Apache.Maven
```

**Linux (apt):**

```bash
sudo apt install openjdk-21-jdk maven
```

Verify both are available:

```bash
java --version
mvn --version
```

## Building and running

```bash
# Clone the repository
git clone <repo-url>
cd PalsAndPalms

# Compile the project
mvn compile

# Run tests
mvn test

# Launch the game
mvn javafx:run
```

## How to play

### Controls

- **Drag & drop** residents onto furniture or other residents to trigger actions
- **Click** a resident to inspect their stats
- **ESC** opens the game menu (or closes an active overlay/conversation)

### Rooms

- **Park (Island)** -- the outdoor area where residents wander freely
- **Apartment** -- the main indoor room with furniture
- **Bathroom** -- single-occupancy room for hygiene

### Furniture interactions (drag a resident onto...)

| Furniture   | Effect                  |
|-------------|-------------------------|
| Kitchen     | Fills hunger to 100%    |
| Sofa        | Resets tiredness to 0%  |
| Toilet      | Enters bathroom, restores hygiene to 100% |

### Resident interactions

- **Drag one resident onto another** to start a conversation
- Click to advance the dialogue (speakers alternate)
- Conversations end automatically after 2-5 messages
- Each message recovers 10% mood for the speaking resident
- Press **ESC** to exit a conversation early

### Room transitions

- Residents can **only leave the apartment** through the door on the left side (they must be in the leftmost 10% of the room)
- Residents can **only enter the apartment** from the island when near the house (center of the island)
- After exiting the bathroom, residents reappear near the bathroom door (right side of the apartment)

### AI behavior

- Each resident runs on their own thread and acts autonomously based on needs and time of day
- When hungry, they head to the apartment to eat from the fridge
- At night, they get tired and go home to rest
- There is a 15-second grace period after any room switch before the AI will move a resident again
- Player drag actions always override AI decisions and are never blocked by the cooldown

### Saving

- The game auto-saves every 30 seconds
- You can also save manually from the game menu (ESC)

## Project structure

```
src/main/java/com/palsandpalms/
  engine/          -- Game loop, AI, clock, interactions
  input/           -- Player command handling
  model/           -- Data models (Resident, Room, Island, etc.)
  persistence/     -- Save/load system (JSON via Gson)
  ui/              -- JavaFX views, components, assets
  world/           -- World resources (fridge, bathroom, events)
```

## Tech stack

- **Java 21** with module system (JPMS)
- **JavaFX 21** for rendering
- **Gson** for JSON save/load
- **JUnit 5** for testing
- **Maven** for build management
