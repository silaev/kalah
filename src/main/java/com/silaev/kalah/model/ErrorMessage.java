package com.silaev.kalah.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author Konstantin Silaev on 2/11/2020
 */
@Builder
@Data
public class ErrorMessage {
    private final String message;
}
