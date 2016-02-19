package com.example.johannesklint.spotifyapitest.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.johannesklint.spotifyapitest.MainActivity;
import com.example.johannesklint.spotifyapitest.R;
import com.example.johannesklint.spotifyapitest.data.Album;
import com.example.johannesklint.spotifyapitest.data.Artist;
import com.example.johannesklint.spotifyapitest.service.APICallback;
import com.example.johannesklint.spotifyapitest.service.SpotifyService;
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

/**
 * This acitivty is for when it's light
 * Press "Play" to play the list
 * Press "Stop" to stop playinh
 * Press "Next" to change song OR shake the device
 */
public class Main2Activity extends MainActivity implements PlayerNotificationCallback, ConnectionStateCallback, SensorEventListener, APICallback {

    private static final String CLIENT_ID = "c630fe9a50b94f27ab408ae38e9e6fdc";
    private static final String REDIRECT_URI = "ourfirstappfromschool://callback";
    private Player mPlayer;
    private static final int REQUEST_CODE = 1337;
    private Button stopBtn;
    private Button playBtn;
    private ArrayList<String> lightList;
    private Button nextBtn;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private SpotifyService spotifyService;
    private ProgressDialog progressDialog;
    private TextView artistName, title;

    public Main2Activity() {
        lightList = new ArrayList<>();
        lightList.add("2dUueqpZlaGCxb8v1JHkb1");
        lightList.add("5487T2CENSpU6cO4oWnxS0");
        lightList.add("5cZFh5iWgNBZoTxrcIAd1k");
        lightList.add("5zoDY7K6X3iGESr6r0GCVK");
        lightList.add("1cobUl98CSA5jbsfbWhn9T");

    }

    public ArrayList<String> getLightList() {
        return lightList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logInInitiated();

        stopBtn = (Button)findViewById(R.id.stopBtn);
        playBtn = (Button)findViewById(R.id.playBtn);
        artistName = (TextView)findViewById(R.id.artistName);
        title = (TextView)findViewById(R.id.title);

        buttonsActions();

        //Accelerometer initiated
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                playMusic();
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
                        mPlayer.addConnectionStateCallback(Main2Activity.this);
                        mPlayer.addPlayerNotificationCallback(Main2Activity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }
                });
            }
        }
    }

    public void buttonsActions(){
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.pause();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic();
            }
        });

        nextBtn = (Button)findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic();
            }
        });
    }

    public void logInInitiated(){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    public void playMusic(){
        spotifyService = new SpotifyService(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // spotifyTrack is needed for the API
        String spotifyTrack = "spotify:track:";
        Collections.shuffle(lightList);

        mPlayer.play(spotifyTrack + lightList.get(0));
        spotifyService.writeTrack(lightList.get(0));

        progressDialog.hide();
    }

    @Override
    public void serviceSucces(Artist artist, Album album) {
        progressDialog.hide();

        artistName.setText("\u2981" + artist.getArtistName().getArtistName());
        title.setText(album.getTitle().getTitleName());
    }

    @Override
    public void serviceFailure(Exception exception) {
        progressDialog.hide();
        Toast.makeText(Main2Activity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoggedIn() {
        Toast.makeText(Main2Activity.this, "User logged in", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoggedOut() {
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Toast.makeText(Main2Activity.this, "\"Login failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTemporaryError() {
    }

    @Override
    public void onConnectionMessage(String s) {
    }

    @Override
    public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String s) {
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
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();

    }


}
