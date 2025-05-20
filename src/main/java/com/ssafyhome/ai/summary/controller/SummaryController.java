package com.ssafyhome.ai.summary.controller;

import com.ssafyhome.ai.summary.dto.SummaryDto;
import com.ssafyhome.ai.summary.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/map")
@RestController
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;


    @GetMapping("/summary")
    public Map<String, String> chat(@RequestBody SummaryDto summaryDto) {
        String result = summaryService.summarizeArea(summaryDto);
        Map<String, String> response = new HashMap<>();
        response.put("summary", result);

        return response;
    }

}
