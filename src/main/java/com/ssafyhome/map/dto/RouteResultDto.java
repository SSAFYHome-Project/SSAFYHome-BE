package com.ssafyhome.map.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteResultDto {
    private double distanceKm;
    private int durationMin;
}
