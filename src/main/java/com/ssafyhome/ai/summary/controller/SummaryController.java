package com.ssafyhome.ai.summary.controller;

import com.ssafyhome.ai.summary.dto.SummaryDto;
import com.ssafyhome.ai.summary.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/map")
@RestController
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;


    @PostMapping("/summary")
    public ResponseEntity<?> chat(@RequestBody SummaryDto summaryDto) {
        try {
            String result = summaryService.summarizeArea(summaryDto);
            Map<String, String> response = new HashMap<>();
            response.put("summary", result);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("요약 서비스 처리 중 오류가 발생했습니다.");
        }
    }

}
