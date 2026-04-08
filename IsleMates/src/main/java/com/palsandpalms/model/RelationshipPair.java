package com.palsandpalms.model;

import java.util.Objects;
import java.util.UUID;

/** Canonical unordered pair of resident IDs. */
public record RelationshipPair(UUID a, UUID b) {
    public RelationshipPair {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        if (a.equals(b)) {
            throw new IllegalArgumentException("same resident");
        }
        if (a.compareTo(b) > 0) {
            UUID t = a;
            a = b;
            b = t;
        }
    }

    public static RelationshipPair of(UUID x, UUID y) {
        return new RelationshipPair(x, y);
    }
}
