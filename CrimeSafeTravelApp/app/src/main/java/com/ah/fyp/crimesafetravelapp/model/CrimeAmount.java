package com.ah.fyp.crimesafetravelapp.model;


import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrimeAmount {

    private String area;
    private int amountOfCrime;
    private String latitude;
    private String longitude;
    private List<CrimeType> crimeType;
    private String month;

    @Override
    public String toString() {
        return "CrimeAmount{" +
                "area='" + area + '\'' +
                ", amountOfCrime=" + amountOfCrime +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", crimeType=" + crimeType +
                ", month='" + month + '\'' +
                '}';
    }

    public String getArea() {
        return area;
    }

    public int getAmountOfCrime() {
        return amountOfCrime;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public List<CrimeType> getCrimeType() {
        return crimeType;
    }

    public String getMonth() {
        return month;
    }
}
