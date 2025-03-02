package com.example.qclothing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

public class SignupFormFragment extends Fragment {

    private EditText nameEditText;
    private EditText emailPhoneEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signupButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_form, container, false);

        nameEditText = view.findViewById(R.id.signup_name_edit_text);
        emailPhoneEditText = view.findViewById(R.id.signup_email_phone_edit_text);
        passwordEditText = view.findViewById(R.id.signup_password_edit_text);
        confirmPasswordEditText = view.findViewById(R.id.signup_confirm_password_edit_text);
        signupButton = view.findViewById(R.id.signup_button);

        signupButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String emailPhone = emailPhoneEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Basic validation (add more robust validation)
            if (name.isEmpty() || emailPhone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getContext(), "Điền vào thông tin sau", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Mật khẩu không giống nhau", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulated Signup Logic (replace with real signup)
            Toast.makeText(getContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
            // In a real app, you would create a user account, etc.
            // For now, maybe switch back to the login tab programmatically
            TabLayout tabLayout = getActivity().findViewById(R.id.login_signup_tabs); // Get TabLayout from LoginActivity
            if (tabLayout != null) {
                tabLayout.selectTab(tabLayout.getTabAt(0)); // Switch to Login tab (index 0)
            }
        });

        return view;
    }
}