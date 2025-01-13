package tn.esprit.cov;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import org.osmdroid.api.IMapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import tn.esprit.cov.Database.AppDatabase;
import tn.esprit.cov.Entity.Ride;

public class UserMapActivity extends AppCompatActivity {
private long lastLocationUpdateTime =0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final double PROXIMITY_THRESHOLD_METERS = 10.0;
    private MapView mapView;
    private LocationManager locationManager;
    private GeoPoint userLocation;
    private Marker userMarker;
    private AppDatabase db; // Room database instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_map);

        // Initialize mapView
        mapView = findViewById(R.id.map);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setMinZoomLevel(3.0);
        mapView.setMaxZoomLevel(19.0);

        // Set initial map controller (default view)
        IMapController mapController = mapView.getController();
        mapController.setZoom(10);
        mapController.setCenter(new GeoPoint(0, 0));

        // Initialize LocationManager for GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        db = AppDatabase.getInstance(this); // Initialize Room database

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                startLocationUpdates();
            }
        }

        // Add markers asynchronously from the database
        addMarkers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume(); // Ensure the MapView resumes properly when activity is resumed
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause(); // Ensure the MapView pauses properly when activity is paused
    }

    private void addMarkers() {
        // Create an executor for background tasks
        Executor executor = Executors.newSingleThreadExecutor();

        // Run the database query on the background thread
        executor.execute(() -> {
            // Get all rides from the Room database
            List<Ride> rides = db.rideDao().getAllRides(); // Assuming the DAO method fetches the list synchronously

            // Update UI after data is fetched (run on the main thread)
            runOnUiThread(() -> {
                if (rides != null) {
                    mapView.getOverlays().clear();  // Clear existing markers before adding new ones
                    for (Ride ride : rides) {
                        double latitude = ride.getLatitude();
                        double longitude = ride.getLongitude();
                        String rideName = ride.getName();  // Get the ride name (assuming a method like this exists)

                        // Validate the coordinates
                        if (latitude >= -85.05112877980658 && latitude <= 85.05112877980658 &&
                                longitude >= -180.0 && longitude <= 180.0) {
                            GeoPoint point = new GeoPoint(latitude, longitude);
                            Marker marker = new Marker(mapView);
                            marker.setPosition(point);

                            // Set the title to display the ride name
                            if (rideName != null && !rideName.isEmpty()) {
                                marker.setTitle("Ride: " + rideName);
                            } else {
                                marker.setTitle("Ride Location");
                            }

                            mapView.getOverlays().add(marker);
                        } else {
                            Log.e("UserMap", "Invalid coordinates for ride: (" + latitude + ", " + longitude + ")");
                        }
                    }
                    mapView.invalidate();  // Refresh the map view
                }

            });
        });
    }

    private void updateUserLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        GeoPoint newLocation = new GeoPoint(latitude, longitude);

        // Log the coordinates to verify the user location is being updated
        Log.d("UserMap", "Updating user location: " + latitude + ", " + longitude);

        // Initialize marker if it doesn't exist
        if (userMarker == null) {
            userMarker = new Marker(mapView);
            Drawable icon = getResources().getDrawable(android.R.drawable.ic_menu_compass);
            userMarker.setIcon(icon);
            userMarker.setTitle("You are here");
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            // Log to verify the marker is being created
            Log.d("UserMap", "Creating user marker.");
            mapView.getOverlays().add(userMarker);
        }

        // Update the marker position
        userLocation = newLocation;
        userMarker.setPosition(userLocation);

        // Log the marker position update
        Log.d("UserMap", "User marker position updated.");

        // Center the map on the user location
        IMapController mapController = mapView.getController();
        mapController.setZoom(15);  // Set a closer zoom level
        mapController.setCenter(userLocation);  // Center the map on the user's location

        // Refresh the map view
        mapView.invalidate();
    }

    private void startLocationUpdates() {
        try {
            String provider = null;

            // Check if GPS or network provider is enabled
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;
                Log.d("UserMap", "Using GPS provider");
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
                Log.d("UserMap", "Using Network provider");
            } else {
                Toast.makeText(this, "No location provider available", Toast.LENGTH_SHORT).show();
                Log.e("UserMap", "No location provider available");
                return;
            }

            locationManager.requestLocationUpdates(provider, 1000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Log the location update to verify if it works
                    Log.d("UserMap", "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
                    updateUserLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // Log provider status changes
                    Log.d("UserMap", "Provider status changed: " + provider + " Status: " + status);
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.d("UserMap", "Provider enabled: " + provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.d("UserMap", "Provider disabled: " + provider);
                }
            });
        } catch (SecurityException e) {
            Log.e("UserMap", "SecurityException: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
