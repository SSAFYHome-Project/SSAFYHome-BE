package com.ssafyhome.map.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegionCodeApiClient {

    @Value("${api.key_vworld}")
    private String vworldKey;

    private static final String BASE_URL = "https://api.vworld.kr/ned/data";

    public String getSidoList() throws Exception {
        return sendRequest("admCodeList", null);
    }

    public String getGugunList(String sidoCode) throws Exception {
        return sendRequest("admSiList", sidoCode);
    }

    public String getDongList(String gugunCode) throws Exception {
        return sendRequest("admDongList", gugunCode);
    }

    private String sendRequest(String endpoint, String admCode) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/" + endpoint);
        urlBuilder.append("?format=json");
        urlBuilder.append("&numOfRows=100");
        urlBuilder.append("&key=").append(URLEncoder.encode(vworldKey, "UTF-8"));
        urlBuilder.append("&domain=localhost");

        if (admCode != null) {
            urlBuilder.append("&admCode=").append(admCode);
        }

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        conn.disconnect();
        return response.toString();
    }
}
