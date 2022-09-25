package com.ah.fyp.crimesafetravelapp.crimemap;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ah.fyp.crimesafetravelapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;
import java.util.Locale;

public class EmergencySMSFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private View view;

    private Button sosMessageBtn;
    private Button saveNumberBtn;
    private TextView numberTxt;
    private EditText getNumberEditTxt;
    private Button commuteMessageBtn;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NUMBER = "number";
    private String number;


    public EmergencySMSFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_emergency_s_m_s, container, false);
        sosMessageBtn = view.findViewById(R.id.sosMessageBtn);
        saveNumberBtn = view.findViewById(R.id.saveNumberBtn);
        numberTxt = view.findViewById(R.id.numberTxt);
        getNumberEditTxt = view.findViewById(R.id.getNumberEditTxt);
        commuteMessageBtn = view.findViewById(R.id.commuteMessageBtn);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());



        saveNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberTxt.setText(getNumberEditTxt.getText());
                saveNumber();
                getNumberEditTxt.getText().clear();
            }
        });

        commuteMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveCommuteAddress();
            }
        });


        sosMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission
                                (getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        &&ActivityCompat.checkSelfPermission
                        (getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission
                                (getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                deviceLocation();
            }
        });

            loadNumber();
            updateNumber();

        return view;
    }

    private void saveNumber(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NUMBER,getNumberEditTxt.getText().toString());
        editor.apply();
        Toast.makeText(getContext(), "Number is Saved", Toast.LENGTH_SHORT).show();
    }

    private void loadNumber(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        number = sharedPreferences.getString(NUMBER, "");
    }


    public void updateNumber(){
        numberTxt.setText(number);
    }

    private void receiveCommuteAddress(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);

        String source = sharedPreferences.getString("SOURCE","");
        String destination = sharedPreferences.getString("DESTINATION","");
        sendCommuteLocationAddress(source,destination);
    }

  private void sendCommuteLocationAddress(String source, String destination){
      loadNumber();
      String phoneNumber = number;
      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(phoneNumber,null, "I am commuting from : " + source + "To" + destination,null,null);
    }

    private void sendSms(String message){
       loadNumber();
        String phoneNumber = number;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber,null, "HELP! My Location is: " + message,null,null);
    }

    private void deviceLocation() {
        try {

            Task location = fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null);

            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        System.out.println("onComplete location found");
                        android.location.Location currentLocation = (Location) task.getResult();

                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> getAddresses = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
                            String address = getAddresses.get(0).getAddressLine(0);
                            sendSms(address);
                            Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }else{
                        System.out.println("Device location not found");
                        Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            System.out.println("Device location exception " + e.getMessage());
        }

    }

}