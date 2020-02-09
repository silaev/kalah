package com.silaev.kalah.converter.impl;

import com.silaev.kalah.converter.MoveResponseDtoConverter;
import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.dto.MoveResponseDto;
import com.silaev.kalah.model.Cell;
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
public class MoveResponseDtoConverterImpl implements MoveResponseDtoConverter {
    public MoveResponseDto convert(
        final Integer gameId,
        final String url,
        final KalahGameStateDto kalahGameStateDto
    ) {
        final Map<Integer, Integer> statuses = Stream.of(kalahGameStateDto.getCells())
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

        return MoveResponseDto.builder()
            .id(gameId)
            .url(url)
            .statuses(statuses)
            .build();
    }
}
