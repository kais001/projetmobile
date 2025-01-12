package com.example.devmob;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Locale;
public class CompassToAccFragment extends Fragment implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor magneticSensor, accelerometerSensor;
    private float[] gravity;
    private float[] geomagnetic;
    private TextView directionText;
    private ImageView compassImage;

    private double accommodationLat, accommodationLon;

    public CompassToAccFragment() {
        // Required empty public constructor
    }

    // Ajouter cette méthode newInstance pour passer les coordonnées
    public static CompassToAccFragment newInstance(double latitude, double longitude) {
        CompassToAccFragment fragment = new CompassToAccFragment();
        Bundle args = new Bundle();
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compass_to_mecca, container, false);
        directionText = view.findViewById(R.id.directionText);
        compassImage = view.findViewById(R.id.compassImage); // Initialize the ImageView here

        // Récupérer les coordonnées de l'accommodation depuis le Bundle
        if (getArguments() != null) {
            accommodationLat = getArguments().getDouble("latitude");
            accommodationLon = getArguments().getDouble("longitude");
        }

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        return view;  // Return the view correctly
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register listeners for the sensors
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listeners to prevent memory leaks
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }

        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, null, gravity, geomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);

                float azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth + 360) % 360;

                // Utiliser les coordonnées de l'accommodation pour calculer l'azimut
                double currentLat = getCurrentLatitude();
                double currentLon = getCurrentLongitude();
                float accommodationAzimuth = calculateBearing(currentLat, currentLon, accommodationLat, accommodationLon);

                float angleDifference = azimuth - accommodationAzimuth;
                if (angleDifference < 0) angleDifference += 360;

                // Update the direction text (optional)
                directionText.setText(String.format(Locale.getDefault(), "Angle to Accommodation: %.2f°", angleDifference));

                // Rotate the compass image based on the angle difference
                if (compassImage != null) {
                    compassImage.setRotation(angleDifference);  // Rotate the compass based on the angle
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle sensor accuracy changes if necessary
    }

    // Simulated function to get the current latitude (replace with actual GPS data)
    private double getCurrentLatitude() {
        return 21.3891; // Exemple: latitude de l'utilisateur
    }

    // Simulated function to get the current longitude (replace with actual GPS data)
    private double getCurrentLongitude() {
        return 39.8579; // Exemple: longitude de l'utilisateur
    }

    // Fonction pour calculer l'azimut entre deux coordonnées
    private float calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        double lonDiff = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double y = Math.sin(lonDiff) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lonDiff);
        double bearing = Math.atan2(y, x);
        bearing = Math.toDegrees(bearing);
        return (float) ((bearing + 360) % 360);  // Normalize to [0, 360)
    }
}
