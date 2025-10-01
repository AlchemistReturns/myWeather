package com.example.myweather.model;

/**
 * Main weather data model containing current weather, forecast, and historical data
 */
public class WeatherData {
    private Location location;
    private Current current;
    private Forecast forecast;
    private History history;


    public WeatherData() {}

    public WeatherData(Location location, Current current, Forecast forecast, History history) {
        this.location = location;
        this.current = current;
        this.forecast = forecast;
        this.history = history;
    }


    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public Current getCurrent() { return current; }
    public void setCurrent(Current current) { this.current = current; }

    public Forecast getForecast() { return forecast; }
    public void setForecast(Forecast forecast) { this.forecast = forecast; }

    public History getHistory() { return history; }
    public void setHistory(History history) { this.history = history; }
}
