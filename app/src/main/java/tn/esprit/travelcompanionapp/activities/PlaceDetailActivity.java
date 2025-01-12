package tn.esprit.travelcompanionapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import tn.esprit.travelcompanionapp.R;
import tn.esprit.travelcompanionapp.database.RoomDB;
import tn.esprit.travelcompanionapp.models.Place;

public class PlaceDetailActivity extends AppCompatActivity {

    private ImageView placeImage;
    private TextView placeName;
    private TextView placeState;
    private TextView placeDescription;
    private TextView placeGpsCoordinates;
    private Button updateButton;
    private Button deleteButton;
    private Button gyroscopeButton;

    private int placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        // Initialize views
        placeImage = findViewById(R.id.placeImaged);
        placeName = findViewById(R.id.placeNamed);
        placeState = findViewById(R.id.placeStated);
        placeDescription = findViewById(R.id.placeDescriptiond);
        placeGpsCoordinates = findViewById(R.id.placeGpsCoordinatesd);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        gyroscopeButton = findViewById(R.id.gyroscopeButton);

        // Get the Place object passed from the previous activity
        Intent intent = getIntent();
        Place place = (Place) intent.getSerializableExtra("place");

        if (place != null) {
            // Set data
            placeId = place.getId();
            Glide.with(this)
                    .load(place.getPhotoUrl()) // Assuming you use Glide for image loading
                    .into(placeImage);

            placeName.setText(place.getName());
            placeState.setText(place.getState());
            placeDescription.setText(place.getDescription());
            placeGpsCoordinates.setText(place.getGpsCoordinates());
        }

        // Handle Update button click
        updateButton.setOnClickListener(v -> {
            Intent updateIntent = new Intent(PlaceDetailActivity.this, UpdatePlaceActivity.class);
            updateIntent.putExtra("placeId", placeId);
            startActivity(updateIntent);
        });

        // Handle Delete button click
        deleteButton.setOnClickListener(v -> {
            RoomDB db = RoomDB.getInstance(this);
            db.placeDAO().delete(placeId);  // Delete place from the database
            Toast.makeText(PlaceDetailActivity.this, "Place deleted successfully!", Toast.LENGTH_SHORT).show();
            // Redirect to ShowPlacesActivity or previous screen
            startActivity(new Intent(PlaceDetailActivity.this, ShowPlacesActivity.class));
            finish();
        });

        gyroscopeButton.setOnClickListener(v -> {
            Intent gyroscopeIntent = new Intent(PlaceDetailActivity.this, GyroscopeActivity.class);
            startActivity(gyroscopeIntent);
        });
    }
}
