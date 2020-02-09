package com.silaev.kalah.converter;

import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.model.Cell;
import com.silaev.kalah.model.KalahGameState;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
@Component
public class KalahGameStateDtoConverter implements Converter<KalahGameState, KalahGameStateDto> {
    @Override
    public KalahGameStateDto convert(KalahGameState source) {
        final Cell[] cells = Arrays.stream(source.getCells())
            .map(c -> c.toBuilder().build())
            .toArray(Cell[]::new);

        return KalahGameStateDto.builder()
            .playerToMakeMove(source.getPlayerToMakeMove())
            .status(source.getStatus())
            .cells(cells)
            .build();
    }
}
