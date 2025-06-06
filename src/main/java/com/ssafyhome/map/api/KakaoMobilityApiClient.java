package com.ssafyhome.map.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafyhome.common.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class KakaoMobilityApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.map.api-key}")
    private String kakaoApiKey;

    private static final String BASE_URL = "https://apis-navi.kakaomobility.com/v1/directions";

    public Map<String, Object> getDrivingInfo(double sx, double sy, double ex, double ey) throws Exception {
        try {String url = BASE_URL +
                "?origin=" + sx + "," + sy +
                "&destination=" + ex + "," + ey +
                "&priority=RECOMMEND";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            JsonNode summary = root.path("routes").get(0).path("summary");
            double distance = summary.path("distance").asDouble(); // 단위: meter
            int durationSeconds = summary.path("duration").asInt(); // 단위: second
            int durationMinutes = (int) Math.round(durationSeconds / 60.0); // 소수점 반올림

            Map<String, Object> result = new HashMap<>();
            result.put("distanceKm", distance / 1000);
            result.put("durationMin", durationMinutes);
            return result;
        } catch (Exception e) {
            throw new ApiException("운전 경로 정보를 가져오는 도중 오류가 발생했습니다: " + e.getMessage(), e);
        }

    }
}
