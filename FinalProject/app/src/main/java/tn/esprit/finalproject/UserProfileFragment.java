package tn.esprit.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.User;

public class UserProfileFragment extends Fragment {

    private DrawerLayout drawerLayout;
    private TextView usernameTextView, emailTextView;
    private NavigationView navigationView;

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

        return view;
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

        // Display the data
        usernameTextView.setText(username);
        emailTextView.setText(email);
    }

    private void editUserProfile() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Default User");
        String email = sharedPreferences.getString("email", "default@example.com");

        // Pass the correct user data to the dialog
        UpdateUserDialogFragment dialogFragment = UpdateUserDialogFragment.newInstance(
                new User("", email, username, "", true)
        );

        dialogFragment.setOnUserUpdatedListener(updatedUser -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                db.userDao().update(updatedUser);
                updateFirestoreUser(updatedUser);

                requireActivity().runOnUiThread(() -> {
                    fetchAndDisplayUserData(); // Refresh UI
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                });
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
                db.userDao().delete(user);
                sharedPreferences.edit().clear().apply(); // Clear preferences after account deletion

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Account deleted. Logging out...", Toast.LENGTH_SHORT).show();
                    navigateToLoginFragment();
                });
            }
        }).start();
    }

    private void logoutUser() {
        requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
        navigateToLoginFragment();
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

    private void updateFirestoreUser(User updatedUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", updatedUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("username", updatedUser.getUsername());
                        updatedData.put("email", updatedUser.getEmail());
                        updatedData.put("emailVerified", updatedUser.isEmailVerified());
                        updatedData.put("password", updatedUser.getPassword());

                        document.getReference().update(updatedData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Firestore updated successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Failed to update Firestore", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(requireContext(), "User not found in Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error accessing Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
