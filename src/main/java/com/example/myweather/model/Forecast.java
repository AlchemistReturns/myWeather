package com.example.myweather.model;

import java.util.List;

/**
 * Weather forecast data
 */
public class Forecast {
    private List<ForecastDay> forecastday;


    public Forecast() {}

    public Forecast(List<ForecastDay> forecastday) {
        this.forecastday = forecastday;
    }


    public List<ForecastDay> getForecastday() { return forecastday; }
    public void setForecastday(List<ForecastDay> forecastday) { this.forecastday = forecastday; }
}
