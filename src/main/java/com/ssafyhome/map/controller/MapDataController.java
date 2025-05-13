package com.ssafyhome.map.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Map<String, Object> getDeals(@RequestParam int regionCode, @RequestParam int yyyymm) throws Exception {
        String tradeJson = apartmentDealApiClient.fetchTradeDeals(regionCode, yyyymm);
        String rentJson = apartmentDealApiClient.fetchRentDeals(regionCode, yyyymm);

        Map<String, Object> result = new HashMap<>();
        result.put("trade", new ObjectMapper().readTree(tradeJson));
        result.put("rent", new ObjectMapper().readTree(rentJson));

        return result;
    }
}
