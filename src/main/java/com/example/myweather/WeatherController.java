package com.example.myweather;

import com.example.myweather.model.*;
import com.example.myweather.service.WeatherApiService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * Main controller for the weather application
 */
public class WeatherController implements Initializable {
    

    @FXML private TextField citySearchField;
    @FXML private Button searchButton;
    @FXML private Button themeToggleButton;

    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    

    @FXML private VBox currentWeatherSection;
    @FXML private Label currentLocationLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label currentWeatherIcon;
    @FXML private Label currentTempLabel;
    @FXML private Label currentConditionLabel;
    @FXML private Label feelsLikeLabel;
    @FXML private Label humidityLabel;
    @FXML private Label cloudCoverLabel;
    @FXML private Label precipitationLabel;
    @FXML private Label windLabel;
    @FXML private Label airQualityLabel;
    @FXML private Label uvIndexLabel;
    

    @FXML private VBox forecastSection;
    @FXML private HBox forecastContainer;
    @FXML private VBox historicalSection;
    @FXML private VBox historicalContainer;
    

    private WeatherApiService weatherService;
    private boolean isDarkTheme = false;
    private String currentCity = "Gazipur";
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        weatherService = new WeatherApiService();
        setupEventHandlers();
        loadDefaultWeather();
    }
    
    private void setupEventHandlers() {

        citySearchField.setOnAction(e -> onSearchCity());
        

        updateCurrentTime();
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.minutes(1), e -> updateCurrentTime())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    @FXML
    private void onSearchCity() {
        String city = citySearchField.getText().trim();
        if (!city.isEmpty()) {
            currentCity = city;
            loadWeatherData(city);

            citySearchField.clear();
        }
    }
    
    @FXML
    private void onToggleTheme() {
        isDarkTheme = !isDarkTheme;
        updateTheme();
    }
    
    private void updateTheme() {
        Scene scene = themeToggleButton.getScene();
        if (scene != null) {
            if (isDarkTheme) {
                scene.getRoot().getStyleClass().add("dark-theme");
                themeToggleButton.setText("🌙");
            } else {
                scene.getRoot().getStyleClass().remove("dark-theme");
                themeToggleButton.setText("☀");
            }
        }
    }
    
    private void loadDefaultWeather() {
        loadWeatherData(currentCity);
    }
    
    private void loadWeatherData(String city) {
        showLoading(true);
        hideError();
        
        CompletableFuture<WeatherData> future = weatherService.getCompleteWeatherData(city);
        
        future.thenAccept(weatherData -> {
            Platform.runLater(() -> {
                updateUI(weatherData);
                showLoading(false);
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                showError("Failed to load weather data: " + throwable.getMessage());
                showLoading(false);
            });
            return null;
        });
    }
    
    private void updateUI(WeatherData weatherData) {
        updateCurrentWeather(weatherData);
        updateForecast(weatherData);
        updateHistorical(weatherData);
    }
    
    private void updateCurrentWeather(WeatherData weatherData) {
        if (weatherData.getLocation() != null) {
            currentLocationLabel.setText(weatherData.getLocation().getFullLocationName());
        }
        
        if (weatherData.getCurrent() != null) {
            Current current = weatherData.getCurrent();
            

            currentTempLabel.setText(String.format("%.0f°C", current.getTemp_c()));
            feelsLikeLabel.setText(String.format("Feels like %.0f°C", current.getFeelslike_c()));
            

            if (current.getCondition() != null) {
                currentConditionLabel.setText(current.getCondition().getText());
                updateWeatherIcon(current.getCondition().getCode(), current.getIs_day() == 1);
            }
            

            humidityLabel.setText(current.getHumidity() + "%");
            cloudCoverLabel.setText(current.getCloud() + "%");
            precipitationLabel.setText(String.format("%.1f mm", current.getPrecip_mm()));
            windLabel.setText(String.format("%.1f km/h %s", current.getWind_kph(), current.getWind_dir()));
            uvIndexLabel.setText(String.format("%.1f", current.getUv()));
            

            if (current.getAir_quality() != null) {
                airQualityLabel.setText(current.getAir_quality().getAirQualityDescription());
            }
        }
        
        updateCurrentTime();
    }
    
    private void updateForecast(WeatherData weatherData) {
        forecastContainer.getChildren().clear();
        
        if (weatherData.getForecast() != null && weatherData.getForecast().getForecastday() != null) {
            for (ForecastDay forecastDay : weatherData.getForecast().getForecastday()) {
                VBox forecastItem = createForecastItem(forecastDay);
                forecastContainer.getChildren().add(forecastItem);
            }
        }
    }
    
    private VBox createForecastItem(ForecastDay forecastDay) {
        VBox item = new VBox();
        item.getStyleClass().add("forecast-item");
        item.setSpacing(8);
        

        Label dateLabel = new Label(formatDate(forecastDay.getDate()));
        dateLabel.getStyleClass().add("forecast-date");
        

        Label icon = new Label();
        icon.getStyleClass().add("forecast-icon");
        if (forecastDay.getDay() != null && forecastDay.getDay().getCondition() != null) {
            setWeatherIcon(icon, forecastDay.getDay().getCondition().getCode(), true);
        }
        

        Label highTemp = new Label();
        Label lowTemp = new Label();
        if (forecastDay.getDay() != null) {
            highTemp.setText(String.format("%.0f°", forecastDay.getDay().getMaxtemp_c()));
            lowTemp.setText(String.format("%.0f°", forecastDay.getDay().getMintemp_c()));
        }
        highTemp.getStyleClass().add("forecast-temp-high");
        lowTemp.getStyleClass().add("forecast-temp-low");
        

        Label condition = new Label();
        if (forecastDay.getDay() != null && forecastDay.getDay().getCondition() != null) {
            condition.setText(forecastDay.getDay().getCondition().getText());
        }
        condition.getStyleClass().add("forecast-condition");
        
        item.getChildren().addAll(dateLabel, icon, highTemp, lowTemp, condition);
        return item;
    }
    
    private void updateHistorical(WeatherData weatherData) {
        historicalContainer.getChildren().clear();
        
        if (weatherData.getHistory() != null && weatherData.getHistory().getForecastday() != null) {
            for (ForecastDay historyDay : weatherData.getHistory().getForecastday()) {
                HBox historicalItem = createHistoricalItem(historyDay);
                historicalContainer.getChildren().add(historicalItem);
            }
        }
    }
    
    private HBox createHistoricalItem(ForecastDay historyDay) {
        HBox item = new HBox();
        item.getStyleClass().add("historical-item");
        item.setSpacing(15);
        

        Label dateLabel = new Label(formatDate(historyDay.getDate()));
        dateLabel.getStyleClass().add("historical-date");
        dateLabel.setPrefWidth(120);
        

        Label icon = new Label();
        icon.getStyleClass().add("historical-icon");
        if (historyDay.getDay() != null && historyDay.getDay().getCondition() != null) {
            setWeatherIcon(icon, historyDay.getDay().getCondition().getCode(), true);
        }
        

        Label temps = new Label();
        if (historyDay.getDay() != null) {
            temps.setText(String.format("%.0f° / %.0f°C", 
                historyDay.getDay().getMaxtemp_c(), 
                historyDay.getDay().getMintemp_c()));
        }
        temps.getStyleClass().add("historical-temps");
        

        Label condition = new Label();
        if (historyDay.getDay() != null && historyDay.getDay().getCondition() != null) {
            condition.setText(historyDay.getDay().getCondition().getText());
        }
        condition.getStyleClass().add("historical-condition");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        item.getChildren().addAll(dateLabel, icon, temps, spacer, condition);
        return item;
    }
    
    private void updateWeatherIcon(int conditionCode, boolean isDay) {
        setWeatherIcon(currentWeatherIcon, conditionCode, isDay);
    }
    
    private void setWeatherIcon(Label icon, int conditionCode, boolean isDay) {

        String iconText;
        
        switch (conditionCode) {
            case 1000:
                iconText = isDay ? "☀" : "🌙";
                break;
            case 1003:
                iconText = isDay ? "⛅" : "🌙";
                break;
            case 1006:
                iconText = "☁";
                break;
            case 1009:
                iconText = "☁";
                break;
            case 1030: case 1135: case 1147:
                iconText = "🌫";
                break;
            case 1063: case 1180: case 1183: case 1186: case 1189: case 1192: case 1195:
                iconText = "🌧";
                break;
            case 1066: case 1210: case 1213: case 1216: case 1219: case 1222: case 1225:
                iconText = "❄";
                break;
            case 1087:
                iconText = "⚡";
                break;
            case 1114: case 1117:
                iconText = "🌨";
                break;
            case 1273: case 1276: case 1279: case 1282:
                iconText = "⛈";
                break;
            default:
                iconText = isDay ? "☀" : "🌙";
        }
        
        icon.setText(iconText);
    }
    
    private void updateCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy - HH:mm"));
        currentTimeLabel.setText(formattedTime);
    }
    
    private String formatDate(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);
            return date.format(DateTimeFormatter.ofPattern("MMM d"));
        } catch (Exception e) {
            return dateString;
        }
    }
    
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void hideError() {
        errorLabel.setVisible(false);
    }
    
    public void shutdown() {
        if (weatherService != null) {
            weatherService.shutdown();
        }
    }
}
