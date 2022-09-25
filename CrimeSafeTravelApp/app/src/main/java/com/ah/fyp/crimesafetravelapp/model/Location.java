package com.ah.fyp.crimesafetravelapp.model;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

public class Location implements ClusterItem {

    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private List<CrimeType> mCrimeType;
    private int mRoadCrimeAmount;

    public Location(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        mTitle = null;
        mSnippet = null;
    }

    public Location(double lat, double lng, String title, String snippet, List<CrimeType> crimeType,int roadCrimeAmount) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mCrimeType = crimeType;
        mRoadCrimeAmount = roadCrimeAmount;
    }

    public int getmRoadCrimeAmount() {
        return mRoadCrimeAmount;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Nullable
    @Override
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public List<CrimeType> getmCrimeType() {
        return mCrimeType;
    }
}
