package tn.esprit.finalproject;

import android.os.Bundle;
import android.text.InputType;
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

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.User;

public class SignUpFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

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
                // Check if email already exists in Room database
                checkIfEmailExistsAndSaveUser(email, username, password);
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

    private void saveUserToRoom(String email, String username, String password) {
        User user = new User(null, email, username, password);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        new Thread(() -> db.userDao().insert(user)).start();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void checkIfEmailExistsAndSaveUser(String email, String username, String password) {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        new Thread(() -> {
            User existingUser = db.userDao().findByEmail(email);
            if (existingUser != null) {
                // If user already exists with the same email, show a Toast message
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Email is already in use", Toast.LENGTH_SHORT).show();
                });
            } else {
                // If email is not taken, proceed to save the new user
                saveUserToRoom(email, username, password);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "User registered successfully!", Toast.LENGTH_SHORT).show();

                    // Redirect to login fragment
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).loadFragment(new LoginFragment());
                    }
                });
            }
        }).start();
    }
}
