//package com.ssafyhome.map.controller;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//@RestController
//@RequestMapping("/api/map")
//public class WmsProxyController {
//	
//	@Value("${api.key.safe_map}")
//    private String serviceKey;
//
//    private static final String SAFEMAP_WMS_URL = "https://www.safemap.go.kr/openApiService/wms/getLayerData.do";
//
//    @GetMapping("/image")
//    public ResponseEntity<byte[]> getWmsImage(
//        @RequestParam String bbox,
//        @RequestParam int width,
//        @RequestParam int height
//    ) {
//        String url = SAFEMAP_WMS_URL +
//            "?apikey=" + URLEncoder.encode(serviceKey, StandardCharsets.UTF_8) +
//            "&service=WMS&version=1.1.1&request=GetMap" +
//            "&layers=A2SM_CRMNLHSPOT_TOT" +
//            "&styles=A2SM_CrmnlHspot_Tot_Tot" +
//            "&format=image/png&transparent=true" +
//            "&bbox=" + bbox +
//            "&width=" + width +
//            "&height=" + height +
//            "&srs=EPSG:4326";
//        try {
//            byte[] image = new RestTemplate().getForObject(url, byte[].class);
//            return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_PNG)
//                .body(image);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).build();
//        }
//    }
//}
