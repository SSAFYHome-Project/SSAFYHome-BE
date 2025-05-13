package com.ssafyhome.map.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Component
public class ApartmentDealApiClient {

    @Value("${api.key.data_co_kr}")
    private String serviceKey;

    private final String BASE_URL = "https://apis.data.go.kr/1613000";

    public String fetchTradeDeals(int dongCode, int yyyymm) throws Exception {
        return fetchFromApi("RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade", dongCode, yyyymm);
    }

    public String fetchRentDeals(int dongCode, int yyyymm) throws Exception {
        return fetchFromApi("RTMSDataSvcAptRent/getRTMSDataSvcAptRent", dongCode, yyyymm);
    }

    private String fetchFromApi(String endpoint, int dongCode, int yyyymm) throws Exception {
        String lawdCd = String.valueOf(dongCode).substring(0, 5);
        String dealYmd = String.valueOf(yyyymm);
        String encodedKey = URLEncoder.encode(serviceKey, "UTF-8");

        StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/" + endpoint);
        urlBuilder.append("?serviceKey=").append(encodedKey);
        urlBuilder.append("&LAWD_CD=").append(lawdCd);
        urlBuilder.append("&DEAL_YMD=").append(dealYmd);
        urlBuilder.append("&_type=json");
        urlBuilder.append("&numOfRows=9999");

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
        }

        rd.close();
        conn.disconnect();

        return response.toString();
    }
}
