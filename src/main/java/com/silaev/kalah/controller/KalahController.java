package com.silaev.kalah.controller;

import com.silaev.kalah.converter.CreationResponseDtoConverter;
import com.silaev.kalah.converter.MoveResponseDtoConverter;
import com.silaev.kalah.converter.StringToPlayerConverter;
import com.silaev.kalah.dto.CreationResponseDto;
import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.dto.MoveResponseDto;
import com.silaev.kalah.model.Player;
import com.silaev.kalah.service.KalahService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/games")
public class KalahController {
    private final KalahService kalahService;
    private final CreationResponseDtoConverter responseDtoConverter;
    private final MoveResponseDtoConverter moveDtoConverter;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(Player.class, new StringToPlayerConverter());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreationResponseDto> createNewGame(
        HttpServletRequest request
    ) {
        log.debug("createNewGame");

        final int gameId = kalahService.createNewGame();
        final String url = request.getRequestURL().toString();
        return ResponseEntity.ok(responseDtoConverter.convert(gameId, url));
    }

    @PutMapping(value = "/{gameId}/pits/{pitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoveResponseDto> makeMove(
        @PathVariable("gameId") @NotNull Integer gameId,
        @PathVariable("pitId") @NotNull Integer pitId,
        @RequestHeader("player") Player player,
        HttpServletRequest request
    ) {
        log.debug("makeMove gameId:{}, pitId: {}, player:{}", gameId, pitId, player);

        final KalahGameStateDto kalahGameStateDto = kalahService.makeMove(gameId, pitId, player);
        final String url = request.getRequestURL().toString();
        return ResponseEntity.ok(moveDtoConverter.convert(gameId, url, kalahGameStateDto));
    }
}
