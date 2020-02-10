package com.silaev.kalah.dto;

import com.silaev.kalah.model.GameStatus;
import com.silaev.kalah.model.Player;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * @author Konstantin Silaev on 2/4/2020
 */
@Builder(toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
public class KalahGameStateDto {
    private final Map<Integer, Integer> statuses;
    private final Player playerToMakeMove;
    private final GameStatus status;
}
