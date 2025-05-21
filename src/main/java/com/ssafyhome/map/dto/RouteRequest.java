package com.ssafyhome.map.dto;

import com.ssafyhome.user.dto.TitleType;
import lombok.Data;

@Data
public class RouteRequest {
    private String dealX;
    private String dealY;
    private TitleType title;
}
