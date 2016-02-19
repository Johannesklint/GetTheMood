package com.example.johannesklint.spotifyapitest.main;

import android.util.Log;

/**
 * Created by johannesklint on 16-02-19.
 */
public class UnitTest {

    Darkside darkside;
    Main2Activity main2Activity;

    public UnitTest() {
        darkside = new Darkside();
        main2Activity = new Main2Activity();
    }

    public int countTracks(int getNumberOfTracksDarkside, int getNumberOfTracksLightside){
        getNumberOfTracksDarkside = darkside.getDarkList().size();
        getNumberOfTracksLightside = main2Activity.getLightList().size();

        int result = getNumberOfTracksDarkside + getNumberOfTracksLightside;

        return result;

    }

}
