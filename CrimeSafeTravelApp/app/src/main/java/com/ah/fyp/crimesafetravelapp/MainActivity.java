package com.ah.fyp.crimesafetravelapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.ah.fyp.crimesafetravelapp.crimemap.DashboardFragment;
import com.ah.fyp.crimesafetravelapp.crimemap.MapsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Button mapButton;
    private Button logout;
    private Boolean isLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 8008;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 8009;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navItemSelectedListener);


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()){

                        case R.id.nav_map:
                            selectedFragment = new MapsFragment();
                            break;
                        case R.id.nav_dashboard:
                            selectedFragment = new DashboardFragment();
                            break;
//                            selectedFragment = new EmergencySMSFragment();

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContiner, selectedFragment).commit();

                    return true;
                }
            };

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    private void getMap (){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContiner, new MapsFragment()).commit();
    }


    private boolean checkRequirements (){
       if (isLocationEnabled()){
           return true;
       }
        return false;
    }


    public boolean isLocationEnabled(){
        final LocationManager locationManager = (LocationManager)  getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return false;
        }
        return true;
    }



    private  void getLocationPermission(){
        if((ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED)){
            isLocationPermissionGranted = true;
            getMap();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}
            , PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        isLocationPermissionGranted = false;
        switch (requestCode){
            case  PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    isLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(isLocationPermissionGranted){
                    getMap();
                }else{
                    getLocationPermission();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkRequirements()){
            if(isLocationPermissionGranted){
                getMap();
            }else {
                getLocationPermission();
            }
        }
    }
}