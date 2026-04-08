# Pals & Palms -- Graphical Assets Required

This document lists all 2D graphical assets that must be created manually for the game.
All assets should be delivered as **PNG files with transparency** unless otherwise noted.
The recommended art style is **pixel art / chibi** consistent with Tomodachi Life aesthetics.

---

## 1. Island & Room Backgrounds

Each room is displayed as a full-screen background when the player views it.
Recommended resolution: **800x600 px** (or matching your chosen game window size).

| Asset ID | Filename | Description |
|----------|----------|-------------|
| BG-01 | `bg_island_overview.png` | Top-down or isometric overview of the island showing all room entry points |
| BG-02 | `bg_park.png` | Park background (grass, trees, benches, open sky) |
| BG-03 | `bg_mall.png` | Shopping center interior (shelves, counter, items on display) |
| BG-04 | `bg_bathroom.png` | Bathroom interior (sink, shower/tub, mirror, tiles) |
| BG-05 | `bg_apartment.png` | Apartment/living room interior (bed, couch, table, window) |

### Time-of-Day Overlays

These are semi-transparent overlays applied on top of room backgrounds to tint the scene.

| Asset ID | Filename | Description |
|----------|----------|-------------|
| OV-01 | `overlay_morning.png` | Warm golden tint (sunrise feel) |
| OV-02 | `overlay_noon.png` | Bright, neutral (clear daylight) -- can be skipped if no tint desired |
| OV-03 | `overlay_evening.png` | Orange/amber tint (sunset feel) |
| OV-04 | `overlay_night.png` | Dark blue/purple tint with reduced brightness |

---

## 2. Resident Sprites

Residents are composed from modular parts so that custom characters (FA-35) can be assembled dynamically.
Each part should be drawn on a **transparent canvas of 64x128 px** (or a consistent sprite size).

### 2.1 Base Bodies

| Asset ID | Filename | Description |
|----------|----------|-------------|
| BODY-01 | `body_light.png` | Base body with light skin |
| BODY-02 | `body_medium.png` | Base body with medium skin |
| BODY-03 | `body_dark.png` | Base body with dark skin |

Provide both **male** and **female** variants for each (suffix `_m` / `_f`), e.g. `body_light_f.png`.

### 2.2 Hair Styles

Combinations of length and color. Draw each on transparent background, positioned to overlay the head of the base body.

**Lengths:** short, long
**Colors:** blonde, brown, black, red

| Asset ID | Filename Pattern | Count |
|----------|-----------------|-------|
| HAIR-XX | `hair_{length}_{color}.png` | 8 total (2 lengths x 4 colors) |

Examples: `hair_short_blonde.png`, `hair_long_brown.png`, etc.

### 2.3 Eyes

Drawn to overlay the face region of the base body.

**Colors:** blue, green, brown

| Asset ID | Filename Pattern | Count |
|----------|-----------------|-------|
| EYES-XX | `eyes_{color}.png` | 3 total |

Examples: `eyes_blue.png`, `eyes_green.png`, `eyes_brown.png`

### 2.4 Predefined Characters (Optional Shortcut)

If you prefer pre-composed sprites instead of modular assembly for the two starter characters:

| Asset ID | Filename | Description |
|----------|----------|-------------|
| CHAR-01 | `nina_idle.png` | Nina: short blonde hair, blue eyes, light skin |
| CHAR-02 | `victoria_idle.png` | Victoria: long brown hair, blue eyes, light skin |

### 2.5 Resident Animation States

For each assembled resident, the following poses/states are needed.
Can be single frames (static poses) or 2-3 frame sprite sheets for simple animation.

| Asset ID | Filename Suffix | Description |
|----------|----------------|-------------|
| ANIM-01 | `_idle.png` | Standing still, default pose |
| ANIM-02 | `_walk_left.png` | Walking left (2-3 frames as sprite sheet) |
| ANIM-03 | `_walk_right.png` | Walking right (2-3 frames as sprite sheet) |
| ANIM-04 | `_sleeping.png` | Sleeping pose (eyes closed, lying down or "Zzz") |
| ANIM-05 | `_eating.png` | Eating pose (holding food item) |
| ANIM-06 | `_bathing.png` | Bathing/washing pose |
| ANIM-07 | `_talking.png` | Talking to another resident (speech bubble or gesture) |
| ANIM-08 | `_arguing.png` | Arguing pose (angry expression, arms gesture) |
| ANIM-09 | `_happy.png` | Happy reaction (for receiving gifts, friendship formed) |

**Note:** At minimum, `_idle` is mandatory. Other states can initially use `_idle` as a fallback and be added incrementally.

---

## 3. UI Elements

### 3.1 Homescreen

| Asset ID | Filename | Description |
|----------|----------|-------------|
| UI-01 | `homescreen_bg.png` | Homescreen background (island panorama or decorative art) |
| UI-02 | `title_islemates.png` | Game title "IsleMates" in decorative, colorful font (FA-28) |
| UI-03 | `btn_start.png` | "Spiel starten" button |
| UI-04 | `btn_continue.png` | "Spiel fortsetzen" button |
| UI-05 | `btn_newgame.png` | "Neues Spiel starten" button |
| UI-06 | `btn_rules.png` | "Regeln lesen" button |

### 3.2 In-Game HUD

| Asset ID | Filename | Description |
|----------|----------|-------------|
| UI-10 | `icon_hunger.png` | Hunger status icon (e.g. fork/plate) -- 24x24 px |
| UI-11 | `icon_tiredness.png` | Tiredness status icon (e.g. moon/pillow) -- 24x24 px |
| UI-12 | `icon_mood.png` | Mood status icon (e.g. smiley face) -- 24x24 px |
| UI-13 | `icon_hygiene.png` | Hygiene status icon (e.g. water drop/soap) -- 24x24 px |
| UI-14 | `statusbar_fill.png` | Status bar fill texture (can be a 1px wide gradient to stretch) |
| UI-15 | `statusbar_bg.png` | Status bar background/frame |
| UI-16 | `btn_menu.png` | "Menu" button always visible during gameplay (FA-38) |
| UI-17 | `icon_time_morning.png` | Time-of-day indicator: morning (sun rising) |
| UI-18 | `icon_time_noon.png` | Time-of-day indicator: noon (full sun) |
| UI-19 | `icon_time_evening.png` | Time-of-day indicator: evening (sunset) |
| UI-20 | `icon_time_night.png` | Time-of-day indicator: night (moon/stars) |

### 3.3 In-Game Menu Overlay

| Asset ID | Filename | Description |
|----------|----------|-------------|
| UI-30 | `menu_panel_bg.png` | Menu overlay background panel (semi-transparent) |
| UI-31 | `btn_create_resident.png` | "Neuen Bewohner erstellen" button |
| UI-32 | `btn_create_resident_disabled.png` | Disabled variant (grayed out, for when 4 residents exist) |
| UI-33 | `btn_inventory.png` | "Inventar aufrufen" button |
| UI-34 | `btn_show_rules.png` | "Spielregeln anzeigen" button |
| UI-35 | `btn_save.png` | "Spielstand manuell speichern" button |
| UI-36 | `btn_back_island.png` | "Zuruck zur Insel" button |
| UI-37 | `btn_back_home.png` | "Zuruck zum Homescreen" button |

### 3.4 Character Creation Screen

| Asset ID | Filename | Description |
|----------|----------|-------------|
| UI-40 | `creation_bg.png` | Character creation screen background |
| UI-41 | `btn_back.png` | Generic "Zuruck" (back) button, reusable across screens |
| UI-42 | `btn_confirm.png` | "Bestatigen" (confirm) button for finalizing character |
| UI-43 | `preview_frame.png` | Frame/border for the character preview area |

### 3.5 Tutorial Overlay

| Asset ID | Filename | Description |
|----------|----------|-------------|
| UI-50 | `tutorial_panel.png` | Tutorial text panel background |
| UI-51 | `tutorial_arrow.png` | Pointing arrow/highlight for guiding the player |
| UI-52 | `btn_next.png` | "Weiter" (next) button for tutorial steps |
| UI-53 | `btn_skip.png` | "Uberspringen" (skip) button |

---

## 4. Item Sprites

Items that can be stored in inventory or given as gifts. Recommended size: **32x32 px**.

| Asset ID | Filename | Description |
|----------|----------|-------------|
| ITEM-01 | `item_apple.png` | Apple (food item from fridge) |
| ITEM-02 | `item_sandwich.png` | Sandwich (food item) |
| ITEM-03 | `item_cake.png` | Cake (birthday event item) |
| ITEM-04 | `item_flower.png` | Flower (gift item) |
| ITEM-05 | `item_book.png` | Book (gift item) |
| ITEM-06 | `item_toy.png` | Toy/stuffed animal (gift item) |

Add more items as desired; these are the minimum set to cover fridge food and gift-giving mechanics.

---

## 5. Event Graphics

Visual indicators or scene decorations for global events.

| Asset ID | Filename | Description |
|----------|----------|-------------|
| EVT-01 | `event_concert_banner.png` | Concert stage/banner overlay for the park |
| EVT-02 | `event_rain_overlay.png` | Rain effect overlay (semi-transparent, tiled or full-screen) |
| EVT-03 | `event_delivery_truck.png` | Delivery truck sprite or icon for supermarket delivery |
| EVT-04 | `event_birthday_decoration.png` | Birthday balloons/confetti decoration overlay |
| EVT-05 | `event_notification.png` | Small popup/toast background for event announcements |

---

## 6. Interaction Bubbles

Small speech/thought bubbles shown above residents during interactions.

| Asset ID | Filename | Description |
|----------|----------|-------------|
| BUBBLE-01 | `bubble_speech.png` | Speech bubble (for conversations) |
| BUBBLE-02 | `bubble_angry.png` | Angry bubble with jagged edges (for arguments) |
| BUBBLE-03 | `bubble_heart.png` | Heart bubble (friendship formed) |
| BUBBLE-04 | `bubble_gift.png` | Gift icon in bubble (giving a present) |
| BUBBLE-05 | `bubble_zzz.png` | Sleep bubble ("Zzz") |

---

## Asset Delivery Notes

- **File format:** PNG with transparency (32-bit RGBA)
- **Naming convention:** All lowercase, underscores for spaces, as shown in filenames above
- **Directory structure:** Place all assets under `src/main/resources/assets/` with subdirectories:
  ```
  assets/
    backgrounds/     (BG-*, OV-*)
    sprites/
      bodies/        (BODY-*)
      hair/          (HAIR-*)
      eyes/          (EYES-*)
      animations/    (ANIM-*)
      characters/    (CHAR-*)
    ui/              (UI-*)
    items/           (ITEM-*)
    events/          (EVT-*)
    bubbles/         (BUBBLE-*)
  ```
- **Priority order:** Start with BG-01 (island overview), BODY/HAIR/EYES for character assembly, UI-01 through UI-06 (homescreen), and ANIM-01 (idle). Everything else can be added iteratively.

---

## Summary Count

| Category | Asset Count |
|----------|-------------|
| Backgrounds | 5 |
| Time overlays | 4 |
| Body sprites | 6 (3 skins x 2 genders) |
| Hair sprites | 8 (2 lengths x 4 colors) |
| Eye sprites | 3 |
| Pre-composed characters | 2 (optional) |
| Animation states | 9 per character (1 mandatory, 8 optional) |
| UI homescreen | 6 |
| UI HUD | 11 |
| UI menu | 8 |
| UI creation | 4 |
| UI tutorial | 4 |
| Items | 6+ |
| Events | 5 |
| Interaction bubbles | 5 |

**Minimum viable set (to get the game running):** ~30 assets (backgrounds, one body+hair+eyes combo, homescreen UI, HUD icons, idle animation)
