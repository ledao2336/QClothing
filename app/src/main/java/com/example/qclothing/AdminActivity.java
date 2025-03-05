package com.example.qclothing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.qclothing.LoginActivity;
import com.example.qclothing.R;
import com.example.qclothing.AddItemFragment;
import com.example.qclothing.EditItemFragment;
import com.example.qclothing.RemoveItemFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Load AddItemFragment as default admin fragment
        loadFragment(new AddItemFragment());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu); // admin_menu.xml
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.admin_logout) {
            // Create a proper SessionManager instance
            SessionManager sessionManager = new SessionManager(this);
            // Call logout method
            sessionManager.logoutUser();

            // Show feedback to the user
            Toast.makeText(AdminActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            // Navigate back to LoginActivity
            Intent loginIntent = new Intent(AdminActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish(); // Close AdminActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}