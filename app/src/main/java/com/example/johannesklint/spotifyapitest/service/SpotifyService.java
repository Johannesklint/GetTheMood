package com.example.johannesklint.spotifyapitest.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.example.johannesklint.spotifyapitest.data.Album;
import com.example.johannesklint.spotifyapitest.data.Artist;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by johannesklint on 16-02-16.
 */
public class SpotifyService extends Service {

    private APICallback apiCallback;
    private String trackID;
    private Exception error;

    public SpotifyService(APICallback apiCallback) {
        this.apiCallback = apiCallback;
    }

    public String getTrackID() {
        return trackID;
    }

    public void writeTrack(final String track){
        trackID = track;

        new AsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... params) {

                String endPoint = String.format("https://api.spotify.com/v1/tracks/%s", track);

                try {
                    URL url = new URL(endPoint);

                    URLConnection urlConnection = url.openConnection();

                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder result = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        result.append(line);
                    }
                    return result.toString();

                } catch (MalformedURLException e) {
                    error = e;
                    return null;
                } catch (IOException e) {
                    error = e;
                    return null;
                }
            }

            @Override
            protected  void onPostExecute(String s){
                if(s == null && error != null){
                    apiCallback.serviceFailure(error);
                    return;
                }
                JSONObject data = null;
                try {
                    data = new JSONObject(s);

                    JSONObject queryResults = data;

                    Album album = new Album();
                    Artist artist = new Artist();

                    album.populate(queryResults);
                    artist.populate(queryResults);

                    apiCallback.serviceSucces(artist, album);
                } catch (JSONException e) {
                    apiCallback.serviceFailure(e);
                }

            }
        }.execute();
    }

    public class SpotifyException extends Exception {
        public SpotifyException(String msg){
            super(msg);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
