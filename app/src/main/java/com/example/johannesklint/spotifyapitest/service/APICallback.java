package com.example.johannesklint.spotifyapitest.service;

import com.example.johannesklint.spotifyapitest.data.Album;
import com.example.johannesklint.spotifyapitest.data.Artist;

/**
 * Created by johannesklint on 16-02-16.
 */
public interface APICallback {
    void serviceSucces(Artist artist, Album album);
    void serviceFailure(Exception exception);
}
