// package com.yth.realtime.service;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import io.github.cdimascio.dotenv.Dotenv;

// @Service

// public class WeatherService {

//     private final Dotenv dotenv;

//     private final String apiKey;

//     public WeatherService() {
//         // Dotenv를 사용해 .env 파일 로드
//         this.dotenv = Dotenv.load();
//         this.apiKey = dotenv.get("WEATHER_API_KEY"); // .env 파일에서 API 키 가져오기
//     }

//     public String fetchWeatherData(String dateFirst, String dateLast, String region) {
//         String apiUrl = String.format(
//             "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm3.php?tm1=%s0000&tm2=%s2359&stn=%s&help=0&authKey=%s",
//             dateFirst, dateLast, region, apiKey
//         );

//         RestTemplate restTemplate = new RestTemplate();
//         return restTemplate.getForObject(apiUrl, String.class);
//     }
// }


package com.yth.realtime.service;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WeatherService {
    private final String apiKey;

    public WeatherService() {
        // 직접 환경 변수에서 API 키 가져오기
        this.apiKey = System.getenv("WEATHER_API_KEY");
        log.info("API 키 로드 상태: {}", apiKey != null);
    }

    public String fetchWeatherData(String dateFirst, String dateLast, String region) {
        String apiUrl = String.format(
            "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm3.php?tm1=%s0000&tm2=%s2359&stn=%s&help=0&authKey=%s",
            dateFirst, dateLast, region, apiKey
        );

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, String.class);
    }
}