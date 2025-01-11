package tn.esprit.finalproject;

import android.os.Bundle;
import android.os.Handler;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.User;

public class SignUpFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private Handler handler;
    private int checkAttempts = 0;
    private static final int MAX_ATTEMPTS = 10;
    private static final int ATTEMPT_DELAY = 3000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        handler = new Handler();

        EditText usernameEditText = view.findViewById(R.id.usernameEditText);
        EditText emailEditText = view.findViewById(R.id.signUpEmailEditText);
        EditText signUpPasswordEditText = view.findViewById(R.id.signUpPasswordEditText);
        EditText confirmsignUpPasswordEditText = view.findViewById(R.id.confirmsignUpPasswordEditText);
        Button signUpButton = view.findViewById(R.id.signUpButton);
        TextView backToLoginTextView = view.findViewById(R.id.backToLoginTextView);
        ImageView togglePasswordVisibility = view.findViewById(R.id.togglePasswordVisibility);
        ImageView toggleConfirmPasswordVisibility = view.findViewById(R.id.toggleConfirmPasswordVisibility);

        togglePasswordVisibility.setOnClickListener(v -> togglePasswordVisibility(signUpPasswordEditText, togglePasswordVisibility));
        toggleConfirmPasswordVisibility.setOnClickListener(v -> togglePasswordVisibility(confirmsignUpPasswordEditText, toggleConfirmPasswordVisibility));

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String password = signUpPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmsignUpPasswordEditText.getText().toString().trim();

            if (validateInput(email, username, password, confirmPassword)) {
                registerUser(email, username, password);
            }
        });

        backToLoginTextView.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new LoginFragment());
            }
        });

        return view;
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon) {
        if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_visibility_on);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_visibility_off);
        }
        editText.setSelection(editText.getText().length());
    }

    private boolean validateInput(String email, String username, String password, String confirmPassword) {
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidEmail(email)) {
            Toast.makeText(getActivity(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(getActivity(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.matches(".*[A-Z].*")) {
            Toast.makeText(getActivity(), "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~].*")) {
            Toast.makeText(getActivity(), "Password must contain at least one special character", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser(String email, String username, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user.getUid(), email, username, password);
                            saveUserToRoom(user.getUid(), email, username, password);
                            sendVerificationEmail(user);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Sign-up failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToRoom(String userId, String email, String username, String password) {
        User user = new User(userId, email, username, password, false);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        new Thread(() -> db.userDao().insert(user)).start();
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(verificationTask -> {
                    if (verificationTask.isSuccessful()) {
                        Toast.makeText(getActivity(), "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                        startEmailVerificationCheck(user);
                    } else {
                        Toast.makeText(getActivity(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startEmailVerificationCheck(FirebaseUser user) {
        checkAttempts = 0;
        handler.postDelayed(() -> checkEmailVerificationStatus(user), ATTEMPT_DELAY);
    }

    private void checkEmailVerificationStatus(FirebaseUser user) {
        if (user != null) {
            user.reload()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (user.isEmailVerified()) {
                                updateEmailVerifiedStatus(user.getUid());
                                updateEmailVerifiedInRoom(user.getUid(), true);
                            } else if (checkAttempts < MAX_ATTEMPTS) {
                                checkAttempts++;
                                handler.postDelayed(() -> checkEmailVerificationStatus(user), ATTEMPT_DELAY);
                            } else {
                                // Safely check if the fragment is still attached
                                if (isAdded() && getActivity() != null) {
                                    Toast.makeText(getActivity(), "Verification email expired or failed.", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e("SignUpFragment", "Fragment not attached, cannot show Toast.");
                                }
                            }
                        } else {
                            Log.e("Reload Error", "Error reloading user", task.getException());
                        }
                    });
        }
    }


    private void updateEmailVerifiedInRoom(String userId, boolean isVerified) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            User user = db.userDao().findByUserId(userId);
            if (user != null) {
                user.setEmailVerified(isVerified);
                db.userDao().update(user);
            } else {
                Log.e("Room", "User not found in Room database");
            }
        }).start();
    }

    private void updateEmailVerifiedStatus(String userId) {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("isEmailVerified", true);

        firestore.collection("users")
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Email verified status updated in Firestore");
                    Toast.makeText(getActivity(), "Email verified! You can now log in.", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating email verification status", e));
    }

    private void saveUserToFirestore(String userId, String email, String username, String password) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("username", username);
        userMap.put("password", password);
        userMap.put("isEmailVerified", false);

        firestore.collection("users").document(userId)
                .set(userMap)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data successfully saved"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving user data", e));
    }

    private void navigateToLogin() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onResume() {
        super.onResume();
        //checkEmailVerificationAndUpdateFirestore();
    }

    private void checkEmailVerificationAndUpdateFirestore() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.reload()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && user.isEmailVerified()) {
                            updateEmailVerifiedStatus(user.getUid());
                        }
                    });
        }
    }
}
