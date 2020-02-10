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

import java.util.Map;
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
        final Map<Integer, Integer> statuses = kalahGameStateDto.getStatuses();

        //WHEN
        assertEquals(Integer.valueOf(3), statuses.get(1));
        assertEquals(Integer.valueOf(3), statuses.get(2));
        assertEquals(Integer.valueOf(0), statuses.get(3));
        assertEquals(Integer.valueOf(0), statuses.get(4));
        assertEquals(Integer.valueOf(10), statuses.get(5));
        assertEquals(Integer.valueOf(0), statuses.get(6));
        assertEquals(Integer.valueOf(18), statuses.get(7));
        assertEquals(Integer.valueOf(2), statuses.get(8));
        assertEquals(Integer.valueOf(1), statuses.get(9));
        assertEquals(Integer.valueOf(12), statuses.get(10));
        assertEquals(Integer.valueOf(0), statuses.get(11));
        assertEquals(Integer.valueOf(11), statuses.get(12));
        assertEquals(Integer.valueOf(10), statuses.get(13));
        assertEquals(Integer.valueOf(2), statuses.get(14));

        assertEquals(KalahServiceImpl.STONE_TOTAL, getTotal(statuses));
        assertEquals(Player.B, kalahGameStateDto.getPlayerToMakeMove());
        assertEquals(GameStatus.IN_PROGRESS, kalahGameStateDto.getStatus());
    }

    private int getTotal(Map<Integer, Integer> statuses) {
        return statuses.values().stream().mapToInt(c -> c).sum();
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
        final Map<Integer, Integer> statuses = kalahGameStateDto.getStatuses();
        //WHEN
        assertEquals(Integer.valueOf(1), statuses.get(1));
        assertEquals(Integer.valueOf(3), statuses.get(2));
        assertEquals(Integer.valueOf(4), statuses.get(3));
        assertEquals(Integer.valueOf(3), statuses.get(4));
        assertEquals(Integer.valueOf(7), statuses.get(5));
        assertEquals(Integer.valueOf(1), statuses.get(6));
        assertEquals(Integer.valueOf(26), statuses.get(7));
        assertEquals(Integer.valueOf(0), statuses.get(8));
        assertEquals(Integer.valueOf(0), statuses.get(9));
        assertEquals(Integer.valueOf(0), statuses.get(10));
        assertEquals(Integer.valueOf(0), statuses.get(11));
        assertEquals(Integer.valueOf(0), statuses.get(12));
        assertEquals(Integer.valueOf(0), statuses.get(13));
        assertEquals(Integer.valueOf(27), statuses.get(14));
        assertNull(kalahGameStateDto.getPlayerToMakeMove());
        assertEquals(KalahServiceImpl.STONE_TOTAL, getTotal(statuses));
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
