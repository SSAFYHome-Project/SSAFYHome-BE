package com.ssafyhome.map.controller;

import com.ssafyhome.map.api.ApartmentDealApiClient;
import com.ssafyhome.map.api.RegionCodeApiClient;
import com.ssafyhome.map.dto.SafetyDto;
import com.ssafyhome.map.service.MapService;
import com.ssafyhome.map.service.SafetyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/map")
public class SafetyController {

    private final SafetyService safetyService;

    @GetMapping("/safety")
    public ResponseEntity<SafetyDto> getSido(@RequestParam String sido, @RequestParam String sigungu) throws Exception {
        SafetyDto dto = safetyService.getSafetyInfo(sido, sigungu);
        return ResponseEntity.ok(dto);
    }
}
