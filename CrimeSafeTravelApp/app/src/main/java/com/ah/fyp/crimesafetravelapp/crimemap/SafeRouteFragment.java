package com.ah.fyp.crimesafetravelapp.crimemap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ah.fyp.crimesafetravelapp.R;
import com.ah.fyp.crimesafetravelapp.model.DashboardData;
import com.ah.fyp.crimesafetravelapp.model.Location;
import com.ah.fyp.crimesafetravelapp.model.PolylineMapsData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SafeRouteFragment extends Fragment implements View.OnClickListener,GoogleMap.OnPolylineClickListener  {

    private Button directionBtn;
    private View view;
    private GoogleMap mMap;
    private EditText sourceEditTxt, destinationEditTxt;
    private Address address;
    private GeoApiContext geoApiContext = null;
    private ArrayList<PolylineMapsData> polylineMapsData = new ArrayList<>();
    private ArrayList<Marker> oldRouteMarket = new ArrayList<>();
    private ArrayList<Marker> oldSourceRouteMarket = new ArrayList<>();
    private ClusterManager<com.ah.fyp.crimesafetravelapp.model.Location> clusterManager;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.243840, -0.903085), 2));

            mMap = googleMap;
            mMap.setOnPolylineClickListener(SafeRouteFragment.this);
            searchInit();
            setUpClusterer();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_safe_route, container, false);

        directionBtn = view.findViewById(R.id.directionsBtn);
        directionBtn.setOnClickListener(this);

        sourceEditTxt = view.findViewById(R.id.sourceEdit);
        destinationEditTxt = view.findViewById(R.id.destinationEdit);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.directionsBtn) {
            searchInit();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder().apiKey("api key").build();

        }
    }

    private void removeRouteMarker(){
        for(Marker marker : oldRouteMarket){
            marker.remove();
        }
        for(Marker marker : oldSourceRouteMarket){
            marker.remove();
        }

    }

    private void searchInit() {
        String source = sourceEditTxt.getText().toString().trim();
        String destination = destinationEditTxt.getText().toString().trim();

        searchGeoLocate(source, destination);
        removeRouteMarker();
    }

    private void searchGeoLocate(String source, String destination) {

        Geocoder geocoder = new Geocoder(getActivity());

        List<Address> sourceList = new ArrayList<>();
        List<Address> destinationList = new ArrayList<>();

        try {
            sourceList = geocoder.getFromLocationName(source, 1);
            destinationList = geocoder.getFromLocationName(destination, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (sourceList.size() > 0 && destinationList.size() > 0) {
            address = sourceList.get(0);

            double getSourceLat = address.getLatitude();
            double getSourceLng = address.getLongitude();
            String sourceLat = String.valueOf(getSourceLat);
            String sourceLng = String.valueOf(getSourceLng);
            LatLng sourceLatLng = new LatLng(getSourceLat, getSourceLng);
            System.out.println("Source address:" + sourceLatLng);
            address = destinationList.get(0);
            String sourceAddress = sourceList.get(0).getAddressLine(0);


            double getDestinationLat = address.getLatitude();
            double getDestinationLng = address.getLongitude();
            String destinationLat = String.valueOf(getDestinationLat);
            String destinationLng = String.valueOf(getDestinationLng);
            LatLng destinationLatLng = new LatLng(getDestinationLat, getDestinationLng);
            System.out.println("Destination address:" + destinationLatLng);
            String destinationAddress = destinationList.get(0).getAddressLine(0);

            directionCalculation(getSourceLat, getSourceLng, getDestinationLat, getDestinationLng);
            commuteLocationData(sourceAddress,destinationAddress);
            retrieveCrimeDataByLocation(sourceLat,destinationLng);
        }
    }

    private void commuteLocationData(String source,String destination){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SOURCE", source);
        editor.putString("DESTINATION", destination);
        editor.apply();
    }

    private void directionCalculation(double sourceLat, double sourceLng, double destinationLat, double destinationLng) {

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                destinationLat,
                destinationLng
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
        directions.alternatives(true);
        directions.mode(TravelMode.WALKING);
        directions.origin(
                new com.google.maps.model.LatLng(
                        sourceLat,
                        sourceLng
                )
        );
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                displayPolyline(result);
            }

            @Override
            public void onFailure(Throwable e) {
                System.out.println("onFailure: " + e.getMessage());
            }
        });
    }




private void displayPolyline(final DirectionsResult directionsResult){
new Handler(Looper.getMainLooper()).post(new Runnable() {
    @Override
    public void run() {
        if(polylineMapsData.size() > 0){
            for(PolylineMapsData polylineMapsData : polylineMapsData){
                polylineMapsData.getPolyline().remove();
            }
            polylineMapsData.clear();
            polylineMapsData = new ArrayList<>();
        }
        double routeDuration = 8888888;
        for(DirectionsRoute directionsRoute : directionsResult.routes){

            List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(directionsRoute.overviewPolyline.getEncodedPath());
            List<LatLng> newDecodedPath = new ArrayList<>();

            for(com.google.maps.model.LatLng latLng: decodedPath){

                newDecodedPath.add(new LatLng(
                        latLng.lat, latLng.lng
                ));
            }
            Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
            polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
            polyline.setClickable(true);
            polylineMapsData.add(new PolylineMapsData(polyline, directionsRoute.legs[0]));

            double tempRouteDuration = directionsRoute.legs[0].duration.inSeconds;
            if(tempRouteDuration < routeDuration){
                routeDuration = tempRouteDuration;
                onPolylineClick(polyline);
                zoomRoute(polyline.getPoints());
            }
        }
    }
});

}
    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        int index = 0;
        for(PolylineMapsData polylineMapsData : polylineMapsData){
            index++;
            if(polyline.getId().equals(polylineMapsData.getPolyline().getId())){
                polylineMapsData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blueViolet));
                polylineMapsData.getPolyline().setZIndex(1);

                LatLng destinationLocation = new LatLng(
                        polylineMapsData.getDirectionsLeg().endLocation.lat,
                        polylineMapsData.getDirectionsLeg().endLocation.lng);

                Marker marker =  mMap.addMarker(new MarkerOptions().position(destinationLocation).title("Destination Mark").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                marker.showInfoWindow();

                LatLng sourceLocation = new LatLng(
                        polylineMapsData.getDirectionsLeg().startLocation.lat,
                        polylineMapsData.getDirectionsLeg().startLocation.lng);

                Marker sourceMarker =  mMap.addMarker(new MarkerOptions().position(sourceLocation).title("Source Route:" + index).snippet("Duration: " +
                        polylineMapsData.getDirectionsLeg().duration).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                sourceMarker.showInfoWindow();
                oldRouteMarket.add(marker);
                oldSourceRouteMarket.add(sourceMarker);
            }else {
                polylineMapsData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                polylineMapsData.getPolyline().setZIndex(0);
            }
        }
    }


    private void setUpClusterer() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.243840, -0.903085), 10));
        clusterManager = new ClusterManager<com.ah.fyp.crimesafetravelapp.model.Location>(getActivity(), mMap);
        mMap.setOnCameraIdleListener(clusterManager);
    }

    private void retrieveCrimeDataByLocation(String lat, String lng) {

        SafeRouteFragment.getSafeRouteApiData callApi = new SafeRouteFragment.getSafeRouteApiData();
        callApi.execute("API LINK"+lat+"&lng="+lng+"&date=2021-12");
    }

    private class getSafeRouteApiData extends AsyncTask<String, Void, JSONArray> {

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
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addCrimeData(JSONArray layer) {

        try {
            mMap.clear();
            List<com.ah.fyp.crimesafetravelapp.model.Location> newitems = new CrimeDataReader().filteredCrimeData(layer);
            System.out.println("Filter data " + newitems.size());
            clusterManager = new ClusterManager<Location>(requireContext(), mMap);
            mMap.setOnCameraIdleListener(clusterManager);
            clusterManager.setRenderer(new MapClusterIconRender(getContext(), mMap, clusterManager));
            clusterManager.addItems(newitems);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }
}