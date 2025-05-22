package com.ssafyhome.ai.recommendation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationDto {
    private String region;
    private String transactionType;
    private String priceRange;
    private String areaSize;
    private String familyType;
    private String childrenAge;
    private String transport;
    private String nightReturn;
    private String mood;
}
