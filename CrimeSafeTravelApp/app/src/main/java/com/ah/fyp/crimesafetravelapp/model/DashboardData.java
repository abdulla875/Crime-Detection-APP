package com.ah.fyp.crimesafetravelapp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardData {
    private int highestAmountOfCrime;
    private int totalAmountOfCrime;
    private String highestCrimeStreet;
    private int lowestAmountOfCrime;
    private String lowestCrimeStreet;
    public int getHighestAmountOfCrime() {
        return highestAmountOfCrime;
    }

    public int getTotalAmountOfCrime() {
        return totalAmountOfCrime;
    }

    public String getHighestCrimeStreet() {
        return highestCrimeStreet;
    }

    public void setHighestAmountOfCrime(int highestAmountOfCrime) {
        this.highestAmountOfCrime = highestAmountOfCrime;
    }

    public void setTotalAmountOfCrime(int totalAmountOfCrime) {
        this.totalAmountOfCrime = totalAmountOfCrime;
    }

    public void setHighestCrimeStreet(String highestCrimeStreet) {
        this.highestCrimeStreet = highestCrimeStreet;
    }

    public int getLowestAmountOfCrime() {
        return lowestAmountOfCrime;
    }

    public void setLowestAmountOfCrime(int lowestAmountOfCrime) {
        this.lowestAmountOfCrime = lowestAmountOfCrime;
    }

    public String getLowestCrimeStreet() {
        return lowestCrimeStreet;
    }

    public void setLowestCrimeStreet(String lowestCrimeStreet) {
        this.lowestCrimeStreet = lowestCrimeStreet;
    }

    @Override
    public String toString() {
        return "DashboardData{" +
                "highestAmountOfCrime=" + highestAmountOfCrime +
                ", totalAmountOfCrime=" + totalAmountOfCrime +
                ", highestCrimeStreet='" + highestCrimeStreet + '\'' +
                ", lowestAmountOfCrime=" + lowestAmountOfCrime +
                ", lowestCrimeStreet='" + lowestCrimeStreet + '\'' +
                '}';
    }
}
