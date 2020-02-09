package com.silaev.kalah.converter;

import com.silaev.kalah.dto.CreationResponseDto;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
public interface CreationResponseDtoConverter {
    CreationResponseDto convert(int gameId, String url);
}
