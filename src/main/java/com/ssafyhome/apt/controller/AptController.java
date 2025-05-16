package com.ssafyhome.apt.controller;

import com.ssafyhome.apt.dto.AptDetailResponse;
import com.ssafyhome.apt.service.AptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apt")
@RequiredArgsConstructor
public class AptController {

    private final AptService aptService;

    @GetMapping("/detail")
    public AptDetailResponse getDetail(
            @RequestParam String aptName,
            @RequestParam String sggCd,
            @RequestParam String jibun
    ) {
        return aptService.getAptDetail(aptName, sggCd, jibun);
    }
}
