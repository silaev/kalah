package com.silaev.kalah.converter;

import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.model.Cell;
import com.silaev.kalah.model.KalahGameState;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
@Component
public class KalahGameStateDtoConverter implements Converter<KalahGameState, KalahGameStateDto> {
    @Override
    public KalahGameStateDto convert(KalahGameState source) {
        final Map<Integer, Integer> statuses = Stream.of(source.getCells())
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toMap(
                        Cell::getId,
                        Cell::getStoneCount,
                        (Integer k, Integer v) -> {
                            throw new IllegalStateException(String.format("Collision among key: %d", k));
                        },
                        LinkedHashMap::new

                    ),
                    Collections::unmodifiableMap
                )
            );

        return KalahGameStateDto.builder()
            .playerToMakeMove(source.getPlayerToMakeMove())
            .status(source.getStatus())
            .statuses(statuses)
            .build();
    }
}
