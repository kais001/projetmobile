package com.example.mobhoussem;


import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;
    private String loggedInUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        frameLayout = findViewById(R.id.frameH);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        loggedInUsername = getIntent().getStringExtra("Email");

        // Set up initial fragment
        replaceFragment(new StepCounterF());  // Display all accommodations by default

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.allAccommodations) {
                // Navigate to All_AccommodationF Fragment
                replaceFragment(new All_EventF());
            }  else if (itemId == R.id.myNote) {
                // Navigate to AddAccommodationF Fragment
                replaceFragment(new All_EventFclient());
            }  else if (itemId == R.id.addacc) {
                // Navigate to RegisterActivity via an Intent
                Intent registerIntent = new Intent(HomeActivity.this, AddEventActivity.class);
                startActivity(registerIntent);  // Launch RegisterActivity
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameH, fragment);
        fragmentTransaction.commit();
    }
}
