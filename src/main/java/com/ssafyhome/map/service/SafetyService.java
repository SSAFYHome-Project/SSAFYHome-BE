package com.ssafyhome.map.service;

import com.ssafyhome.map.dao.Safety;
import com.ssafyhome.map.dao.SafetyRepository;
import com.ssafyhome.map.dto.SafetyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SafetyService {

    private final SafetyRepository safetyRepository;

    public SafetyDto getSafetyInfo(String sido, String sigungu) {
        Safety safety = safetyRepository.findBySidoAndSigungu(sido, sigungu)
                .orElseThrow(() -> new IllegalArgumentException("해당 지역 정보 없음"));

        return new SafetyDto(safety);
    }


}
