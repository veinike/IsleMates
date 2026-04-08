# IsleMates

A Tomodachi Life-inspired 2D multithreaded island life simulation game built with JavaFX.

## Where to find

IsleMates\IsleMates\target\installer\IsleMates\IsleMates.exe"

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
| Kitchen     | Fills hunger            |
| Sofa        | Resets tiredness        |
| Toilet      | Enters bathroom, restores hygiene |

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
