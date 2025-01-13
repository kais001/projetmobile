package tn.esprit.cov;



import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import tn.esprit.cov.Fragment.HomeFragment;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // Load the default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }
}

