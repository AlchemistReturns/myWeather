package com.example.myweather.model;

/**
 * Air quality information
 */
public class AirQuality {
    private double co;
    private double no2;
    private double o3;
    private double so2;
    private double pm2_5;
    private double pm10;
    private int us_epa_index;
    private int gb_defra_index;


    public AirQuality() {}


    public double getCo() { return co; }
    public void setCo(double co) { this.co = co; }

    public double getNo2() { return no2; }
    public void setNo2(double no2) { this.no2 = no2; }

    public double getO3() { return o3; }
    public void setO3(double o3) { this.o3 = o3; }

    public double getSo2() { return so2; }
    public void setSo2(double so2) { this.so2 = so2; }

    public double getPm2_5() { return pm2_5; }
    public void setPm2_5(double pm2_5) { this.pm2_5 = pm2_5; }

    public double getPm10() { return pm10; }
    public void setPm10(double pm10) { this.pm10 = pm10; }

    public int getUs_epa_index() { return us_epa_index; }
    public void setUs_epa_index(int us_epa_index) { this.us_epa_index = us_epa_index; }

    public int getGb_defra_index() { return gb_defra_index; }
    public void setGb_defra_index(int gb_defra_index) { this.gb_defra_index = gb_defra_index; }

    public String getAirQualityDescription() {
        if (us_epa_index <= 1) return "Good";
        else if (us_epa_index <= 2) return "Moderate";
        else if (us_epa_index <= 3) return "Unhealthy for Sensitive Groups";
        else if (us_epa_index <= 4) return "Unhealthy";
        else if (us_epa_index <= 5) return "Very Unhealthy";
        else return "Hazardous";
    }
}
