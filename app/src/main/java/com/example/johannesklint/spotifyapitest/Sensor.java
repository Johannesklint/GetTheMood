package com.example.johannesklint.spotifyapitest;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by johannesklint on 16-02-05.
 */
public class Sensor extends MainActivity {
    TextView textLight_available, textLight_reading;
    SensorManager mySensorManager;
    android.hardware.Sensor lightSensor;


    public void seeLight() {

        textLight_available = (TextView) findViewById(R.id.textLightAvailable);
        textLight_reading = (TextView) findViewById(R.id.textLightReading);

        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        lightSensor = mySensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_LIGHT);

        if (lightSensor != null) {
            textLight_available.setText("Light sensor is working");
            mySensorManager.registerListener(LightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            textLight_available.setText("Light sensor NOT working");
        }

    }

    private final SensorEventListener LightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == android.hardware.Sensor.TYPE_LIGHT) {
                textLight_reading.setText("LIGHT: " + event.values[0]);
            }
        }

        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

        }
    };
 }
