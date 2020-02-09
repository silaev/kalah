package com.silaev.kalah.model;

import java.util.Objects;

/**
 * @author Konstantin Silaev on 2/4/2020
 */
public enum Player {
    A, B;

    public static Player byName(String name) {
        Objects.requireNonNull(name);

        for (Player player : values()) {
            if (player.name().equalsIgnoreCase(name)) {
                return player;
            }
        }
        throw new IllegalArgumentException(String.format(
            "Cannot find player by %s", name));
    }
}
