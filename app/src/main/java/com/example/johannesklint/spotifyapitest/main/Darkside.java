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
import android.util.Log;
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
 * This acitivty is for when it's dark
 * Press "Play" to play the list
 * Press "Stop" to stop playinh
 * Press "Next" to change song OR shake the device
 */
public class Darkside extends MainActivity implements PlayerNotificationCallback, ConnectionStateCallback, SensorEventListener, APICallback {

    private static final String CLIENT_ID = "c630fe9a50b94f27ab408ae38e9e6fdc";
    private static final String REDIRECT_URI = "ourfirstappfromschool://callback";
    private Player mPlayer;
    private static final int REQUEST_CODE = 1337;
    private Button stopBtn;
    private Button playBtn;
    private ArrayList<String> darkList;
    private Button nextBtn;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private SpotifyService spotifyService;
    private ProgressDialog progressDialog;
    private TextView artistName, title;

    public Darkside() {
        darkList = new ArrayList<>();
        darkList.add("1brwdYwjltrJo7WHpIvbYt");
        darkList.add("3WEmAizxrtQwXEJQRwCrK1");
        darkList.add("3NBDgwEAGMj0aKRsU8zoO9");
        darkList.add("7fPHfBCyKE3aVCBjE4DAvl");
        darkList.add("2779sByIJexX6nBSXEJsVP");
    }

    public ArrayList<String> getDarkList() {
        return darkList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_darkside);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logInInitiated();

        stopBtn = (Button)findViewById(R.id.stopBtn);
        playBtn = (Button)findViewById(R.id.playBtn);
        nextBtn = (Button)findViewById(R.id.nextBtn);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

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

    public void playMusic(){
        spotifyService = new SpotifyService(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // spotifyTrack is needed for the API
        String spotifyTrack = "spotify:track:";
        Collections.shuffle(darkList);

        mPlayer.play(spotifyTrack + darkList.get(0));
        spotifyService.writeTrack(darkList.get(0));

        progressDialog.hide();
    }

    @Override
    public void serviceSucces(Artist artist, Album album) {
        progressDialog.hide();

        artistName.setText(artist.getArtistName().getArtistName());
        title.setText(album.getTitle().getTitleName());
    }

    @Override
    public void serviceFailure(Exception exception) {
        progressDialog.hide();
        Toast.makeText(Darkside.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLoggedIn() {
        Toast.makeText(Darkside.this, "User logged in", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoggedOut() {
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Toast.makeText(Darkside.this, "\"Login failed", Toast.LENGTH_SHORT).show();
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
            // Handle event type as necessary
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

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
