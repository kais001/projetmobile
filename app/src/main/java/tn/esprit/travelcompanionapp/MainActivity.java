package tn.esprit.travelcompanionapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import tn.esprit.travelcompanionapp.activities.ShowPlacesActivity;

public class MainActivity extends AppCompatActivity {

    private Button campingManagementButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        campingManagementButton = findViewById(R.id.campingManagementButton);

        campingManagementButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ShowPlacesActivity.class));
        });
    }
}
