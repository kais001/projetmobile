package tn.esprit.finalproject;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StepCounterActivity extends AppCompatActivity {

    private TextView textViewStepCount, textViewDistance, textViewTime;
    private double MagnitudePrevious = 0;
    private Integer stepCount = 0;
    private double stepLength = 0.8;  // Longueur moyenne d'un pas en mètres
    private long startTime;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private long elapsedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        textViewStepCount = findViewById(R.id.textViewStepCount);
        textViewDistance = findViewById(R.id.textViewDistance);
        textViewTime = findViewById(R.id.textViewTime);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent != null) {
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];

                    double Magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
                    double MagnitudeDelta = Magnitude - MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    // Réduire la sensibilité en abaissant le seuil
                    if (MagnitudeDelta > 3) {  // Sensibilité réduite
                        stepCount++;
                    }

                    // Mettre à jour l'affichage
                    textViewStepCount.setText(String.valueOf(stepCount));
                    double distance = stepCount * stepLength;  // Calcul de la distance
                    textViewDistance.setText(String.format("%.2f m", distance));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Initialiser le timer
        startTime = System.currentTimeMillis();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                elapsedTime = currentTime - startTime;
                long seconds = elapsedTime / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;

                // Mettre à jour l'affichage du temps
                textViewTime.setText(String.format("%02d:%02d", minutes, seconds));

                // Redémarrer le timer
                timerHandler.postDelayed(this, 1000);
            }
        };

        // Démarrer le timer
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepCount", stepCount);  // Saving the step count
        editor.putLong("elapsedTime", elapsedTime);  // Saving elapsed time
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepCount", stepCount);  // Saving the step count
        editor.putLong("elapsedTime", elapsedTime);  // Saving elapsed time
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("stepCount", 0);  // Retrieving the saved step count
        elapsedTime = sharedPreferences.getLong("elapsedTime", 0);  // Retrieving the saved elapsed time

        // Mettre à jour l'affichage avec les valeurs récupérées
        textViewStepCount.setText(String.valueOf(stepCount));
        double distance = stepCount * stepLength;  // Calcul de la distance
        textViewDistance.setText(String.format("%.2f m", distance));

        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        textViewTime.setText(String.format("%02d:%02d", minutes, seconds));

        // Redémarrer le timer
        startTime = System.currentTimeMillis() - elapsedTime;
        timerHandler.postDelayed(timerRunnable, 0);
    }
}
