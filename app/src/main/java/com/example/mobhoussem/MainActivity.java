package com.example.mobhoussem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.anim);

        // Find the ImageView
        ImageView ringImageView = findViewById(R.id.imageView4);

        // Start the animation on the ImageView
        ringImageView.startAnimation(rotateAnimation);

        // Delay for 2 seconds before transitioning
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the HomeActivity after the animation
                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeIntent);

                // Close the MainActivity (optional)
                finish();
            }
        }, 2000); // 2000 ms delay (2 seconds)
    }
}
