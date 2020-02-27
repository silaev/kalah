package com.silaev.kalah.converter.impl;

import com.silaev.kalah.converter.KalahGameStateDtoConverter;
import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.model.KalahGameState;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
@Component
public class KalahGameStateDtoConverterImpl implements KalahGameStateDtoConverter {
    public KalahGameStateDto convert(
        final KalahGameState kalahGameState, final Map<Integer, Integer> statuses
    ) {
        return KalahGameStateDto.builder()
            .playerToMakeMove(kalahGameState.getPlayerToMakeMove())
            .gameStatus(kalahGameState.getStatus())
            .statuses(statuses)
            .build();
    }
}
