package tn.esprit.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.concurrent.Executor;

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.User;

public class LoginFragment extends Fragment {

    private CompassView compassView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Check if user is already logged in
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false); // Check login status

        // If user is logged in, navigate directly to UserHomeFragment
        if (isLoggedIn) {
            navigateUserToHome();
            return view;
        }

        // UI Elements
        EditText emailEditText = view.findViewById(R.id.emailEditText);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        Button loginButton = view.findViewById(R.id.loginButton);
        TextView signUpTextView = view.findViewById(R.id.signUpTextView);
        Button fingerprintLoginButton = view.findViewById(R.id.fingerprintLoginButton);
        ImageView togglePasswordVisibility = view.findViewById(R.id.togglePasswordVisibility);

        // Initialize Compass
        compassView = view.findViewById(R.id.compassView);
        compassView.start();

        // Toggle Password Visibility
        togglePasswordVisibility.setOnClickListener(v -> togglePasswordVisibility(passwordEditText, togglePasswordVisibility));

        // Handle Login
        loginButton.setOnClickListener(v -> handleLogin(emailEditText, passwordEditText));

        // Fingerprint Login
        fingerprintLoginButton.setOnClickListener(v -> startBiometricAuthentication());

        // Navigate to SignUpFragment
        signUpTextView.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new SignUpFragment());
            }
        });

        return view;
    }

    private void togglePasswordVisibility(EditText passwordEditText, ImageView togglePasswordVisibility) {
        if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on);
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private void handleLogin(EditText emailEditText, EditText passwordEditText) {
        String identifier = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (identifier.equals("sys") && password.equals("sys")) {
            Toast.makeText(getActivity(), "Admin Login Successful!", Toast.LENGTH_SHORT).show();

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", "sys");
            editor.putString("password", "sys");
            editor.putString("email", "Administrator Account");
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            navigateToHome();
        } else {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                User user = db.userDao().findByEmailOrUsername(identifier);

                requireActivity().runOnUiThread(() -> {
                    if (user == null || !user.getPassword().equals(password)) {
                        Toast.makeText(getActivity(), "Invalid credentials!", Toast.LENGTH_SHORT).show();
                    } else {
                        updateUserPreferences(user);
                        Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);  // Mark user as logged in
                        editor.apply();

                        navigateUserToHome();
                    }
                });
            }).start();
        }
    }

    private void updateUserPreferences(User user) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("id", user.getId());
        editor.putString("email", user.getEmail());
        editor.putString("username", user.getUsername());
        editor.putString("password", user.getPassword());
        editor.apply();

        Log.d("UserProfileFragment", "Saved userId: " + user.getId());
    }

    private void startBiometricAuthentication() {
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
                Executor executor = ContextCompat.getMainExecutor(requireContext());
                BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        handleBiometricSuccess();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        Toast.makeText(requireContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        Toast.makeText(requireContext(), "Authentication failed!", Toast.LENGTH_SHORT).show();
                    }
                });

                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Login with Fingerprint")
                        .setSubtitle("Place your finger on the sensor")
                        .setNegativeButtonText("Cancel")
                        .build();

                biometricPrompt.authenticate(promptInfo);
            } else {
                Toast.makeText(requireContext(), "Fingerprint not supported or enabled on this device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleBiometricSuccess() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String savedEmail = sharedPreferences.getString("email", null);
            String savedUsername = sharedPreferences.getString("username", null);
            String savedPassword = sharedPreferences.getString("password", null);

            if ("sys".equals(savedUsername) && "sys".equals(savedPassword)) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Admin Biometric Login Successful!", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                });
                return;
            }

            AppDatabase db = AppDatabase.getInstance(requireContext());
            User user = db.userDao().findByUsername(savedUsername);

            requireActivity().runOnUiThread(() -> {
                if (user != null && user.getEmail().equals(savedEmail)) {
                    Toast.makeText(requireContext(), "Biometric Login Successful!", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);  // Mark user as logged in
                    editor.apply();

                    navigateUserToHome();
                } else {
                    handleBiometricError(user);
                }
            });
        }).start();
    }

    private void handleBiometricError(User user) {
        String message = (user == null) ? "Account no longer exists. Please log in manually." : "Data mismatch. Please log in manually.";
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        if (user == null) {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
        }
        navigateToLogin();
    }

    private void navigateToLogin() {
        replaceFragment(new LoginFragment());
    }

    private void navigateToHome() {
        replaceFragment(new HomeFragment());
    }

    private void navigateUserToHome() {
        replaceFragment(new UserHomeFragment());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (compassView != null) {
            compassView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (compassView != null) {
            compassView.stop();
        }
    }
}
