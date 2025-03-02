package com.example.qclothing;

import android.content.Intent;
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

import com.example.qclothing.R;

public class LoginFormFragment extends Fragment {

    private EditText emailPhoneEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_form, container, false);

        emailPhoneEditText = view.findViewById(R.id.login_email_phone_edit_text);
        passwordEditText = view.findViewById(R.id.login_password_edit_text);
        loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            String emailPhone = emailPhoneEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            // Basic validation (add more robust validation)
            if (emailPhone.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please enter email/phone and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulated Login Logic (replace with real authentication)
            if (emailPhone.equals("admin@example.com") && password.equals("adminpass")) {
                // Admin login
                Intent adminIntent = new Intent(getContext(), AdminActivity.class);
                startActivity(adminIntent);
                getActivity().finish(); // Close LoginActivity
            } else if (emailPhone.equals("user@example.com") && password.equals("password")) {
                // Regular user login
                Intent mainIntent = new Intent(getContext(), MainActivity.class);
                startActivity(mainIntent);
                getActivity().finish(); // Close LoginActivity
            } else {
                // Login failed
                Toast.makeText(getContext(), "Đăng nhập thất bại, không có người dùng.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}