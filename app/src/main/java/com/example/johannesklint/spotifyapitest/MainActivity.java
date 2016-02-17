package com.example.johannesklint.spotifyapitest;

import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.johannesklint.spotifyapitest.main.About;
import com.example.johannesklint.spotifyapitest.main.Darkside;
import com.example.johannesklint.spotifyapitest.main.Main2Activity;


public class MainActivity extends AppCompatActivity {

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

                    if (currentValue < 300 && ifBoolean == false) {
                        Intent intent = new Intent(getApplicationContext(), Darkside.class);
                        startActivity(intent);
                        ifBoolean = true;
                    } else if(currentValue > 300 && ifBoolean == false){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_about){
            Intent intent = new Intent(getApplicationContext(), About.class);
            startActivity(intent);
        }else if(id == R.id.action_settings){
            startActivityForResult(new Intent(Settings.ACTION_APPLICATION_SETTINGS),0);
        }
        return super.onOptionsItemSelected(item);
    }

}
