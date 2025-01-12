package tn.esprit.finalproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Locale;

public class StepCounterF extends Fragment implements SensorEventListener {
    private Button pauseButton;
    private Sensor stepCounterSensor;
    private SensorManager sensorManager;
    private ProgressBar progressBar;
    private boolean isPaused = false;
    private TextView timeText;
    private TextView distanceText;
    private TextView stepsText;
    private TextView stepCountTargetText;
    private long timePaused = 0;
    private float stepsLengthInMeter = 0.762f; // Longueur d'un pas en mètres
    private long startTime;
    private int stepCountTarget = 5000; // Objectif de nombre de pas
    private int stepCount = 0;
    private Handler timeHandler = new Handler();

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int min = seconds / 60;
            seconds = seconds % 60;
            timeText.setText(String.format(Locale.getDefault(), "Time: %02d:%02d", min, seconds));
            timeHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        // Désinscrire le capteur lorsque l'activité est arrêtée
        if (stepCounterSensor != null) {
            sensorManager.unregisterListener(this);
            timeHandler.removeCallbacks(timeRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Enregistrer le capteur lorsque l'activité est reprise
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
            timeHandler.postDelayed(timeRunnable, 0);
        } else {
            Log.e("StepCounterF", "Step counter sensor not available!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_counter, container, false);

        stepsText = view.findViewById(R.id.stepsText);
        distanceText = view.findViewById(R.id.distanceText);
        timeText = view.findViewById(R.id.timeText);
        pauseButton = view.findViewById(R.id.pauseButton);
        stepCountTargetText = view.findViewById(R.id.StepCountTarget);
        progressBar = view.findViewById(R.id.progressBar);

        startTime = System.currentTimeMillis();
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        progressBar.setMax(stepCountTarget);

        // Vérifier si le capteur est disponible
        if (stepCounterSensor == null) {
            stepCountTargetText.setText("Step counter: not available");
        } else {
            stepCountTargetText.setText("Step Goal: " + stepCountTarget);
        }

        // Configurer le bouton de pause
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseButtonClicked(v);
            }
        });

        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            // Obtenez le nombre de pas depuis le capteur
            stepCount = (int) sensorEvent.values[0];
            Log.d("StepCounterF", "Steps detected: " + stepCount);

            // Mettre à jour l'affichage des étapes
            stepsText.setText("Step Count: " + stepCount);
            progressBar.setProgress(stepCount);

            // Vérifier si l'objectif de pas est atteint
            if (stepCount >= stepCountTarget) {
                stepCountTargetText.setText("Step Goal Achieved");
            }

            // Calculer la distance parcourue en km
            float distanceInKm = stepCount * stepsLengthInMeter / 1000;
            distanceText.setText(String.format(Locale.getDefault(), "Distance: %.2f km", distanceInKm));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Gestion des changements de précision si nécessaire
    }

    public void onPauseButtonClicked(View view) {
        if (isPaused) {
            // Reprendre le comptage des pas
            isPaused = false;
            pauseButton.setText("Pause");
            startTime = System.currentTimeMillis() - timePaused;
            timeHandler.postDelayed(timeRunnable, 0);
        } else {
            // Pauser le comptage des pas
            isPaused = true;
            pauseButton.setText("Resume");
            timeHandler.removeCallbacks(timeRunnable);
            timePaused = System.currentTimeMillis() - startTime;
        }
    }
}
