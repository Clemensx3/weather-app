package com.example.weather_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class WeatherDataController {
    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @GetMapping("/weather")
    public ResponseEntity<WeatherData> getWeatherByCity(@RequestParam(value = "city") String city) {
        Optional<WeatherData> weatherByCity = weatherDataRepository.findByCity(city);

        if(weatherByCity.isPresent()) {
            return new ResponseEntity<WeatherData>(weatherByCity.get(), HttpStatus.OK);
        }
        return new ResponseEntity("No data for city " + city + " found", HttpStatus.NOT_FOUND);
    }
}
