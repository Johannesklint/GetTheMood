package com.example.johannesklint.spotifyapitest.data;

import org.json.JSONObject;

/**
 * Created by johannesklint on 16-02-16.
 */
public class Album implements JsonPopulator{
    private Title title;

    public Title getTitle() {
        return title;
    }

    @Override
    public void populate(JSONObject data) {
        title = new Title();
        title.populate(data.optJSONObject("album"));
    }
}
