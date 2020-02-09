package com.silaev.kalah.dao;

import com.silaev.kalah.model.KalahGameState;

/**
 * @author Konstantin Silaev on 2/4/2020
 */
public interface KalahDao {
    int create(KalahGameState initializedStore);

    KalahGameState getById(int gameId);
}
