package com.example.johannesklint.spotifyapitest;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "c630fe9a50b94f27ab408ae38e9e6fdc";
    private static final String REDIRECT_URI = "ourfirstappfromschool://callback";
    private Player mPlayer;
    private static final int REQUEST_CODE = 1337;
    private Player player;
    private Button pauseBtn;
    private Button playBtn;
    private Button lightBtn;
    private TextView textLight_available, textLight_reading;
    private SensorManager mySensorManager;
    private android.hardware.Sensor lightSensor;
    private boolean songIsPlaying;
    private float previousValue;
    private ArrayList<String> lightList;
    private ArrayList<String> darkList;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        pauseBtn = (Button)findViewById(R.id.pauseBtn);
        playBtn = (Button)findViewById(R.id.playBtn);
        lightBtn = (Button)findViewById(R.id.lightBtn);

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.pause();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.play("spotify:track:5UJhGHTejeId8sd04ypvam");
            }
        });

        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeIfLightIsWorking();
            }
        });

        nextBtn = (Button)findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.skipToNext();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer = player;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addPlayerNotificationCallback(MainActivity.this);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    public void lightPlayList(){
        lightList = new ArrayList<String>();

        lightList.add("spotify:track:4AWo1MuDaBGRiIqpjFzVfW");
        lightList.add("spotify:track:6pkjW5srxjzRSKKMrl7et8");
        lightList.add("spotify:track:58ZVxvtCUBeVONNAttWMHX");

        Collections.shuffle(lightList);

        mPlayer.play(lightList);

    }

    public void darkPlayList(){
        darkList = new ArrayList<String>();

        darkList.add("spotify:track:3SLJvq2HH1UvPyL7CF7Auh");
        darkList.add("spotify:track:58ZVxvtCUBeVONNAttWMHX");

        Collections.shuffle(darkList);

        mPlayer.play(darkList);
    }


    public void seeIfLightIsWorking() {

        textLight_available = (TextView) findViewById(R.id.textLightAvailable);
        textLight_reading = (TextView) findViewById(R.id.textLightReading);

        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        lightSensor = mySensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_LIGHT);

        if (lightSensor != null) {
            textLight_available.setText("Light sensor is working");
            mySensorManager.registerListener(LightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            textLight_available.setText("Light sensor NOT working");
        }

    }

    final SensorEventListener LightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float currentValue = event.values[0];

                if (event.sensor.getType() == android.hardware.Sensor.TYPE_LIGHT) {
                    textLight_reading.setText("LIGHT: " + event.values[0]);

                    if (currentValue < 200 && songIsPlaying == false) {
                        Log.v("MainActivity", "under 200");
                        darkPlayList();
                        songIsPlaying = true;
                    } else if(currentValue > 200 && songIsPlaying == false){
                        Log.v("MainActivity", "over 200");
                        lightPlayList();
                        songIsPlaying = true;
                    }
                }
            previousValue = currentValue;
        }

        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onLoggedIn() {
        Toast.makeText(MainActivity.this, "User logged in", Toast.LENGTH_SHORT).show();
        Log.v("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.v("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Toast.makeText(MainActivity.this, "\"Login failed", Toast.LENGTH_SHORT).show();
        Log.v("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.v("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String s) {
        Log.v("MainActivity", "Received connection message: " + s);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.v("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
        Log.v("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }


}
