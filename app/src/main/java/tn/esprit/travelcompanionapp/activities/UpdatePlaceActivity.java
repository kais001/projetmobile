package tn.esprit.travelcompanionapp.activities;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tn.esprit.travelcompanionapp.R;
import tn.esprit.travelcompanionapp.database.RoomDB;
import tn.esprit.travelcompanionapp.models.Place;

public class UpdatePlaceActivity extends AppCompatActivity {

    private EditText nameInput, stateInput, descriptionInput, photoUrlInput, gpsCoordinatesInput;
    private Button updateButton;
    private int placeId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_place);

        nameInput = findViewById(R.id.nameInput);
        stateInput = findViewById(R.id.stateInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        photoUrlInput = findViewById(R.id.photoUrlInput);
        gpsCoordinatesInput = findViewById(R.id.gpsCoordinatesInput);
        updateButton = findViewById(R.id.updateButton);

        // Get the place ID from the intent
        placeId = getIntent().getIntExtra("placeId", -1);

        if (placeId == -1) {
            Toast.makeText(this, "Invalid place ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load the place details
        RoomDB db = RoomDB.getInstance(this);
        Place place = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            place = db.placeDAO().getAllPlaces().stream().filter(p -> p.getId() == placeId).findFirst().orElse(null);
        }

        if (place == null) {
            Toast.makeText(this, "Place not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate the fields
        nameInput.setText(place.getName());
        stateInput.setText(place.getState());
        descriptionInput.setText(place.getDescription());
        photoUrlInput.setText(place.getPhotoUrl());
        gpsCoordinatesInput.setText(place.getGpsCoordinates());

        // Handle update button click
        updateButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String state = stateInput.getText().toString();
            String description = descriptionInput.getText().toString();
            String photoUrl = photoUrlInput.getText().toString();
            String gpsCoordinates = gpsCoordinatesInput.getText().toString();

            if (name.isEmpty() || state.isEmpty() || gpsCoordinates.isEmpty()) {
                Toast.makeText(this, "Please fill out all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            db.placeDAO().update(placeId, name, state, description, photoUrl, gpsCoordinates);
            Toast.makeText(this, "Place updated successfully!", Toast.LENGTH_SHORT).show();

            // Redirect back to ShowPlacesActivity
            startActivity(new Intent(UpdatePlaceActivity.this, ShowPlacesActivity.class));
            finish();
        });
    }
}

