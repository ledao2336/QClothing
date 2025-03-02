package com.example.qclothing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);

        // Initialize session manager
        sessionManager = new SessionManager(this);
        databaseManager = new DatabaseManager(this);

        // Check if user is logged in and is admin
        if (!sessionManager.isLoggedIn() || !sessionManager.getUserDetails().isAdmin()) {
            // Redirect to login
            redirectToLogin();
            return;
        }

        bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Load AddItemFragment as default admin fragment
        if (savedInstanceState == null) {
            loadFragment(new AddItemFragment());
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();

                    if (itemId == R.id.admin_add_item) {
                        selectedFragment = new AddItemFragment();
                    } else if (itemId == R.id.admin_edit_item) {
                        selectedFragment = new EditItemFragment();
                    } else if (itemId == R.id.admin_remove_item) {
                        selectedFragment = new RemoveItemFragment();
                    } else if (itemId == R.id.admin_customer_lookup) {
                        selectedFragment = new CustomerLookupFragment();
                    } else if (itemId == R.id.admin_orders) {
                        selectedFragment = new OrdersFragment();
                    }

                    if (selectedFragment != null) {
                        loadFragment(selectedFragment);
                        return true;
                    }
                    return false;
                }
            };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.admin_fragment_container, fragment);
        transaction.commit();
    }

    private void redirectToLogin() {
        Toast.makeText(this, "Bạn cần đăng nhập với tài khoản quản trị", Toast.LENGTH_SHORT).show();

        // Clear session
        sessionManager.logoutUser();

        // Redirect to login
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.admin_logout) {
            // Logout
            sessionManager.logoutUser();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            // Redirect to login
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}