package com.ah.fyp.crimesafetravelapp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MostCrime {
    String crimeName;
    int numberOfCrimeOccurrence;

    public MostCrime(String crimeName, int numberOfCrimeOccurrence) {
        this.crimeName = crimeName;
        this.numberOfCrimeOccurrence = numberOfCrimeOccurrence;
    }

    public String getCrimeName() {
        return crimeName;
    }

    public void setCrimeName(String crimeName) {
        this.crimeName = crimeName;
    }

    public int getNumberOfCrimeOccurrence() {
        return numberOfCrimeOccurrence;
    }

    public void setNumberOfCrimeOccurrence(int numberOfCrimeOccurrence) {
        this.numberOfCrimeOccurrence = numberOfCrimeOccurrence;
    }

    @Override
    public String toString() {
        return "MostCrime{" +
                "crimeName='" + crimeName + '\'' +
                ", numberOfCrimeOccurrence=" + numberOfCrimeOccurrence +
                '}';
    }
}
