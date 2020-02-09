package com.silaev.kalah.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Konstantin Silaev on 2/4/2020
 */
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class KalahGameState {
    private final Cell[] cells;
    @Builder.Default
    private final ReentrantLock lock = new ReentrantLock();
    @Setter
    private volatile Player playerToMakeMove;
    @Setter
    private volatile GameStatus status;
}
