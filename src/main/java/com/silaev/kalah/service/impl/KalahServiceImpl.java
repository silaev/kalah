package com.silaev.kalah.service.impl;

import com.silaev.kalah.converter.KalahGameStateDtoConverter;
import com.silaev.kalah.dao.KalahDao;
import com.silaev.kalah.dto.KalahGameStateDto;
import com.silaev.kalah.model.Cell;
import com.silaev.kalah.model.CellType;
import com.silaev.kalah.model.GameStatus;
import com.silaev.kalah.model.KalahGameState;
import com.silaev.kalah.model.Player;
import com.silaev.kalah.service.KalahService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * @author Konstantin Silaev on 2/4/2020
 */
@Service
@RequiredArgsConstructor
public class KalahServiceImpl implements KalahService {
    public static final int CELL_TOTAL = 14;
    public static final int STONE_TOTAL = 72;
    private final KalahDao kalahDao;
    private final KalahGameStateDtoConverter kalahGameStateDtoConverter;

    @Override
    public int createNewGame() {
        return kalahDao.create(KalahGameState.builder().build(), getInitializedStore());
    }

    @Override
    public KalahGameStateDto makeMove(final int gameId, final int pitId, final Player player) {
        verifyStoreBounds(pitId);

        final KalahGameState kalahGameState = getKalahGameState(gameId);
        final int cellIndex = pitId - 1;
        final ReentrantLock lock = getLock(gameId, kalahGameState);

        if (!lock.tryLock()) {
            throw new IllegalStateException(
                String.format("Game: %d is busy. Please, try again later on", gameId));
        }

        try {
            final Cell cell = kalahDao.getCellByGameIdAndCellIndex(gameId, cellIndex);
            verifyGameStatus(kalahGameState.getStatus());
            verifyPlayer(player, cell, kalahGameState.getPlayerToMakeMove(), kalahGameState.getStatus());

            int stoneCounter = cell.getStoneCount();
            verifyCellToMove(cell.getCellType(), stoneCounter);
            int stoneCountTotalWithoutKalah = getCurrentPlayerStoneTotal(player, gameId);
            final boolean isLastMove = stoneCountTotalWithoutKalah == stoneCounter;
            cell.setStoneCount(0);

            int currentCellIndex = cell.getId() - 1;
            Player playerToMakeNextMove = player == Player.A ? Player.B : Player.A;
            while (stoneCounter > 0) {
                currentCellIndex++;

                if (currentCellIndex == CELL_TOTAL) {
                    currentCellIndex = 0;
                }
                final Cell currentCellEntity = kalahDao.getCellByGameIdAndCellIndex(gameId, currentCellIndex);

                if (isNotOpponentKalah(player, currentCellEntity)) {
                    stoneCounter--;

                    if (isLastCellEmptyAndBelongsToCurrentPlayer(player, currentCellEntity, stoneCounter)) {
                        playerToMakeNextMove =
                            processLastMove(player, gameId, currentCellIndex, playerToMakeNextMove, currentCellEntity);
                    } else {
                        currentCellEntity.setStoneCount(currentCellEntity.getStoneCount() + 1);
                    }
                }
            }

            final GameStatus status = getStatus(player, gameId, isLastMove);
            kalahGameState.setStatus(status);
            kalahGameState.setPlayerToMakeMove(status == GameStatus.IN_PROGRESS ? playerToMakeNextMove : null);

            return kalahGameStateDtoConverter.convert(
                kalahGameState,
                kalahDao.getCellsIndexByGameIdAndCellIndex(gameId)
            );
        } finally {
            lock.unlock();
        }
    }

    private Player processLastMove(
        final Player player,
        final int gameId,
        final int currentCellIndex,
        final Player playerToMakeNextMove,
        final Cell currentCellEntity
    ) {
        if (currentCellEntity.getCellType() == CellType.KALAH) {
            currentCellEntity.setStoneCount(currentCellEntity.getStoneCount() + 1);
            return player;
        } else {
            final int oppositeCellIndex = CELL_TOTAL - currentCellEntity.getId() - 1;
            final Cell oppositeCellEntity = kalahDao.getCellByGameIdAndCellIndex(gameId, oppositeCellIndex);
            final int oppositeCellStoneCount = oppositeCellEntity.getStoneCount();
            if (oppositeCellStoneCount > 0) {
                kalahDao.getCellByGameIdAndCellIndex(gameId, oppositeCellIndex).setStoneCount(0);
                final int playerKalahIndex = getPlayerKalah(currentCellIndex);
                kalahDao.getCellByGameIdAndCellIndex(gameId, playerKalahIndex).setStoneCount(
                    kalahDao.getCellByGameIdAndCellIndex(gameId, playerKalahIndex).getStoneCount() + oppositeCellStoneCount + 1
                );
            } else {
                currentCellEntity.setStoneCount(currentCellEntity.getStoneCount() + 1);
            }
        }
        return playerToMakeNextMove;
    }

    private KalahGameState getKalahGameState(int gameId) {
        return Optional.ofNullable(kalahDao.getGameStateById(gameId))
            .orElseThrow(() -> new IllegalArgumentException(
                    String.format("Cannot find a game by id: %d", gameId)
                )
            );
    }

    private ReentrantLock getLock(int gameId, KalahGameState kalahGameState) {
        return Optional.ofNullable(kalahGameState.getLock())
            .orElseThrow(
                () -> new IllegalStateException(String.format(
                    "Game: %d is supposed to have a lock", gameId
                ))
            );
    }

    private GameStatus getStatus(Player player, int gameId, boolean isLastMove) {
        GameStatus gameStatus;
        if (isLastMove) {
            int currentPlayerStoneCountOverall = kalahDao.getCellByGameIdAndCellIndex(gameId, player == Player.A ? 6 : 13).getStoneCount();
            int opponentPlayerStoneCountOverall = STONE_TOTAL - currentPlayerStoneCountOverall;
            if (currentPlayerStoneCountOverall == opponentPlayerStoneCountOverall) {
                gameStatus = GameStatus.DRAW;
            } else if (currentPlayerStoneCountOverall > opponentPlayerStoneCountOverall) {
                gameStatus = GameStatus.valueOf("WON_BY_PLAYER_" + player.name());
            } else {
                gameStatus = GameStatus.valueOf(
                    "WON_BY_PLAYER_" + (player == Player.A ? Player.B : Player.A).name()
                );
            }
        } else {
            gameStatus = GameStatus.IN_PROGRESS;
        }
        return gameStatus;
    }

    private int getCurrentPlayerStoneTotal(final Player player, int gameId) {
        int currentIndex = player == Player.A ? 0 : 7;
        int lastIndex = player == Player.A ? 5 : 12;

        return IntStream.rangeClosed(currentIndex, lastIndex)
            .mapToObj(i -> kalahDao.getCellByGameIdAndCellIndex(gameId, i))
            .mapToInt(Cell::getStoneCount)
            .sum();
    }

    private void verifyGameStatus(GameStatus status) {
        if (status == GameStatus.WON_BY_PLAYER_A || status == GameStatus.WON_BY_PLAYER_B || status == GameStatus.DRAW) {
            throw new IllegalStateException(
                String.format("This game is already over with a status: %s. " +
                        "Please, start a new one.",
                    status.name()
                )
            );
        }
    }

    private Cell[] getInitializedStore() {
        return IntStream.rangeClosed(1, CELL_TOTAL)
            .mapToObj(i -> (i % 7 == 0) ? getKalahCell(i) : getPitCell(i))
            .toArray(Cell[]::new);
    }

    private Cell getPitCell(int i) {
        return Cell.builder()
            .id(i)
            .stoneCount(6)
            .cellType(CellType.PIT)
            .player(getPlayer(i))
            .build();
    }

    private Player getPlayer(int i) {
        return i <= 7 ? Player.A : Player.B;
    }

    private Cell getKalahCell(int i) {
        return Cell.builder()
            .id(i)
            .stoneCount(0)
            .cellType(CellType.KALAH)
            .player(getPlayer(i))
            .build();
    }

    private void verifyStoreBounds(int i) {
        if (i < 1 || i > CELL_TOTAL) {
            throw new IllegalArgumentException(
                String.format("cell id: %d is out of range [1-%d]", i, CELL_TOTAL)
            );
        }
    }

    private int getPlayerKalah(int index) {
        if (index < 7) {
            return 6;
        } else if (index < CELL_TOTAL) {
            return 13;
        } else {
            throw new IllegalArgumentException(
                String.format("Cannot getCellByGameIdAndCellIndex a kalah cell of player by index: %d", index)
            );
        }
    }

    private boolean isLastCellEmptyAndBelongsToCurrentPlayer(Player player, Cell currentCellEntity, int stoneCount) {
        return stoneCount == 0
            && currentCellEntity.getPlayer() == player
            && currentCellEntity.getStoneCount() == 0;
    }

    private boolean isNotOpponentKalah(Player player, Cell currentCellEntity) {
        return !(currentCellEntity.getCellType() == CellType.KALAH && currentCellEntity.getPlayer() != player);
    }

    private void verifyCellToMove(CellType cellType, int stoneCount) {
        if (cellType == CellType.KALAH) {
            throw new IllegalArgumentException(
                "You are not allowed to move from Kalah cells. Please, select any pit sell."
            );
        }
        if (stoneCount == 0) {
            throw new IllegalArgumentException(
                "You do not have any stone to make a move."
            );
        }
    }

    private void verifyPlayer(Player player, Cell cellEntity, Player playerToMakeMove, GameStatus status) {
        if (status == GameStatus.IN_PROGRESS) {
            if (playerToMakeMove != null && playerToMakeMove != player) {
                throw new IllegalStateException(
                    String.format("Player: %s is supposed to make a move.", playerToMakeMove)
                );
            }

            if (cellEntity.getPlayer() != player) {
                throw new IllegalArgumentException(
                    String.format("You are not allowed to play for: %s", player)
                );
            }
        }
    }
}

