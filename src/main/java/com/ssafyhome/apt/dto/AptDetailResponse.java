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
    private String lowestDate;
    private String lowestPrice;
    private String lowestFloor;
    private List<ChartItem> chartData;
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
