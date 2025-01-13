package tn.esprit.finalproject;

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

import com.google.android.gms.maps.MapView;



import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.Ride;

public class UserMapActivity extends AppCompatActivity {
   /* private long lastLocationUpdateTime = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView; // Correct MapView import
    private LocationManager locationManager;
    private GeoPoint userLocation;
    private Marker userMarker;
    private AppDatabase db; // Room database instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialisation de la configuration osmdroid
        Configuration.getInstance().load(getApplicationContext(),
                android.preference.PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.user_map);

        // Initialize mapView
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
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
        mapView.onResume(); // Ensure the MapView resumes properly when activity is resumed*/
    }
