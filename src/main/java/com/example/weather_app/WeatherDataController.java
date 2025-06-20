package com.example.weather_app;

import jdk.javadoc.doclet.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class WeatherDataController {
    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseEntity<WeatherData> getWeatherByCity(@RequestParam(value = "city") String city) {
        Optional<WeatherData> weatherByCity = weatherDataRepository.findByCity(city);

        if(weatherByCity.isPresent()) {
            return new ResponseEntity<WeatherData>(weatherByCity.get(), HttpStatus.OK);
        }
        return new ResponseEntity("No data for city " + city + " found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/weather/all")
    public ResponseEntity<Iterable<WeatherData>> getAllWeatherData() {
        var allWeatherData = weatherDataRepository.findAll();

        return new ResponseEntity<Iterable<WeatherData>>(allWeatherData, HttpStatus.OK);
    }

    @PostMapping("/weather")
    public ResponseEntity<WeatherData> saveWeatherData(@RequestBody WeatherData newWeatherData) {
        weatherDataRepository.save(newWeatherData);
        return new ResponseEntity<WeatherData>(newWeatherData, HttpStatus.CREATED);
    }

    @DeleteMapping("/weather")
    public ResponseEntity deleteWeatherData(@RequestParam(value = "city") String city) {
        Optional<WeatherData> weatherDataToDelete= weatherDataRepository.findByCity(city);

        if(weatherDataToDelete.isPresent()) {
            weatherDataRepository.deleteById(weatherDataToDelete.get().getId());
            return new ResponseEntity("Weather Data of " + weatherDataToDelete.get().getCity() + " was deleted.", HttpStatus.OK);
        }
        return new ResponseEntity("No Weather Data for following city found: " + weatherDataToDelete.get().getCity(), HttpStatus.NOT_FOUND);
    }

    @PutMapping("/weather")
    public ResponseEntity<WeatherData> editWeatherData(@RequestBody WeatherData editedWeatherData) {
        Optional<WeatherData> weatherDataInDb = weatherDataRepository.findByCity(editedWeatherData.getCity());

        if(weatherDataInDb.isPresent()) {
            editedWeatherData.setId(weatherDataInDb.get().getId());
            WeatherData savedWeatherData = weatherDataRepository.save(editedWeatherData);
            return new ResponseEntity<WeatherData>(savedWeatherData, HttpStatus.OK);
        }
        return new ResponseEntity("No existing city to update with name " + editedWeatherData.getCity() + " found", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/weather/api")
    public ResponseEntity<WeatherData> fetchWeather(@RequestParam(value = "city") String city,
                                                    @RequestParam(value = "countryCode") String countryCode) {
        var weather = weatherService.getCoordinatesAndSaveWeather(city, countryCode);

        return new ResponseEntity<WeatherData>(weather, HttpStatus.OK);
    }

    @GetMapping("/weather/api")
    public ResponseEntity<WeatherData> getWeather(@RequestParam(value = "city") String city,
                                                  @RequestParam(value = "countryCode") String countryCode) {

        Optional<WeatherData> weatherFromDb = weatherDataRepository.findByCityAndCountryCode(city, countryCode);

        if(weatherFromDb.isPresent()) {
            var lastUpdatedTime = weatherFromDb.get().getLastUpdated();
            if(LocalDateTime.now().isAfter(lastUpdatedTime.plusHours(1))) {
                //Patch request ausführen
                //Return
                return new ResponseEntity("Now the entity should be patched", HttpStatus.OK);
            } else {
                return new ResponseEntity<WeatherData>(weatherFromDb.get(), HttpStatus.OK);
            }
        }
        return fetchWeather(city, countryCode);
    }

    







}
