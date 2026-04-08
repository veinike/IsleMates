package com.palsandpalms.input;

import com.palsandpalms.model.InteractionType;
import com.palsandpalms.model.ItemType;

import java.util.Optional;
import java.util.UUID;

public sealed interface PlayerCommand {
    record GiveItem(UUID residentId, ItemType itemType) implements PlayerCommand {}
    record ForceInteraction(UUID a, UUID b, InteractionType type) implements PlayerCommand {}
    record AddResidentRequest() implements PlayerCommand {}
}
