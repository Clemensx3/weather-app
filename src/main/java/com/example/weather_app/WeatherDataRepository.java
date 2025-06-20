package com.example.weather_app;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WeatherDataRepository extends CrudRepository<WeatherData, Integer> {
    Optional<WeatherData> findByCity(String city);
    Optional<WeatherData> findByCityAndCountryCode(String city, String countryCode);
}
