package com.ssafyhome.map.service;

import com.ssafyhome.map.api.KakaoMobilityApiClient;
import com.ssafyhome.map.dto.RouteRequest;
import com.ssafyhome.map.dto.RouteResultDto;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dao.AddressRepository;
import com.ssafyhome.user.dto.Address;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MapService {

    private final AddressRepository addressRepository;
    private final KakaoMobilityApiClient kakaoMobilityApiClient;

    public RouteResultDto getRouteComparison(CustomUserDetails userDetails, RouteRequest request) throws Exception {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();
        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        Address start = addressRepository.findByUserAndTitle(user, request.getTitle())
                .orElseThrow(() -> new EntityNotFoundException("주소 없음"));

        double sx = Double.parseDouble(start.getX());
        double sy = Double.parseDouble(start.getY());
        double ex = Double.parseDouble(request.getDealX());
        double ey = Double.parseDouble(request.getDealY());

        Map<String, Object> carInfo = kakaoMobilityApiClient.getDrivingInfo(sx, sy, ex, ey);

        return new RouteResultDto(
                (double) carInfo.get("distanceKm"),
                (int) carInfo.get("durationMin")
        );
    }
}
