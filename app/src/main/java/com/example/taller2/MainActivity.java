package com.example.taller2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.cameraButton).setOnClickListener(v -> {
            startActivity(new Intent(this, ImageActivity.class));
        });
        findViewById(R.id.contactsButton).setOnClickListener(v -> {
            startActivity(new Intent(this, ContactsActivity.class));
        });
        findViewById(R.id.mapsButton).setOnClickListener(v -> {
            startActivity(new Intent(this, MapsActivity.class));
        });
    }
}