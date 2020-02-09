package com.silaev.kalah.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreationResponseDto {
    private Integer id;
    private String url;
}
