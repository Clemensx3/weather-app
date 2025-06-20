package com.example.weather_app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class GeocodingService {

    public record GeoCodingResponse(
            String name,
            double lat,
            double lon,
            String country
    ) {}

    @Value("${openweathermap.api.key}")
    private String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<GeoCodingResponse> getCoordinates(String city, String countryCode) {
        String url = String.format(
                "http://api.openweathermap.org/geo/1.0/direct?q=%s,%s&limit=1&appid=%s",
                city, countryCode, apiKey
        );

        ResponseEntity<GeoCodingResponse[]> response = restTemplate.getForEntity(url, GeoCodingResponse[].class);
        GeoCodingResponse[] results = response.getBody();

        if (results != null && results.length > 0) {
            return Optional.of(results[0]);
        }
        return Optional.empty();
    }
}
