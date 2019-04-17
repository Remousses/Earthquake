package com.example.earthquakeandroidapp.beans;

import java.math.BigInteger;

public class EarthquakeBean {
    private String id;
    private String place;
    private String urlMap;
    private Double magnetude;
    private BigInteger time ;

    public EarthquakeBean(){
        super();
    }

    public EarthquakeBean(String id, String place, String urlMap, Double magnetude, BigInteger time) {
        this.id = id;
        this.place = place;
        this.urlMap = urlMap;
        this.magnetude = magnetude;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(String urlMap) {
        this.urlMap = urlMap;
    }

    public Double getMagnetude() {
        return magnetude;
    }

    public void setMagnetude(Double magnetude) {
        this.magnetude = magnetude;
    }

    public BigInteger getTime() {
        return time;
    }

    public void setTime(BigInteger time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "id : " + this.id + ", place : " + this.place +", urlMap : " + this.urlMap +", magnetude : " + this.magnetude +", time : " + this.time;
    }
}