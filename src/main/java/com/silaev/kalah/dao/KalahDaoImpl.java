package com.silaev.kalah.dao;

import com.silaev.kalah.model.Cell;
import com.silaev.kalah.model.KalahGameState;
import com.silaev.kalah.model.Pair;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Konstantin Silaev on 2/4/2020
 */
@Repository
public class KalahDaoImpl implements KalahDao {
    private final ConcurrentMap<Integer, Pair<KalahGameState, Cell[]>> store;

    public KalahDaoImpl() {
        this.store = new ConcurrentHashMap<>();
    }

    public KalahDaoImpl(final ConcurrentMap<Integer, Pair<KalahGameState, Cell[]>> store) {
        this.store = store;
    }

    @Override
    public int create(final KalahGameState initializedStore, final Cell[] cells) {
        final int generatedId = ThreadLocalRandom.current().nextInt();
        store.put(generatedId, Pair.of(initializedStore, cells));
        return generatedId;
    }

    @Override
    public KalahGameState getGameStateById(final int gameId) {
        return Optional.ofNullable(store.get(gameId))
            .map(Pair::getLeft)
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format("Cannot find KalahGameState for game id:%d", gameId)
                )
            );
    }

    @Override
    public Cell getCellByGameIdAndCellIndex(int gameId, int cellIndex) {
        return Optional.ofNullable(store.get(gameId))
            .map(Pair::getRight)
            .map(c -> c[cellIndex])
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format("Cannot find a cell with index: %d for game id: %d",
                        gameId, cellIndex
                    )
                )
            );
    }

    @Override
    public Map<Integer, Integer> getCellsIndexByGameIdAndCellIndex(int gameId) {
        final Cell[] cells = Optional.ofNullable(store.get(gameId))
            .map(Pair::getRight)
            .orElseThrow(
                () -> new IllegalStateException(String.format("Cannot find game id: %d", gameId))
            );
        return Stream.of(cells).collect(
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
    }
}
