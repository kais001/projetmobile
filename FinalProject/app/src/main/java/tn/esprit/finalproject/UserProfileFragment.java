package tn.esprit.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.User;

public class UserProfileFragment extends Fragment implements SensorEventListener {

    private DrawerLayout drawerLayout;
    private TextView usernameTextView, emailTextView;
    private NavigationView navigationView;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float SHAKE_THRESHOLD = 12.0f; // Adjust this threshold
    private long lastShakeTime = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_user, container, false);

        // Initialize views
        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        Button editButton = view.findViewById(R.id.editButton);
        Button deleteButton = view.findViewById(R.id.deleteUserButton);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);

        // Set up toolbar with toggle for drawer
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("User Profile");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle navigation menu item clicks
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Fetch and display user data
        fetchAndDisplayUserData();

        // Set up button listeners
        editButton.setOnClickListener(v -> editUserProfile());
        deleteButton.setOnClickListener(v -> deleteUserAccount());

        // Dynamically update navigation header
        updateNavigationHeader();

        // Initialize the SensorManager and accelerometer
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNavigationHeader();

        // Register the accelerometer listener
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the accelerometer listener
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calculate the magnitude of acceleration
            double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            // Check if acceleration exceeds the shake threshold
            if (acceleration > SHAKE_THRESHOLD) {
                long currentTime = System.currentTimeMillis();

                // Avoid multiple shakes in quick succession
                if (currentTime - lastShakeTime > 1000) { // 1-second gap
                    lastShakeTime = currentTime;
                    showLogoutPopup();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // required to implement SensorEventListener
    }

    private void showLogoutPopup() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout Confirmation")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logoutUser())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void logoutUser() {
        requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("isLoggedIn", false)
                .apply();
        navigateToLoginFragment();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            navigateToHomeFragment();
        } else if (id == R.id.nav_user_profile) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fetchAndDisplayUserData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Default User");
        String email = sharedPreferences.getString("email", "default@example.com");

        usernameTextView.setText(username);
        emailTextView.setText(email);
    }

    private void navigateToHomeFragment() {
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new UserHomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToLoginFragment() {
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new LoginFragment());
        transaction.commit();
    }

    @SuppressLint("SetTextI18n")
    private void updateNavigationHeader() {
        if (navigationView != null && navigationView.getHeaderCount() > 0) {
            View headerView = navigationView.getHeaderView(0);
            TextView navHeaderTitle = headerView.findViewById(R.id.nav_header_title);
            TextView navHeaderSubtitle = headerView.findViewById(R.id.nav_header_subtitle);

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "Default User");
            String email = sharedPreferences.getString("email", "default@example.com");

            navHeaderTitle.setText("Hello, " + username + "!");
            navHeaderSubtitle.setText(email);
        }
    }

    private void editUserProfile() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        long id = sharedPreferences.getLong("id", -1); // Retrieve id
        String username = sharedPreferences.getString("username", "Default User");
        String email = sharedPreferences.getString("email", "default@example.com");

        if (id == -1) {
            Toast.makeText(requireContext(), "Error: User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("UserProfileFragment", "Editing profile for id: " + id);

        // Pass the correct user data to the dialog
        UpdateUserDialogFragment dialogFragment = UpdateUserDialogFragment.newInstance(
                new User(id, email, username, "") // Ensure id is set
        );

        dialogFragment.setOnUserUpdatedListener(updatedUser -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                try {
                    // Update the user in the Room database
                    db.userDao().update(updatedUser);

                    // Verify the update by querying the updated user
                    User updatedUserFromDb = db.userDao().findById(updatedUser.getId());
                    requireActivity().runOnUiThread(() -> {
                        if (updatedUserFromDb != null &&
                                updatedUserFromDb.getUsername().equals(updatedUser.getUsername()) &&
                                updatedUserFromDb.getEmail().equals(updatedUser.getEmail())) {

                            // Update SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", updatedUser.getUsername());
                            editor.putString("email", updatedUser.getEmail());
                            editor.apply();

                            fetchAndDisplayUserData(); // Refresh UI
                            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Update verification failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e("UserProfileFragment", "Error during update or verification", e);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "An error occurred while updating profile.", Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();
        });

        dialogFragment.show(getChildFragmentManager(), "update_user");
    }

    private void deleteUserAccount() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "default@default.default");

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            User user = db.userDao().findByEmail(email);

            if (user != null) {
                db.userDao().delete(user); // Delete the user from Room database
                sharedPreferences.edit().clear().apply(); // Clear preferences after account deletion

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Account deleted. Logging out...", Toast.LENGTH_SHORT).show();
                    navigateToLoginFragment();
                });
            }
        }).start();
    }
}
