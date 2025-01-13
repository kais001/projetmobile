package tn.esprit.cov;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tn.esprit.cov.Entity.Ride;
import tn.esprit.cov.Database.AppDatabase;

public class AddRideActivity extends AppCompatActivity {

    private EditText editTextRideName, editTextDate, editTextDestination, editTextLatitude, editTextLongitude;
    private Button buttonAddRide;
    private RecyclerView recyclerViewRides;
    private AppDatabase db;
    private RideAdapter rideAdapter;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride);

        // Initialize views
        editTextRideName = findViewById(R.id.editTextRideName);
        editTextDate = findViewById(R.id.editTextDate);
        editTextDestination = findViewById(R.id.editTextDestination);
        editTextLatitude = findViewById(R.id.edit_text_latitude);
        editTextLongitude = findViewById(R.id.edit_text_longitude);
        buttonAddRide = findViewById(R.id.buttonAddRide);
        recyclerViewRides = findViewById(R.id.recyclerViewRides);

        // Initialize the Room database and ExecutorService
        db = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Set up RecyclerView with an empty list initially
        recyclerViewRides.setLayoutManager(new LinearLayoutManager(this));
        rideAdapter = new RideAdapter(new ArrayList<>(), new RideAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(Ride ride) {
                deleteRide(ride);
            }
        }, new RideAdapter.OnUpdateClickListener() {
            @Override
            public void onUpdateClick(Ride ride) {
                // Show a dialog or populate the fields with current ride details for editing
                showUpdateDialog(ride);
            }
        });
        recyclerViewRides.setAdapter(rideAdapter);

        // Load the initial list of rides asynchronously
        loadRides();

        // Set the click listener for the add ride button
        buttonAddRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRide();
            }
        });
    }

    private void deleteRide(Ride ride) {
        // Perform database deletion on a background thread
        new Thread(() -> {
            db.rideDao().deleteRide(ride);
            runOnUiThread(() -> {
                Toast.makeText(AddRideActivity.this, "Ride deleted successfully!", Toast.LENGTH_SHORT).show();
                updateRideList(); // Refresh the RecyclerView
            });
        }).start();
    }

    private void updateRideList() {
        // Fetch the list of rides from the database on a background thread
        executorService.execute(() -> {
            List<Ride> rideList = db.rideDao().getAllRides();

            // Update the RecyclerView on the main thread
            runOnUiThread(() -> rideAdapter.updateRideList(rideList));
        });
    }

    private void addRide() {
        // Get the user input
        String name = editTextRideName.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String destination = editTextDestination.getText().toString().trim();
        String latitudeStr = editTextLatitude.getText().toString().trim();
        String longitudeStr = editTextLongitude.getText().toString().trim();

        // Validate the input
        if (name.isEmpty() || date.isEmpty() || destination.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            Toast.makeText(AddRideActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate latitude and longitude as Double
        double latitude = 0.0;
        double longitude = 0.0;
        try {
            latitude = Double.parseDouble(latitudeStr);
            longitude = Double.parseDouble(longitudeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(AddRideActivity.this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Ride object
        Ride ride = new Ride();
        ride.setName(name);
        ride.setDate(date);
        ride.setDestination(destination);
        ride.setLatitude(latitude);
        ride.setLongitude(longitude);

        // Insert the ride into the database on a background thread
        executorService.execute(() -> {
            db.rideDao().insertRide(ride);

            // Update the RecyclerView with the new data on the main thread
            runOnUiThread(() -> {
                Toast.makeText(AddRideActivity.this, "Ride added successfully!", Toast.LENGTH_SHORT).show();
                loadRides(); // Refresh the list of rides
            });
        });
    }

    private void showUpdateDialog(Ride ride) {
        // Pre-fill the input fields with current ride details
        editTextRideName.setText(ride.getName());
        editTextDate.setText(ride.getDate());
        editTextDestination.setText(ride.getDestination());
        editTextLatitude.setText(String.valueOf(ride.getLatitude()));
        editTextLongitude.setText(String.valueOf(ride.getLongitude()));

        // Update the button text to "Update" and set the onClickListener for updating the ride
        buttonAddRide.setText("Update Ride");

        // Handle the update functionality
        buttonAddRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRide(ride);
            }
        });
    }

    private void updateRide(Ride ride) {
        // Get the user input
        String name = editTextRideName.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String destination = editTextDestination.getText().toString().trim();
        String latitudeStr = editTextLatitude.getText().toString().trim();
        String longitudeStr = editTextLongitude.getText().toString().trim();

        // Validate the input
        if (name.isEmpty() || date.isEmpty() || destination.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            Toast.makeText(AddRideActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate latitude and longitude as Double
        double latitude = 0.0;
        double longitude = 0.0;
        try {
            latitude = Double.parseDouble(latitudeStr);
            longitude = Double.parseDouble(longitudeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(AddRideActivity.this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the ride object
        ride.setName(name);
        ride.setDate(date);
        ride.setDestination(destination);
        ride.setLatitude(latitude);
        ride.setLongitude(longitude);

        // Update the ride in the database on a background thread
        executorService.execute(() -> {
            db.rideDao().updateRide(ride);

            // Update the RecyclerView with the updated data on the main thread
            runOnUiThread(() -> {
                Toast.makeText(AddRideActivity.this, "Ride updated successfully!", Toast.LENGTH_SHORT).show();
                loadRides(); // Refresh the list of rides
                resetForm(); // Reset form fields
            });
        });
    }

    private void loadRides() {
        // Fetch the list of rides from the database on a background thread
        executorService.execute(() -> {
            List<Ride> rideList = db.rideDao().getAllRides();

            // Update the RecyclerView on the main thread
            runOnUiThread(() -> rideAdapter.updateRideList(rideList));
        });
    }

    private void resetForm() {
        // Clear input fields and reset button text
        editTextRideName.setText("");
        editTextDate.setText("");
        editTextDestination.setText("");
        editTextLatitude.setText("");
        editTextLongitude.setText("");
        buttonAddRide.setText("Add Ride");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shutdown the ExecutorService to release resources
    }
}
