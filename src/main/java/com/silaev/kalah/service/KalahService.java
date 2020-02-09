package com.silaev.kalah.service;

import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.model.Player;

/**
 * @author Konstantin Silaev on 2/4/2020
 */
public interface KalahService {
    int createNewGame();

    KalahGameStateDto makeMove(final int gameId, final int pitId, final Player player);
}

