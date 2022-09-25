package com.ah.fyp.crimesafetravelapp.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrimeType {

    private String ctype;
    private int amount;

    public CrimeType(String ctype, int amount) {
        this.ctype = ctype;
        this.amount = amount;
    }

    public String getCtype() {
        return ctype;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "CrimeType{" +
                "cType='" + ctype + '\'' +
                ", amount=" + amount +
                '}';
    }
}