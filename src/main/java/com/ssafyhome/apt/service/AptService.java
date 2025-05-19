package com.ssafyhome.apt.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafyhome.apt.api.AptApiClient;
import com.ssafyhome.apt.dto.AptDetailResponse;
import com.ssafyhome.apt.dto.AptDetailResponse.ChartItem;
import com.ssafyhome.apt.dto.AptDetailResponse.HistoryItem;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AptService {

    private final AptApiClient aptApiClient;

    @Value("${api.key.data_co_kr}")
    private String serviceKey;

    public AptDetailResponse getAptDetail(String aptName, String sggCd, String jibun) {
        String aptCode = aptApiClient.findAptCode(aptName, sggCd);
        if (aptCode == null) {
            throw new IllegalArgumentException("아파트 코드 없음");
        }

        JsonNode aptInfo;
        try {
            String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);
            StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/1613000/AptBasisInfoServiceV3/getAphusBassInfoV3");
            urlBuilder.append("?serviceKey=").append(encodedKey);
            urlBuilder.append("&kaptCode=").append(aptCode);
            urlBuilder.append("&pageNo=1&numOfRows=100&resultType=json");

            HttpURLConnection conn = (HttpURLConnection) new URL(urlBuilder.toString()).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader rd = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
                    ? new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))
                    : new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) response.append(line);
            rd.close(); conn.disconnect();

            JsonNode root = new ObjectMapper().readTree(response.toString());
            aptInfo = root.path("response").path("body").path("item");

        } catch (Exception e) {
            throw new RuntimeException("아파트 기본 정보 조회 실패: " + e.getMessage());
        }

        String targetAptName = aptInfo.path("kaptName").asText();
        String lawdCd = sggCd.substring(0, 5);

        List<HistoryItem> history = new ArrayList<>();
        Map<String, List<Integer>> tradeChartMap = new TreeMap<>();
        Map<String, List<Integer>> rentChartMap = new TreeMap<>();

        for (int year = 2023; year <= 2025; year++) {
            int endMonth = (year == 2025) ? 5 : 12;
            for (int month = 1; month <= endMonth; month++) {
                String yyyymm = String.format("%04d%02d", year, month);
                String periodKey = String.format("%04d-%02d", year, ((month - 1) / 3) * 3 + 1);

                history.addAll(fetchDeals("Trade", lawdCd, yyyymm, targetAptName, jibun, tradeChartMap, periodKey));
                history.addAll(fetchDeals("Rent", lawdCd, yyyymm, targetAptName, jibun, rentChartMap, periodKey));
            }
        }

        history.sort((a, b) -> parseYearMonth(b.getDate()) - parseYearMonth(a.getDate()));

        List<ChartItem> tradeChart = buildChartData(tradeChartMap);
        List<ChartItem> rentChart = buildChartData(rentChartMap);

        HistoryItem recentTrade = history.stream().filter(h -> h.getType().equals("매매"))
                .max(Comparator.comparing(HistoryItem::getDate)).orElse(null);
        HistoryItem recentRent = history.stream().filter(h -> h.getType().equals("전월세"))
                .max(Comparator.comparing(HistoryItem::getDate)).orElse(null);

        HistoryItem lowestTrade = history.stream().filter(h -> h.getType().equals("매매"))
                .min(Comparator.comparingInt(h -> parsePrice(h.getPrice()))).orElse(null);

        HistoryItem lowestRent = history.stream()
                .filter(h -> h.getType().equals("전월세") && h.getPrice().contains("(전세)"))
                .min(Comparator.comparingInt(h -> parsePrice(h.getPrice()))).orElse(null);

        return AptDetailResponse.builder()
                .aptName(aptInfo.path("kaptName").asText())
                .kaptCode(aptInfo.path("kaptCode").asText())
                .kaptAddr(aptInfo.path("kaptAddr").asText())
                .doroJuso(aptInfo.path("doroJuso").asText())
                .hoCnt(aptInfo.path("hoCnt").asInt())
                .kaptDongCnt(aptInfo.path("kaptDongCnt").asText())
                .kaptTopFloor(aptInfo.path("kaptTopFloor").asInt())
                .kaptUsedate(aptInfo.path("kaptUsedate").asText())
                .recentTradeDate(recentTrade != null ? recentTrade.getDate() : "-")
                .recentTradePrice(recentTrade != null ? recentTrade.getPrice() : "-")
                .recentTradeFloor(recentTrade != null ? recentTrade.getFloor() : "-")
                .recentRentDate(recentRent != null ? recentRent.getDate() : "-")
                .recentRentPrice(recentRent != null ? recentRent.getPrice() : "-")
                .recentRentFloor(recentRent != null ? recentRent.getFloor() : "-")
                .lowestTradeDate(lowestTrade != null ? lowestTrade.getDate() : "-")
                .lowestTradePrice(lowestTrade != null ? lowestTrade.getPrice() : "-")
                .lowestTradeFloor(lowestTrade != null ? lowestTrade.getFloor() : "-")
                .lowestRentDate(lowestRent != null ? lowestRent.getDate() : "-")
                .lowestRentPrice(lowestRent != null ? lowestRent.getPrice() : "-")
                .lowestRentFloor(lowestRent != null ? lowestRent.getFloor() : "-")
                .chartData(tradeChart)
                .rentChartData(rentChart)
                .history(history)
                .build();
    }

    private List<ChartItem> buildChartData(Map<String, List<Integer>> map) {
        List<ChartItem> chart = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            double avg = entry.getValue().stream().mapToInt(i -> i).average().orElse(0);
            chart.add(ChartItem.builder()
                    .date(entry.getKey())
                    .price(Math.round(avg / 10000.0 * 10.0) / 10.0)
                    .build());
        }
        return chart;
    }

    private List<HistoryItem> fetchDeals(String type, String lawdCd, String yyyymm, String aptName, String jibun, Map<String, List<Integer>> priceMap, String key) {
        List<HistoryItem> results = new ArrayList<>();
        try {
            String endpoint = type.equals("Trade") ? "RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade" : "RTMSDataSvcAptRent/getRTMSDataSvcAptRent";
            String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);

            StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/1613000/" + endpoint);
            urlBuilder.append("?LAWD_CD=").append(lawdCd);
            urlBuilder.append("&DEAL_YMD=").append(yyyymm);
            urlBuilder.append("&serviceKey=").append(encodedKey);
            urlBuilder.append("&_type=json&numOfRows=100");

            HttpURLConnection conn = (HttpURLConnection) new URL(urlBuilder.toString()).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader rd = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
                    ? new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))
                    : new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) response.append(line);
            rd.close(); conn.disconnect();

            JsonNode items = new ObjectMapper().readTree(response.toString())
                    .path("response").path("body").path("items").path("item");

            for (JsonNode item : items) {
                String name = item.path("aptNm").asText();
                String aptJibun = item.path("jibun").asText();

                String strippedName = name.replaceAll("\\s+", "");
                String strippedTarget = aptName.replaceAll("\\s+", "");

                boolean nameMatch = strippedName.contains(strippedTarget) || strippedTarget.contains(strippedName);
                boolean jibunMatch = aptJibun.contains(jibun) || jibun.contains(aptJibun);

                if (!(nameMatch && jibunMatch)) continue;

                String date = item.path("dealYear").asText() + "." + String.format("%02d", item.path("dealMonth").asInt());
                String floor = item.path("floor").asText() + "층";
                String area = item.path("excluUseAr").asText() + "m²";

                String rawPrice = type.equals("Trade")
                        ? item.path("dealAmount").asText().replaceAll(",", "").trim()
                        : item.path("deposit").asText().replaceAll(",", "") + "/" + item.path("monthlyRent").asText().replaceAll(",", "");

                String price;
                if (type.equals("Trade")) {
                    int amount = Integer.parseInt(rawPrice);
                    price = formatTradePrice(amount);
                    priceMap.computeIfAbsent(key, k -> new ArrayList<>()).add(amount);
                } else {
                    int deposit = 0, rent = 0;
                    try {
                        String[] parts = rawPrice.split("/");
                        deposit = Integer.parseInt(parts[0].trim());
                        rent = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
                    } catch (Exception e) {
                        System.err.println("전월세 가격 파싱 실패: " + rawPrice);
                    }
                    price = formatRentPrice(deposit, rent);
                    if (rent == 0 && deposit > 0) {
                        priceMap.computeIfAbsent(key, k -> new ArrayList<>()).add(deposit);
                    }
                }

                results.add(HistoryItem.builder()
                        .date(date)
                        .type(type.equals("Trade") ? "매매" : "전월세")
                        .price(price)
                        .floor(floor)
                        .area(area)
                        .build());
            }

        } catch (Exception e) {
            System.err.println("거래 정보 조회 실패: " + e.getMessage());
        }
        return results;
    }

    private int parseYearMonth(String date) {
        try {
            String[] parts = date.split("\\.");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            return year * 100 + month;
        } catch (Exception e) {
            return 0;
        }
    }

    private int parsePrice(String price) {
        try {
            return Integer.parseInt(price.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatTradePrice(int amount) {
        if (amount == 0) return "-";
        int eok = amount / 10000;
        int man = amount % 10000;
        return eok == 0 ? man + "만원" : (man == 0 ? eok + "억" : eok + "억 " + man + "만원");
    }

    private String formatRentPrice(int deposit, int rent) {
        return rent == 0 ? formatTradePrice(deposit) + " (전세)" : formatTradePrice(deposit) + " / " + rent + "만원 (월세)";
    }
}