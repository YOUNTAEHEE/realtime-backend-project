package com.yth.realtime.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    
    @Value("${weather.api.key}")
    private String apiKey;
    
    public String fetchWeatherData(String dateFirst, String dateLast, String region) {
        String apiUrl = String.format(
            "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm3.php?tm1=%s0000&tm2=%s2359&stn=%s&help=0&authKey=%s",
            dateFirst, dateLast, region, apiKey
        );
        
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, String.class);
    }
}