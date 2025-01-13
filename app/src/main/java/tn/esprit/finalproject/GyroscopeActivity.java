package tn.esprit.finalproject;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GyroscopeActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private TextView gyroscopeData;

    private final float[] rotationMatrix = new float[9];
    private final float[] orientation = new float[3];

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        // Initialize views
        gyroscopeData = findViewById(R.id.gyroscopeData);

        // Initialize SensorManager and Gyroscope Sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscopeSensor == null) {
            Toast.makeText(this, "Gyroscope not available!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener to listen to the gyroscope events
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener to stop listening when the activity is paused
        sensorManager.unregisterListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Get the gyroscope data (angular velocity)
            float xRotation = event.values[0];
            float yRotation = event.values[1];
            float zRotation = event.values[2];

            // Update the display with the rotation values
            gyroscopeData.setText("X: " + xRotation + "\nY: " + yRotation + "\nZ: " + zRotation);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used for this case
    }
}
