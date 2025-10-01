package com.example.myweather.model;

/**
 * Location information for weather data
 */
public class Location {
    private String name;
    private String region;
    private String country;
    private double lat;
    private double lon;
    private String tz_id;
    private long localtime_epoch;
    private String localtime;


    public Location() {}


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public String getTz_id() { return tz_id; }
    public void setTz_id(String tz_id) { this.tz_id = tz_id; }

    public long getLocaltime_epoch() { return localtime_epoch; }
    public void setLocaltime_epoch(long localtime_epoch) { this.localtime_epoch = localtime_epoch; }

    public String getLocaltime() { return localtime; }
    public void setLocaltime(String localtime) { this.localtime = localtime; }

    public String getFullLocationName() {
        StringBuilder locationName = new StringBuilder();
        
        if (name != null && !name.trim().isEmpty()) {
            locationName.append(name);
        }
        
        if (region != null && !region.trim().isEmpty()) {
            if (locationName.length() > 0) {
                locationName.append(", ");
            }
            locationName.append(region);
        }
        
        if (country != null && !country.trim().isEmpty()) {
            if (locationName.length() > 0) {
                locationName.append(", ");
            }
            locationName.append(country);
        }
        
        return locationName.toString();
    }
}
