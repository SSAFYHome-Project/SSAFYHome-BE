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

    private String recentDate;

    // 매매 최저가 정보
    private String lowestDate;
    private String lowestPrice;
    private String lowestFloor;

    // 전월세 최저가 정보
    private String lowestRentDate;
    private String lowestRentPrice;
    private String lowestRentFloor;

    // 차트 데이터
    private List<ChartItem> chartData;       // 매매
    private List<ChartItem> rentChartData;   // 전월세

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
