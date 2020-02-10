package com.silaev.kalah.converter.impl;

import com.silaev.kalah.converter.MoveResponseDtoConverter;
import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.dto.MoveResponseDto;
import org.springframework.stereotype.Component;

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
        return MoveResponseDto.builder()
            .id(gameId)
            .url(url)
            .statuses(kalahGameStateDto.getStatuses())
            .build();
    }
}
