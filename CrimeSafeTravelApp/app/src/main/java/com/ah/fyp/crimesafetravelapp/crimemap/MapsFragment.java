package com.ah.fyp.crimesafetravelapp.crimemap;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ah.fyp.crimesafetravelapp.R;
//import com.ah.fyp.crimesafetravelapp.databinding.ActivityMapsBinding;
import com.ah.fyp.crimesafetravelapp.model.CrimeAmount;
import com.ah.fyp.crimesafetravelapp.model.DashboardData;
import com.ah.fyp.crimesafetravelapp.model.MostCrime;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.CloseableHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapsFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private GoogleMap mMap;
    private EditText mSearchText;
    private Address address;
    private List<Location> items = new ArrayList<>();
    private ClusterManager<com.ah.fyp.crimesafetravelapp.model.Location> clusterManager;
    private SearchView searchView;
    private CloseableHttpClient client = HttpClientBuilder.create().build();
    private CrimeDataReader crimeDataReader = new CrimeDataReader();
    private View view;
    private ImageButton routeBtn;
    private static final String STREET_WITH_HIGHEST_CRIME = "StreetWithHighestCrime";
    private static final String TOTAL_AMOUNT_OF_CRIME = "TotalAmountOfCrime";
    private static final String STREET_WITH_LOWEST_CRIME = "StreetWithLowestCrime";
    private static final String MAX_TOTAL_STREET_CRIME= "maxTotalStreetCrime";
    private static final String MIN_TOTAL_STREET_CRIME= "minTotalStreetCrime";
    private static final String  HIGHEST_RATED_CRIME= "highestRatedCrime";
    private static final String LOWEST_Rated_CRIME= "lowestRatedCrime";

    private Spinner dateSpinner;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            searchView = view.findViewById(R.id.newSearch);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setOnCameraIdleListener(clusterManager);
            setUpClusterer();
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        routeBtn = view.findViewById(R.id.routebtn);
        dateSpinner = view.findViewById(R.id.dateSpinner);
        routeBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.date, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);
        dateSpinner.setOnItemSelectedListener(this);

        String lat = "52.243840";
        String lng = "-0.903085";
        String date = "2021-12";
        retrieveCrimeDataByLocation(lat,lng,date);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String date = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), date,Toast.LENGTH_SHORT).show();
        searchInit(date);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setUpClusterer() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.243840, -0.903085), 10));
        clusterManager = new ClusterManager<com.ah.fyp.crimesafetravelapp.model.Location>(getActivity(), mMap);
        mMap.setOnCameraIdleListener(clusterManager);
    }

    private void retrieveCrimeDataByLocation(String lat, String lng, String date) {

        getApiData callApi = new getApiData();
        callApi.execute("apilink"+lat+"&lng="+lng+"&date="+date);
    }

    private void searchInit(String date) {

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchGeoLocate(query, date);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
    }
    private void searchGeoLocate(String searchString, String date) {

        Geocoder geocoder = new Geocoder(getActivity());

        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            address = list.get(0);
            String currentAddress = list.get(0).getAddressLine(0);
            System.out.println("Current address: " + currentAddress);
            double getLat = address.getLatitude();
            double getLng = address.getLongitude();
            String lat = String.valueOf(getLat);
            String lng = String.valueOf(getLng);
            retrieveCrimeDataByLocation(lat,lng,date);
            LatLng latLng = new LatLng(getLat, getLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(latLng, 12),
                    600,
                    null
            );
        }


    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        if(view.getId() == R.id.routebtn){
            fragment = new SafeRouteFragment();
            replaceFragment(fragment);
        }
    }
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    private class getApiData extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            try {
                InputStream stream = new URL(strings[0]).openStream();
                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                stream.close();
                return new JSONArray(result.toString());
            } catch (IOException e) {
                System.out.println("JSON file could not be read");
            } catch (JSONException e) {
                System.out.println("JSON file could not be converted");
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(JSONArray layer) {

            addCrimeData(layer);
            topCrimeList(convertJsonArrayToCrimeAmountLists(layer));
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addCrimeData(JSONArray layer) {

        try {
            mMap.clear();
            List<com.ah.fyp.crimesafetravelapp.model.Location> newitems = new CrimeDataReader().returnData(layer);
            clusterManager = new ClusterManager<com.ah.fyp.crimesafetravelapp.model.Location>(requireContext(), mMap);
            mMap.setOnCameraIdleListener(clusterManager);
            clusterManager.addItems(newitems);

            dashboardData(layer);


        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    private List<CrimeAmount> convertJsonArrayToCrimeAmountLists(JSONArray jsonArray) {
        Type collectionType = new TypeToken<List<CrimeAmount>>() {
        }.getType();
        return new Gson()
                .fromJson(String.valueOf((jsonArray)), collectionType);
    }


    public Set<MostCrime> topCrimeList(List<CrimeAmount> lst){
        List<String> topCrime = new ArrayList<>();
        for (CrimeAmount crimeAmount : lst) {
            for (int j = 0; j < crimeAmount.getCrimeType().size(); j++) {
                String ct = crimeAmount.getCrimeType().get(j).getCtype();
                topCrime.add(ct);
            }
        }
        Set<MostCrime> mostCrimeList = new HashSet<>();
        for(int i=0;i<topCrime.size(); i++) {
            int freq = Collections.frequency(topCrime, topCrime.get(i));
            mostCrimeList.add(new MostCrime(topCrime.get(i),freq));
        }
        return mostCrimeList;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dashboardData(JSONArray layer) throws IOException, JSONException {
        List<com.ah.fyp.crimesafetravelapp.model.Location> crimeData = new CrimeDataReader().returnData(layer);
        DashboardData dashboardData = new DashboardData();
        dashboardData =  crimeDataReader.rodeWithHighestCrime(crimeData);
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> highestCrimeRoadList = new ArrayList<>();
        List<Address> lowestCrimeRoadList = new ArrayList<>();

        String highestCrimeStreet = null;
        int totalAmountOfCrime = dashboardData.getTotalAmountOfCrime();
        String lowestCrimeStreet = null;

        double highestCrimeStreetLat = 0;
        double highestCrimeStreetLng = 0;
        double lowestCrimeStreetLat = 0;
        double lowestCrimeStreetLng = 0;
        int maxTotalStreetCrime = dashboardData.getHighestAmountOfCrime();
        int minTotalStreetCrime = dashboardData.getLowestAmountOfCrime();

        for(int i =0; i<crimeData.size(); i++){
            if(maxTotalStreetCrime == crimeData.get(i).getmRoadCrimeAmount()){
                 highestCrimeStreetLat = crimeData.get(i).getPosition().latitude;
                 highestCrimeStreetLng = crimeData.get(i).getPosition().longitude;
                lowestCrimeStreetLat = crimeData.get(i).getPosition().latitude;
                lowestCrimeStreetLng = crimeData.get(i).getPosition().longitude;
            }
            if( minTotalStreetCrime == crimeData.get(i).getmRoadCrimeAmount() ){
                lowestCrimeStreetLat = crimeData.get(i).getPosition().latitude;
                lowestCrimeStreetLng = crimeData.get(i).getPosition().longitude;
            }
        }
        try {
            highestCrimeRoadList =geocoder.getFromLocation(highestCrimeStreetLat,highestCrimeStreetLng,1);
            lowestCrimeRoadList =geocoder.getFromLocation(lowestCrimeStreetLat,lowestCrimeStreetLng,1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (highestCrimeRoadList.size() > 0 && lowestCrimeRoadList.size() > 0) {
            address = highestCrimeRoadList.get(0);
            highestCrimeStreet = highestCrimeRoadList.get(0).getAddressLine(0).split(",")[0].replaceAll("[0-9]","");
            address = lowestCrimeRoadList.get(0);
            lowestCrimeStreet = lowestCrimeRoadList.get(0).getAddressLine(0).split(",")[0].replaceAll("[0-9]","");;

        }
        sendDashboardData(highestCrimeStreet, totalAmountOfCrime, lowestCrimeStreet,maxTotalStreetCrime, minTotalStreetCrime);

        List<String> top = new ArrayList<>();

        for(int x = 0; x <crimeData.size(); x ++){
            for (int i = 0; i < crimeData.get(x).getmCrimeType().size(); i ++) {
                String ct = crimeData.get(x).getmCrimeType().get(i).getCtype();
                top.add(ct);
            }
        }
        List<MostCrime> mostCrimeList = new ArrayList<>();
        for(int k=0;k<top.size(); k++) {

            mostCrimeList.add(new MostCrime(top.get(k),1));
        }



        HashSet<MostCrime>  hashSet = new HashSet<MostCrime>();
        for(int j = 0; j<mostCrimeList.size();j++) {

            hashSet.add(new MostCrime(mostCrimeList.get(j).getCrimeName(),mostCrimeList.get(j).getNumberOfCrimeOccurrence()));

        }
        System.out.println("Test " + hashSet);


        System.out.println("Most crime " + mostCrimeList);
    }





    private void sendDashboardData(String streetWithHighestCrime, int totalAmountOfCrime,String lowestCrimeStreet,
                                   int maxTotalStreetCrime, int minTotalStreetCrime){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STREET_WITH_HIGHEST_CRIME, streetWithHighestCrime);
        editor.putString(STREET_WITH_LOWEST_CRIME, lowestCrimeStreet);
        editor.putInt(TOTAL_AMOUNT_OF_CRIME, totalAmountOfCrime);
        editor.putInt(MAX_TOTAL_STREET_CRIME, maxTotalStreetCrime);
        editor.putInt(MIN_TOTAL_STREET_CRIME, minTotalStreetCrime);
        editor.putInt(HIGHEST_RATED_CRIME, maxTotalStreetCrime);
        editor.putInt(LOWEST_Rated_CRIME, minTotalStreetCrime);

        editor.apply();
    }
}

