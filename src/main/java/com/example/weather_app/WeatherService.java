package com.example.weather_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class WeatherService {
    @Autowired
    private GeocodingService geocodingService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openweathermap.api.key}")
    private String apiKey;

    public WeatherData getCoordinatesAndSaveWeather(String city, String countryCode) {
        var geoCoordinates = geocodingService.getCoordinates(city, countryCode);

        if(geoCoordinates.isEmpty()) {
            throw new RuntimeException("City not found");
        }

        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&appid=%s",
                geoCoordinates.get().lat(), geoCoordinates.get().lon(), apiKey
        );

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        Map<String, Object> body = response.getBody();
        Map<String, Object> main = (Map<String, Object>) body.get("main");

        double temperature = (double) main.get("temp");

        WeatherData weather = new WeatherData();

        weather.setCity(city);
        weather.setCountryCode(countryCode);
        weather.setTemperature(temperature);
        weather.setLastUpdated(LocalDateTime.now());

        return weather;

    }
}
