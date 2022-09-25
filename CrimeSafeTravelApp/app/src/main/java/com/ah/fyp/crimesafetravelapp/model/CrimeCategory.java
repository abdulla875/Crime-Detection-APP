package com.ah.fyp.crimesafetravelapp.model;

public enum CrimeCategory {
    type1("vehicle-crime"),
    type2("public-order"),
    type3("burglary"),
    type4( "bicycle-theft"),
    type5("criminal-damage-arson"),
    type6("shoplifting"),
    type7("other-crime"),
    type8("robbery");
    public final String name;

    private CrimeCategory(String s) {
        name = s;

    }
}
