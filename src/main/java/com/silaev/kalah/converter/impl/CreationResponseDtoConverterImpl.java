package com.silaev.kalah.converter.impl;

import com.silaev.kalah.converter.CreationResponseDtoConverter;
import com.silaev.kalah.dto.CreationResponseDto;
import org.springframework.stereotype.Component;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
@Component
public class CreationResponseDtoConverterImpl implements CreationResponseDtoConverter {
    public CreationResponseDto convert(int gameId, String url) {
        return CreationResponseDto.builder()
            .id(gameId)
            .url(url)
            .build();
    }
}
