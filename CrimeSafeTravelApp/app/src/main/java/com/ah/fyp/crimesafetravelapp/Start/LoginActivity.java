package com.ah.fyp.crimesafetravelapp.Start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ah.fyp.crimesafetravelapp.MainActivity;
import com.ah.fyp.crimesafetravelapp.R;
import com.ah.fyp.crimesafetravelapp.crimemap.MapsFragment;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView newUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        newUser =  findViewById(R.id.newUser);
        newUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.newUser:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }

    }
}