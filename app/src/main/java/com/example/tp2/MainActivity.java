package com.example.tp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button flickrActivityBtn = findViewById(R.id.buttonFlickrActivity);
        Button authenticateActivityBtn = findViewById(R.id.buttonAuthenticateActivity);
        Button listActivityBtn = findViewById(R.id.buttonListActivity);

        flickrActivityBtn.setOnClickListener(v -> {
            Intent flickrActivityIntent = new Intent(this, FlickrActivity.class);
            startActivity(flickrActivityIntent);
        });

        authenticateActivityBtn.setOnClickListener(v -> {
            Intent authenticateActivityIntent = new Intent(this, AuthenticationActivity.class);
            startActivity(authenticateActivityIntent);
        });

        listActivityBtn.setOnClickListener(v -> {
            Intent listActivityIntent = new Intent(this, ListActivity.class);
            startActivity(listActivityIntent);
        });
    }
}