package com.example.johannesklint.spotifyapitest;

import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity  {

    private Button lightBtn;
    private SensorManager mySensorManager;
    private android.hardware.Sensor lightSensor;
    private boolean ifBoolean;
    private float previousValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lightBtn = (Button)findViewById(R.id.lightBtn);
        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeIfLightIsWorking();
            }
        });
    }

    public void seeIfLightIsWorking() {

        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = mySensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_LIGHT);
        mySensorManager.registerListener(LightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    final SensorEventListener LightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float currentValue = event.values[0];

                if (event.sensor.getType() == android.hardware.Sensor.TYPE_LIGHT) {

                    if (currentValue < 200 && ifBoolean == false) {
                        Intent intent = new Intent(getApplicationContext(), Darkside.class);
                        startActivity(intent);
                        ifBoolean = true;
                    } else if(currentValue > 200 && ifBoolean == false){
                        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                        startActivity(intent);
                        ifBoolean = true;
                    }
                }
            previousValue = currentValue;
        }

        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

        }
    };

}
