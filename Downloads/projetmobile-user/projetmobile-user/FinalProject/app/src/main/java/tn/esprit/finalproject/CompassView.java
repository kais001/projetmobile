package tn.esprit.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class CompassView extends View implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magnetometer;
    private Sensor accelerometer;
    private float[] gravity;
    private float[] geomagnetic;

    private Bitmap compassNeedle;
    private float degree = 0f;

    private static final float ALPHA = 0.115f; // Adjust for smoothness (0 < ALPHA <= 1)

    private float[] applyLowPassFilter(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }


    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Load the compass needle image
        compassNeedle = BitmapFactory.decodeResource(context.getResources(), R.drawable.compass_needle);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = applyLowPassFilter(event.values.clone(), gravity);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = applyLowPassFilter(event.values.clone(), geomagnetic);
        }
        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                degree = (float) Math.toDegrees(orientation[0]);
                invalidate();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        // Draw a circular background
        @SuppressLint("DrawAllocation") Paint paint = new Paint();
        paint.setColor(0xFFE0E0E0); // Light gray color background
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle((float) width /2, (float) height / 2, (float) Math.min(width, height) / 2, paint);

        // Position the compass needle in the center of the view
        canvas.translate((float) width / 2, (float) height / 2);
        canvas.rotate(-degree); // Rotate the needle based on the degree

        // Scale the compass needle
        float scaleFactor = Math.min(width, height) / (float) compassNeedle.getWidth();

        int needleWidth = (int) (compassNeedle.getWidth() * scaleFactor);
        int needleHeight = (int) (compassNeedle.getHeight() * scaleFactor);
        @SuppressLint("DrawAllocation") Bitmap scaledNeedle = Bitmap.createScaledBitmap(compassNeedle, needleWidth, needleHeight, true);

        // Draw the compass needle
        @SuppressLint("DrawAllocation")
        Rect destRect = new Rect(-scaledNeedle.getWidth() / 2, -scaledNeedle.getHeight() / 2,
                scaledNeedle.getWidth() / 2, scaledNeedle.getHeight() / 2);
        canvas.drawBitmap(scaledNeedle, null, destRect, null);
    }


    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }
}
