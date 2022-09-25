package com.ah.fyp.crimesafetravelapp.crimemap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ah.fyp.crimesafetravelapp.R;
import com.google.gson.Gson;

public class DashboardFragment extends Fragment{

    private static final String ARG_HIGHEST_STREET_CRIME = "highestStreetCrime";
    private static final String STREET_WITH_HIGHEST_CRIME = "StreetWithHighestCrime";
    private static final String TOTAL_AMOUNT_OF_CRIME = "TotalAmountOfCrime";
    private static final String STREET_WITH_LOWEST_CRIME = "StreetWithLowestCrime";


    private String highestStreetCrime;
    private String mParam2;
    private View view;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String highestStreetCrime) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_HIGHEST_STREET_CRIME, highestStreetCrime);
        fragment.setArguments(bundle);
        return fragment;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        TextView txtStreetWithHighestCrime = view.findViewById(R.id.txtStreetWithHighestCrime);
        TextView txtStreetWithLowestCrime = view.findViewById(R.id.txtStreetWithLowestCrime);
        TextView txtTotalAmountOfCrime = view.findViewById(R.id.txtTotalAmountOfCrime);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);

        int totalAmountOfCrime = sharedPreferences.getInt(TOTAL_AMOUNT_OF_CRIME,0);
        txtTotalAmountOfCrime.setText("Total Amount Crime: " + System.getProperty("line.separator") + Integer.toString(totalAmountOfCrime));

        String streetWithHighestCrime = sharedPreferences.getString(STREET_WITH_HIGHEST_CRIME,"");
        txtStreetWithHighestCrime.setText("Most Dangerous Street is around : " + System.getProperty("line.separator") + streetWithHighestCrime);

        String streetWithLowestCrime = sharedPreferences.getString(STREET_WITH_LOWEST_CRIME,"");
        txtStreetWithLowestCrime.setText("Most Safe Street is around : " + System.getProperty("line.separator") + streetWithLowestCrime);

        return view;
    }

}