package com.example.myweather.service;

import com.example.myweather.model.ForecastDay;
import com.example.myweather.model.History;
import com.example.myweather.model.WeatherData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service class for handling WeatherAPI requests
 */
public class WeatherApiService {
    private static final String API_KEY = "193f87848a1b4a6dabe52758252108";
    private static final String BASE_URL = "https://api.weatherapi.com/v1";
    
    private final OkHttpClient httpClient;
    private final Gson gson;

    public WeatherApiService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Get current weather for a city
     */
    public CompletableFuture<WeatherData> getCurrentWeather(String city) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = String.format("%s/current.json?key=%s&q=%s&aqi=yes", 
                    BASE_URL, API_KEY, city);
                
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected response code: " + response.code());
                    }
                    
                    String responseBody = response.body().string();
                    return gson.fromJson(responseBody, WeatherData.class);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch current weather: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Get weather forecast for specified days
     */
    public CompletableFuture<WeatherData> getForecast(String city, int days) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = String.format("%s/forecast.json?key=%s&q=%s&days=%d&aqi=yes&alerts=no", 
                    BASE_URL, API_KEY, city, days);
                
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected response code: " + response.code());
                    }
                    
                    String responseBody = response.body().string();
                    return gson.fromJson(responseBody, WeatherData.class);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch forecast: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Get historical weather data for the last 7 days
     */
    public CompletableFuture<WeatherData> getHistoricalWeather(String city, int days) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                LocalDate currentDate = LocalDate.now().minusDays(1);
                List<ForecastDay> historicalDays = new ArrayList<>();
                

                for (int i = 0; i < Math.min(days, 7); i++) {
                    LocalDate targetDate = currentDate.minusDays(i);
                    
                    String url = String.format("%s/history.json?key=%s&q=%s&dt=%s", 
                        BASE_URL, API_KEY, city, 
                        targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    
                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    try (Response response = httpClient.newCall(request).execute()) {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            WeatherData dayData = gson.fromJson(responseBody, WeatherData.class);
                            
                            if (dayData.getForecast() != null && 
                                dayData.getForecast().getForecastday() != null && 
                                !dayData.getForecast().getForecastday().isEmpty()) {
                                historicalDays.add(dayData.getForecast().getForecastday().get(0));
                            }
                        }
                    } catch (Exception e) {

                        System.err.println("Failed to get data for " + targetDate + ": " + e.getMessage());
                    }
                }
                

                WeatherData result = new WeatherData();
                History history = new History();
                history.setForecastday(historicalDays);
                result.setHistory(history);
                
                return result;
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch historical weather: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Search for cities
     */
    public CompletableFuture<String> searchCities(String query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = String.format("%s/search.json?key=%s&q=%s", 
                    BASE_URL, API_KEY, query);
                
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected response code: " + response.code());
                    }
                    
                    return response.body().string();
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to search cities: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Get comprehensive weather data (current + forecast + historical)
     */
    public CompletableFuture<WeatherData> getCompleteWeatherData(String city) {
        return getForecast(city, 3).thenCompose(forecastData -> {
            return getHistoricalWeather(city, 7).thenApply(historicalData -> {

                forecastData.setHistory(historicalData.getHistory());
                return forecastData;
            });
        });
    }

    /**
     * Close the HTTP client
     */
    public void shutdown() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}
