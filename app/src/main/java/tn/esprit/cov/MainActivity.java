package tn.esprit.cov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;

public class MainActivity extends AppCompatActivity {

    private Button buttonAdmin, buttonUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Configuration.getInstance().setUserAgentValue("Cov/1.0");

        // Initialize buttons
        buttonAdmin = findViewById(R.id.buttonAdmin);
        buttonUser = findViewById(R.id.buttonUser);

        // Set the click listeners for the buttons
        buttonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the admin view (AddRideActivity or similar)
                Intent intent = new Intent(MainActivity.this, AddRideActivity.class);
                startActivity(intent);
            }
        });

        buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the user view (ViewRidesActivity)
                Intent intent = new Intent(MainActivity.this, ViewRidesActivity.class);
                startActivity(intent);
            }
        });
    }
}
