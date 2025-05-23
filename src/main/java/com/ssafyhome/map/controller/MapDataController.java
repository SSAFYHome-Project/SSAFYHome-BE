package com.ssafyhome.map.controller;

import java.util.HashMap;
import java.util.Map;

import com.ssafyhome.map.dto.RouteRequest;
import com.ssafyhome.map.dto.RouteResultDto;
import com.ssafyhome.map.service.MapService;
import com.ssafyhome.security.dto.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafyhome.map.api.ApartmentDealApiClient;
import com.ssafyhome.map.api.RegionCodeApiClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/map")
public class MapDataController {

    private final RegionCodeApiClient regionCodeApiClient;
    private final ApartmentDealApiClient apartmentDealApiClient;
    private final MapService mapService;

    @GetMapping("/sido")
    public String getSido() throws Exception {
        return regionCodeApiClient.getSidoList();
    }

    @GetMapping("/gugun")
    public String getGugun(@RequestParam String sidoCode) throws Exception {
        return regionCodeApiClient.getGugunList(sidoCode);
    }

    @GetMapping("/dong")
    public String getDong(@RequestParam String gugunCode) throws Exception {
        return regionCodeApiClient.getDongList(gugunCode);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getDeals(@RequestParam int regionCode, @RequestParam int yyyymm) {
        try {
            String tradeJson = apartmentDealApiClient.fetchTradeDeals(regionCode, yyyymm);
            String rentJson = apartmentDealApiClient.fetchRentDeals(regionCode, yyyymm);

            Map<String, Object> result = new HashMap<>();
            result.put("trade", new ObjectMapper().readTree(tradeJson));
            result.put("rent", new ObjectMapper().readTree(rentJson));

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 검색 조건입니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("거래 정보 조회 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/route")
    public RouteResultDto getRoute(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody RouteRequest request) throws Exception {
        return mapService.getRouteComparison(userDetails, request);
    }
}
