package tn.esprit.cov.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tn.esprit.cov.Entity.Ride;
import tn.esprit.cov.R;

public class AddRideFragment extends Fragment {

    private double latitude;
    private double longitude;
    private tn.esprit.cov.Database.AppDatabase database;
    private LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_ride, container, false);

        EditText nameEditText = view.findViewById(R.id.edit_text_name);
        EditText dateEditText = view.findViewById(R.id.edit_text_date);
        EditText destinationEditText = view.findViewById(R.id.edit_text_destination);
        Button saveButton = view.findViewById(R.id.button_save);

        // Initialize database instance
        database = tn.esprit.cov.Database.AppDatabase.getInstance(requireContext());

        // Initialize LocationManager
        locationManager = (LocationManager) requireContext().getSystemService(requireContext().LOCATION_SERVICE);

        // Get the location when the fragment is created
        getLocation();

        // Save button listener
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String destination = destinationEditText.getText().toString();

            if (name.isEmpty() || date.isEmpty() || destination.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new Ride object
            Ride ride = new Ride();
            ride.setName(name);
            ride.setDate(date);
            ride.setDestination(destination);

            // Assign latitude and longitude to the Ride object
            ride.setLatitude(latitude);
            ride.setLongitude(longitude);

            // Insert the ride in a background thread
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                database.rideDao().insertRide(ride);

                // Update UI on the main thread
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Ride added successfully", Toast.LENGTH_SHORT).show());
            });
        });

        return view;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // Update latitude and longitude values
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(@NonNull String provider) {}

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    Toast.makeText(requireContext(), "Please enable GPS", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
