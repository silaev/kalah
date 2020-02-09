package com.silaev.kalah.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Konstantin Silaev on 2/4/2020
 */
@Builder(toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode(exclude = {"stoneCount"})
public final class Cell {
    private final int id;
    private final CellType cellType;
    private final Player player;
    @Setter
    private volatile int stoneCount;
}
