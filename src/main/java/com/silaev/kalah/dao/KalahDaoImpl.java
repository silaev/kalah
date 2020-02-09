package com.silaev.kalah.dao;

import com.silaev.kalah.model.KalahGameState;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Konstantin Silaev on 2/4/2020
 */
@Repository
public class KalahDaoImpl implements KalahDao {
    private final ConcurrentMap<Integer, KalahGameState> store;

    public KalahDaoImpl() {
        this.store = new ConcurrentHashMap<>();
    }

    public KalahDaoImpl(final ConcurrentMap<Integer, KalahGameState> store) {
        this.store = store;
    }

    @Override
    public int create(KalahGameState initializedStore) {
        final int generatedId = ThreadLocalRandom.current().nextInt();
        store.put(generatedId, initializedStore);
        return generatedId;
    }

    @Override
    public KalahGameState getById(int gameId) {
        return store.get(gameId);
    }
}
