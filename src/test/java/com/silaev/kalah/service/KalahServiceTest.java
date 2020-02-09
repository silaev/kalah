package com.silaev.kalah.service;

import com.silaev.kalah.converter.KalahGameStateDtoConverter;
import com.silaev.kalah.dao.KalahDao;
import com.silaev.kalah.dao.KalahDaoImpl;
import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.model.Cell;
import com.silaev.kalah.model.CellType;
import com.silaev.kalah.model.GameStatus;
import com.silaev.kalah.model.KalahGameState;
import com.silaev.kalah.model.Player;
import com.silaev.kalah.service.impl.KalahServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Konstantin Silaev on 2/4/2020
 */
class KalahServiceTest {
    @Test
    void shouldTestAcquiringOpponentPit() {
        //GIVEN
        final KalahDao kalahDao = new KalahDaoImpl();
        final KalahGameStateDtoConverter kalahGameStateDtoConverter = new KalahGameStateDtoConverter();
        final KalahService gameService = new KalahServiceImpl(kalahDao, kalahGameStateDtoConverter);
        final int gameId = gameService.createNewGame();

        //WHEN
        gameService.makeMove(gameId, 1, Player.A);
        gameService.makeMove(gameId, 2, Player.A);
        gameService.makeMove(gameId, 9, Player.B);
        gameService.makeMove(gameId, 3, Player.A);
        gameService.makeMove(gameId, 8, Player.B);
        gameService.makeMove(gameId, 4, Player.A);
        gameService.makeMove(gameId, 9, Player.B);
        final KalahGameStateDto kalahGameStateDto =
            gameService.makeMove(gameId, 6, Player.A);
        final Cell[] cells = kalahGameStateDto.getCells();

        //WHEN
        assertEquals(3, cells[0].getStoneCount());
        assertEquals(3, cells[1].getStoneCount());
        assertEquals(0, cells[2].getStoneCount());
        assertEquals(0, cells[3].getStoneCount());
        assertEquals(10, cells[4].getStoneCount());
        assertEquals(0, cells[5].getStoneCount());
        assertEquals(2, cells[7].getStoneCount());
        assertEquals(1, cells[8].getStoneCount());
        assertEquals(12, cells[9].getStoneCount());
        assertEquals(0, cells[10].getStoneCount());
        assertEquals(11, cells[11].getStoneCount());
        assertEquals(10, cells[12].getStoneCount());
        assertEquals(2, cells[13].getStoneCount());

        assertEquals(Player.B, kalahGameStateDto.getPlayerToMakeMove());
        assertEquals(GameStatus.IN_PROGRESS, kalahGameStateDto.getStatus());
    }

    @Test
    void shouldTestLastMove() {
        //GIVEN
        final KalahDao kalahDao = mock(KalahDao.class);
        final KalahGameStateDtoConverter kalahGameStateDtoConverter = new KalahGameStateDtoConverter();
        final KalahService gameService = new KalahServiceImpl(kalahDao, kalahGameStateDtoConverter);
        MockitoAnnotations.initMocks(this);
        final int gameId = 1;
        when(kalahDao.getById(gameId)).thenReturn(getInitializedMockStore());

        //WHEN
        final KalahGameStateDto kalahGameStateDto = gameService.makeMove(gameId, 13, Player.B);
        final Cell[] cells = kalahGameStateDto.getCells();

        //WHEN
        assertEquals(1, cells[0].getStoneCount());
        assertEquals(3, cells[1].getStoneCount());
        assertEquals(4, cells[2].getStoneCount());
        assertEquals(3, cells[3].getStoneCount());
        assertEquals(7, cells[4].getStoneCount());
        assertEquals(1, cells[5].getStoneCount());
        assertEquals(26, cells[6].getStoneCount());
        assertEquals(0, cells[7].getStoneCount());
        assertEquals(0, cells[8].getStoneCount());
        assertEquals(0, cells[9].getStoneCount());
        assertEquals(0, cells[10].getStoneCount());
        assertEquals(0, cells[11].getStoneCount());
        assertEquals(0, cells[12].getStoneCount());
        assertEquals(27, cells[13].getStoneCount());
        assertNull(kalahGameStateDto.getPlayerToMakeMove());
        assertEquals(GameStatus.WON_BY_PLAYER_A, kalahGameStateDto.getStatus());
    }

    @Test
    void shouldThrowExceptionBecauseCannotFindGame() {
        //GIVEN
        final KalahDao kalahDao = mock(KalahDao.class);
        final KalahGameStateDtoConverter kalahGameStateDtoConverter = mock(KalahGameStateDtoConverter.class);
        final KalahService gameService = new KalahServiceImpl(kalahDao, kalahGameStateDtoConverter);
        MockitoAnnotations.initMocks(this);
        final int gameId = 1;


        //WHEN
        final Executable executable =
            () -> gameService.makeMove(gameId, 13, Player.B);

        //THEN
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void shouldThrowExceptionBecauseGameIsLocked() {
        //GIVEN
        final KalahDao kalahDao = mock(KalahDao.class);
        final KalahGameStateDtoConverter kalahGameStateDtoConverter = mock(KalahGameStateDtoConverter.class);
        final KalahService gameService = new KalahServiceImpl(kalahDao, kalahGameStateDtoConverter);
        MockitoAnnotations.initMocks(this);
        final int gameId = 1;
        final KalahGameState kalahGameState = mock(KalahGameState.class);
        when(kalahDao.getById(gameId)).thenReturn(kalahGameState);
        final ReentrantLock lock = mock(ReentrantLock.class);
        when(kalahGameState.getLock()).thenReturn(lock);
        when(lock.tryLock()).thenReturn(false);

        //WHEN
        final Executable executable =
            () -> gameService.makeMove(gameId, 13, Player.B);

        //THEN
        assertThrows(IllegalStateException.class, executable);
    }

    private KalahGameState getInitializedMockStore() {
        final Cell[] cells = new Cell[KalahServiceImpl.CELL_TOTAL];
        cells[0] = mockPitCell(1, 0);
        cells[1] = mockPitCell(2, 2);
        cells[2] = mockPitCell(3, 3);
        cells[3] = mockPitCell(4, 2);
        cells[4] = mockPitCell(5, 6);
        cells[5] = mockPitCell(6, 0);
        cells[6] = mockPitCell(7, 26);
        cells[7] = mockPitCell(8, 0);
        cells[8] = mockPitCell(9, 0);
        cells[9] = mockPitCell(10, 0);
        cells[10] = mockPitCell(11, 0);
        cells[11] = mockPitCell(12, 0);
        cells[12] = mockPitCell(13, 7);
        cells[13] = mockPitCell(14, 26);
        return KalahGameState.builder()
            .cells(cells)
            .playerToMakeMove(Player.B)
            .status(GameStatus.IN_PROGRESS)
            .build();
    }

    private Cell mockPitCell(
        final int i,
        final int stoneCount
    ) {
        return Cell.builder()
            .id(i)
            .stoneCount(stoneCount)
            .cellType(CellType.PIT)
            .player(i <= 7 ? Player.A : Player.B)
            .build();
    }
}
