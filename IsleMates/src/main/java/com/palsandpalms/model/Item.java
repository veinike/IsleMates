package com.palsandpalms.model;

import java.util.Objects;
import java.util.UUID;

public final class Item {
    private final ItemType type;
    private final UUID id;

    public Item(ItemType type) {
        this(type, UUID.randomUUID());
    }

    public Item(ItemType type, UUID id) {
        this.type = Objects.requireNonNull(type);
        this.id = Objects.requireNonNull(id);
    }

    public ItemType getType() {
        return type;
    }

    public UUID getId() {
        return id;
    }
}
