package com.example.johannesklint.spotifyapitest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class About extends MainActivity {

    private TextView header, footer, names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        header = (TextView)findViewById(R.id.header);
        footer = (TextView)findViewById(R.id.footer);
        names = (TextView)findViewById(R.id.names);
        header.setText("Get The Mood");
        footer.setText("Developed by");
        names.setText("Marlon Jakobsson & Johannes Klint");


    }



}
