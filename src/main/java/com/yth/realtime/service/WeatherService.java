package com.yth.realtime.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WeatherService {

    private final Dotenv dotenv;

    private final String apiKey;

    public WeatherService() {
        // Dotenv를 사용해 .env 파일 로드
        this.dotenv = Dotenv.load();
        this.apiKey = dotenv.get("WEATHER_API_KEY"); // .env 파일에서 API 키 가져오기
    }

    public List<Map<String, String>> fetchWeatherData(String dateFirst, String dateLast, String region) {
        String apiUrl = String.format(
            "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm3.php?tm1=%s0000&tm2=%s2359&stn=%s&help=0&authKey=%s",
            dateFirst, dateLast, region, apiKey
        );

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);
        
        return parseWeatherData(response);
    }

    private List<Map<String, String>> parseWeatherData(String data) {
        if (data == null || data.trim().isEmpty()) {
            log.error("받은 데이터가 비어있습니다");
            return Collections.emptyList();
        }

        try {
            List<Map<String, String>> result = new ArrayList<>();
            String[] lines = data.split("\n");
            log.info("원본 데이터: {}", data);  // 원본 데이터 로깅
            
            // #7777END 이전의 유효한 데이터만 처리
            List<String> validLines = new ArrayList<>();
            for (String line : lines) {
                if (line.trim().equals("#7777END")) break;
                if (!line.trim().isEmpty()) {
                    validLines.add(line);
                }
            }
            
            // 헤더 찾기 (YYMMDDHHMI가 포함된 라인)
            int headerIndex = -1;
            for (int i = 0; i < validLines.size(); i++) {
                if (validLines.get(i).contains("YYMMDDHHMI")) {
                    headerIndex = i;
                    break;
                }
            }
            
            if (headerIndex == -1) {
                log.error("헤더를 찾을 수 없습니다");
                return Collections.emptyList();
            }

            // 헤더 파싱
            String[] headers = validLines.get(headerIndex).replace("#", "").trim().split("\\s+");
            log.info("파싱된 헤더: {}", Arrays.toString(headers));

            // 데이터 파싱
            for (int i = headerIndex + 1; i < validLines.size(); i++) {
                String line = validLines.get(i).trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] values = line.split("\\s+");
                
                // 데이터 검증
                if (values.length < 2) {
                    log.warn("라인 {} 스킵: 데이터가 부족함", i);
                    continue;
                }

                // 필요한 데이터만 추출 (YYMMDDHHMI, TA, WS)
                Map<String, String> entry = new HashMap<>();
                entry.put("YYMMDDHHMI", values[0]);  // 시간
                entry.put("TA", values[2]);          // 기온
                entry.put("WS", values[3]);          // 풍속

                result.add(entry);
            }

            log.info("파싱된 데이터 개수: {}", result.size());
            if (!result.isEmpty()) {
                log.info("첫 번째 데이터: {}", result.get(0));
            }
            return result;
        } catch (Exception e) {
            log.error("데이터 파싱 중 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}

