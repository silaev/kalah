package com.silaev.kalah.converter;

import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.dto.MoveResponseDto;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
public interface MoveResponseDtoConverter {
    MoveResponseDto convert(
        final Integer gameId,
        final String url,
        final KalahGameStateDto kalahGameStateDto
    );
}
