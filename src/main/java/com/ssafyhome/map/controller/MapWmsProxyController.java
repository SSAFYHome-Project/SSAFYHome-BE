package com.ssafyhome.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapWmsProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/wms")
    public ResponseEntity<byte[]> proxyWmsTile(
            @RequestParam String bbox,
            @RequestParam(defaultValue = "256") int width,
            @RequestParam(defaultValue = "256") int height
    ) {

        try {
        	 String url = String.format(
                     "https://geo.safemap.go.kr/geoserver/safemap/wms?" +
                             "service=WMS&version=1.1.1&request=GetMap" +
                             "&layers=A2SM_CRMNLHSPOT_TOT" +
                             "&styles=A2SM_CrmnlHspot_Tot_Tot" +
                             "&bbox=%s" +
                             "&srs=EPSG:3857" +
                             "&width=%d&height=%d" +
                             "&format=image/png&transparent=true",
                     bbox, width, height
             );


            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.IMAGE_PNG));
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    URI.create(url), HttpMethod.GET, request, byte[].class
            );


            return ResponseEntity.status(response.getStatusCode())
                    .contentType(response.getHeaders().getContentType() != null
                            ? response.getHeaders().getContentType()
                            : MediaType.APPLICATION_OCTET_STREAM)
                    .body(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
