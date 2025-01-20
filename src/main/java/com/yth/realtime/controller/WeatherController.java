package com.yth.realtime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yth.realtime.service.WeatherService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})  // React 서버주소
public class WeatherController {
    
    @Autowired
    private WeatherService weatherService;
    
    @GetMapping("/weather")
    public ResponseEntity<?> getWeatherData(
            @RequestParam("date-first") String dateFirst,
            @RequestParam("date-last") String dateLast,
            @RequestParam("region") String region) {
        try {
            String weatherData = weatherService.fetchWeatherData(dateFirst, dateLast, region);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}