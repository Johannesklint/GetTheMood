package com.example.johannesklint.spotifyapitest.data;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by johannesklint on 16-02-16.
 */
public class ArtistName implements JsonPopulator {

    private String artistName;

    public String getArtistName() {
        return artistName;
    }

    @Override
    public void populate(JSONObject data) {
        Log.v("JSONRESULT ArtistName", data.toString());
        artistName = data.optString("name");
        Log.v("JSONRESULT String",artistName);
    }
}
