package com.ssafyhome.apt.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AptApiClient {

    @Value("${api.key.data_co_kr}")
    private String serviceKey;

    public String findAptCode(String aptName, String sggCd) {
        try {
            String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);
            StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/1613000/AptListService3/getSigunguAptList3");
            urlBuilder.append("?serviceKey=").append(encodedKey);
            urlBuilder.append("&sigunguCode=").append(sggCd);
            urlBuilder.append("&pageNo=1");
            urlBuilder.append("&numOfRows=100");
            urlBuilder.append("&resultType=json");
            System.out.println("요청 URL: " + urlBuilder);

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
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

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.toString());
            JsonNode items = root.path("response").path("body").path("items");

            for (JsonNode item : items) {
                String name = item.path("kaptName").asText();
                String code = item.path("kaptCode").asText();

                if (name.contains(aptName)) {
                    System.out.println("찾은 aptCode: " + code);
                    return code;
                }
            }

            System.out.println("일치하는 아파트 이름 없음");

        } catch (Exception e) {
            System.err.println("아파트 코드 조회 중 오류: " + e.getMessage());
        }

        return null;
    }

}
