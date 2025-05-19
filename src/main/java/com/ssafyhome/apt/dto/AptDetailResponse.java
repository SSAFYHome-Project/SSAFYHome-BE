package com.ssafyhome.apt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class AptDetailResponse {
    private String aptName;
    private String kaptCode;
    private String kaptAddr;
    private String doroJuso;
    private int hoCnt;
    private String kaptDongCnt;
    private int kaptTopFloor;
    private String kaptUsedate;

    // 매매 최근 실거래 정보
    private String recentTradeDate;
    private String recentTradePrice;
    private String recentTradeFloor;

    // 전월세 최근 실거래 정보
    private String recentRentDate;
    private String recentRentPrice;
    private String recentRentFloor;

    // 매매 최저가 정보
    private String lowestTradeDate;
    private String lowestTradePrice;
    private String lowestTradeFloor;

    // 전세 기준 최저가 정보 (월세는 제외)
    private String lowestRentDate;
    private String lowestRentPrice;
    private String lowestRentFloor;

    // 차트 데이터
    private List<ChartItem> chartData;       // 매매
    private List<ChartItem> rentChartData;   // 전월세 (전세만 기준)

    // 전체 이력 데이터 (매매 + 전월세)
    private List<HistoryItem> history;

    @Data
    @Builder
    @AllArgsConstructor
    public static class ChartItem {
        private String date;
        private double price;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class HistoryItem {
        private String date;
        private String type;
        private String price;
        private String floor;
        private String area;
    }
}
