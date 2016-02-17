package com.example.johannesklint.spotifyapitest.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by johannesklint on 16-02-16.
 */
public class Artist implements JsonPopulator {

    private ArtistName artistName;

    public ArtistName getArtistName() {
        return artistName;
    }

    @Override
    public void populate(JSONObject data) {
        artistName = new ArtistName();
        JSONArray temp = data.optJSONArray("artists");
        try {
            artistName.populate(temp.getJSONObject(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
