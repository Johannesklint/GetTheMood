package com.example.johannesklint.spotifyapitest.data;

import org.json.JSONObject;

/**
 * Created by johannesklint on 16-02-16.
 */
public class Title implements JsonPopulator{

    private String titleName;

    public String getTitleName() {
        return titleName;
    }

    @Override
    public void populate(JSONObject data) {
        titleName = data.optString("name");
    }
}
