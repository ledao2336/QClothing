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

public class LoginFormFragment extends Fragment {

    private EditText emailPhoneEditText;
    private EditText passwordEditText;
    private Button loginButton;
    
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_form, container, false);

        emailPhoneEditText = view.findViewById(R.id.login_email_phone_edit_text);
        passwordEditText = view.findViewById(R.id.login_password_edit_text);
        loginButton = view.findViewById(R.id.login_button);
        
        // Initialize database and session managers
        databaseManager = new DatabaseManager(getContext());
        sessionManager = new SessionManager(getContext());
        
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            User user = sessionManager.getUserDetails();
            navigateBasedOnUserRole(user);
        }

        loginButton.setOnClickListener(v -> {
            String emailPhone = emailPhoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Basic validation
            if (emailPhone.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Authenticate user
            authenticateUser(emailPhone, password);
        });

        return view;
    }
    
    private void authenticateUser(String emailPhone, String password) {
        try {
            // Open database connection
            databaseManager.open();
            
            // Authenticate user
            User user = databaseManager.authenticateUser(emailPhone, password);
            
            if (user != null) {
                // Create user session
                sessionManager.createLoginSession(user);
                
                // Show success message
                Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                
                // Navigate based on user role
                navigateBasedOnUserRole(user);
            } else {
                // Login failed
                Toast.makeText(getContext(), "Đăng nhập thất bại, tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Đăng nhập thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            
            // Fallback to hardcoded credentials for demo
            if (emailPhone.equals("admin@example.com") && password.equals("adminpass")) {
                // Admin login
                User adminUser = new User();
                adminUser.setId(1);
                adminUser.setName("Admin");
                adminUser.setEmail("admin@example.com");
                adminUser.setAdmin(true);
                
                // Create session
                sessionManager.createLoginSession(adminUser);
                
                Intent adminIntent = new Intent(getContext(), AdminActivity.class);
                startActivity(adminIntent);
                getActivity().finish();
            } else if (emailPhone.equals("user@example.com") && password.equals("password")) {
                // Regular user login
                User regularUser = new User();
                regularUser.setId(2);
                regularUser.setName("User");
                regularUser.setEmail("user@example.com");
                regularUser.setAdmin(false);
                
                // Create session
                sessionManager.createLoginSession(regularUser);
                
                // Navigate to cart or previous screen
                if (getActivity().getIntent().hasExtra("REDIRECT_TO_CART")) {
                    Intent cartIntent = new Intent(getContext(), CartActivity.class);
                    startActivity(cartIntent);
                } else {
                    Intent mainIntent = new Intent(getContext(), MainActivity.class);
                    startActivity(mainIntent);
                }
                getActivity().finish();
            } else {
                // Login failed
                Toast.makeText(getContext(), "Đăng nhập thất bại, tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
    }
    
    private void navigateBasedOnUserRole(User user) {
        if (user.isAdmin()) {
            // Navigate to admin screen
            Intent adminIntent = new Intent(getContext(), AdminActivity.class);
            startActivity(adminIntent);
        } else {
            // Check if we need to redirect to cart
            if (getActivity().getIntent().hasExtra("REDIRECT_TO_CART")) {
                Intent cartIntent = new Intent(getContext(), CartActivity.class);
                startActivity(cartIntent);
            } else {
                // Navigate to main screen
                Intent mainIntent = new Intent(getContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        }
        
        // Close login activity
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}