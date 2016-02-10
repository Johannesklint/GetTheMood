package com.example.johannesklint.spotifyapitest;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class Darkside extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "c630fe9a50b94f27ab408ae38e9e6fdc";
    private static final String REDIRECT_URI = "ourfirstappfromschool://callback";
    private Player mPlayer;
    private static final int REQUEST_CODE = 1337;
    private Button pauseBtn;
    private Button playBtn;
    private ArrayList<String> darkList;
    private Button nextBtn;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_darkside);
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

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.pause();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                darkPlayList();
            }
        });

        nextBtn = (Button)findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.skipToNext();
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener(){

            @Override
            public void onShake(int count) {
                mPlayer.skipToNext();
                Log.v("Darkside", "SHAKE WORKS ---SONG CHANGED");

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
                        mPlayer.addConnectionStateCallback(Darkside.this);
                        mPlayer.addPlayerNotificationCallback(Darkside.this);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("Darkside", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }


    public void darkPlayList(){
        darkList = new ArrayList<String>();

        darkList.add("spotify:track:3SLJvq2HH1UvPyL7CF7Auh");
        darkList.add("spotify:track:58ZVxvtCUBeVONNAttWMHX");

        Collections.shuffle(darkList);

        mPlayer.play(darkList);
    }


    @Override
    public void onLoggedIn() {
        Toast.makeText(Darkside.this, "User logged in", Toast.LENGTH_SHORT).show();
        Log.v("Darkside", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.v("Darkside", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Toast.makeText(Darkside.this, "\"Login failed", Toast.LENGTH_SHORT).show();
        Log.v("Darkside", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.v("Darkside", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String s) {
        Log.v("Darkside", "Received connection message: " + s);
    }

    @Override
    public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
        Log.v("Darkside", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String s) {
        Log.v("Darkside", "Playback error received: " + errorType.name());
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

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

}
