package com.example.myweather.model;

import java.util.List;

/**
 * Historical weather data
 */
public class History {
    private List<ForecastDay> forecastday;


    public History() {}

    public History(List<ForecastDay> forecastday) {
        this.forecastday = forecastday;
    }


    public List<ForecastDay> getForecastday() { return forecastday; }
    public void setForecastday(List<ForecastDay> forecastday) { this.forecastday = forecastday; }
}
