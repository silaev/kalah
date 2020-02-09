package com.silaev.kalah.controller;

import com.silaev.kalah.converter.CreationResponseDtoConverter;
import com.silaev.kalah.converter.MoveResponseDtoConverter;
import com.silaev.kalah.dto.CreationResponseDto;
import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.dto.MoveResponseDto;
import com.silaev.kalah.model.Player;
import com.silaev.kalah.service.KalahService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
@ExtendWith(MockitoExtension.class)
class KalahControllerTest {
    @Mock
    private MoveResponseDtoConverter moveDtoConverter;
    @Mock
    private KalahService kalahService;
    @Mock
    private CreationResponseDtoConverter responseDtoConverter;
    @InjectMocks
    private KalahController kalahController;

    @Test
    void shouldTestCreateNewGame() {
        //GIVEN
        final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        final String url = "http://localhost:8080/games";
        final int gameId = 1;
        final CreationResponseDto responseDto = mock(CreationResponseDto.class);
        final StringBuffer stringBuffer = mock(StringBuffer.class);
        when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
        when(stringBuffer.toString()).thenReturn(url);
        when(kalahService.createNewGame()).thenReturn(gameId);
        when(responseDtoConverter.convert(gameId, url)).thenReturn(responseDto);

        //WHEN
        final ResponseEntity<CreationResponseDto> actualResponseEntity =
            kalahController.createNewGame(httpServletRequest);

        //THEN
        assertNotNull(actualResponseEntity);
        assertEquals(HttpStatus.OK, actualResponseEntity.getStatusCode());
        verify(kalahService, times(1)).createNewGame();
        verify(responseDtoConverter, times(1)).convert(gameId, url);
        assertEquals(responseDto, actualResponseEntity.getBody());
    }

    @Test
    void shouldTestMakeMove() {
        //GIVEN
        final int pitId = 1;
        final int gameId = -441249097;
        final String url = String.format("http://localhost:8080/games/%d/pits/%d", gameId, pitId);
        final Player player = Player.A;
        final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        final KalahGameStateDto kalahGameStateDto = mock(KalahGameStateDto.class);
        when(kalahService.makeMove(gameId, pitId, player)).thenReturn(kalahGameStateDto);
        final StringBuffer stringBuffer = mock(StringBuffer.class);
        when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
        when(stringBuffer.toString()).thenReturn(url);
        final MoveResponseDto moveResponseDto = mock(MoveResponseDto.class);
        when(moveDtoConverter.convert(gameId, url, kalahGameStateDto))
            .thenReturn(moveResponseDto);

        //WHEN
        final ResponseEntity<MoveResponseDto> actualResponseEntity =
            kalahController.makeMove(gameId, pitId, player, httpServletRequest);

        //THEN
        assertNotNull(actualResponseEntity);
        assertEquals(moveResponseDto, actualResponseEntity.getBody());
        assertEquals(HttpStatus.OK, actualResponseEntity.getStatusCode());
        verify(kalahService, times(1)).makeMove(gameId, pitId, player);
        verify(moveDtoConverter, times(1)).convert(gameId, url, kalahGameStateDto);
    }
}
