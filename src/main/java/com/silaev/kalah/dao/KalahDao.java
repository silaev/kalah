package com.silaev.kalah.dao;

import com.silaev.kalah.model.Cell;
import com.silaev.kalah.model.KalahGameState;

import java.util.Map;

/**
 * @author Konstantin Silaev on 2/4/2020
 */
public interface KalahDao {
    int create(KalahGameState initializedStore, Cell[] cells);

    KalahGameState getGameStateById(int gameId);

    Cell getCellByGameIdAndCellIndex(int gameId, int oppositeCellIndex);

    Map<Integer, Integer> getCellsIndexByGameIdAndCellIndex(int gameId);
}
