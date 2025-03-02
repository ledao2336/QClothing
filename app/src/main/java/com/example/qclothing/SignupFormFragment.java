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

import java.util.regex.Pattern;

public class SignupFormFragment extends Fragment {

    private EditText nameEditText;
    private EditText emailPhoneEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signupButton;
    
    private DatabaseManager databaseManager;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
            "@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
    );
    
    // Phone validation pattern (simple pattern for demonstration)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,11}$");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_form, container, false);

        nameEditText = view.findViewById(R.id.signup_name_edit_text);
        emailPhoneEditText = view.findViewById(R.id.signup_email_phone_edit_text);
        passwordEditText = view.findViewById(R.id.signup_password_edit_text);
        confirmPasswordEditText = view.findViewById(R.id.signup_confirm_password_edit_text);
        signupButton = view.findViewById(R.id.signup_button);
        
        // Initialize database manager
        databaseManager = new DatabaseManager(getContext());

        signupButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String emailPhone = emailPhoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Validate inputs
            if (!validateInputs(name, emailPhone, password, confirmPassword)) {
                return;
            }

            // Register user
            registerUser(name, emailPhone, password);
        });

        return view;
    }
    
    private boolean validateInputs(String name, String emailPhone, String password, String confirmPassword) {
        // Check if fields are empty
        if (name.isEmpty() || emailPhone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Check name length
        if (name.length() < 3) {
            Toast.makeText(getContext(), "Tên phải có ít nhất 3 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Check if email or phone is valid
        boolean isEmail = EMAIL_PATTERN.matcher(emailPhone).matches();
        boolean isPhone = PHONE_PATTERN.matcher(emailPhone).matches();
        
        if (!isEmail && !isPhone) {
            Toast.makeText(getContext(), "Email hoặc số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Check password length
        if (password.length() < 6) {
            Toast.makeText(getContext(), "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Mật khẩu nhập lại không trùng khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private void registerUser(String name, String emailPhone, String password) {
        try {
            // Open database connection
            databaseManager.open();
            
            // Check if user already exists
            User existingUser = databaseManager.getUserByEmailOrPhone(emailPhone);
            if (existingUser != null) {
                Toast.makeText(getContext(), "Email hoặc số điện thoại đã được đăng ký", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Determine if input is email or phone
            boolean isEmail = EMAIL_PATTERN.matcher(emailPhone).matches();
            
            // Register user
            long userId = databaseManager.addUser(
                    name,
                    isEmail ? emailPhone : "",  // Set email if it's an email
                    isEmail ? "" : emailPhone,  // Set phone if it's a phone
                    password,
                    false  // Regular user (not admin)
            );
            
            if (userId > 0) {
                // Registration successful
                Toast.makeText(getContext(), "Đăng ký thành công, vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                
                // Switch to login tab
                TabLayout tabLayout = getActivity().findViewById(R.id.login_signup_tabs);
                if (tabLayout != null) {
                    tabLayout.selectTab(tabLayout.getTabAt(0)); // Switch to Login tab (index 0)
                }
                
                // Clear form fields
                nameEditText.setText("");
                emailPhoneEditText.setText("");
                passwordEditText.setText("");
                confirmPasswordEditText.setText("");
            } else {
                // Registration failed
                Toast.makeText(getContext(), "Đăng ký thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
    }
}