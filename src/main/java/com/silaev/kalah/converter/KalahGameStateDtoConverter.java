package com.silaev.kalah.converter;

import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.model.KalahGameState;

import java.util.Map;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
public interface KalahGameStateDtoConverter {
    KalahGameStateDto convert(
        final KalahGameState kalahGameState,
        final Map<Integer, Integer> statuses
    );
}
