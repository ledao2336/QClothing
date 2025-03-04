package com.example.qclothing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Declare and initialize clothingItemList as public static
    public static List<ClothingItem> clothingItemList = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Initialize database
        initializeDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            // Load ShoppingFragment initially
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ShoppingFragment());
            transaction.commit();
        }
    }

    private void initializeDatabase() {
        DatabaseManager dbManager = null;
        try {
            // Initialize DatabaseHelper
            databaseHelper = new DatabaseHelper(this);

            // Prepare database (create or copy from assets if needed)
            databaseHelper.prepareDataBase();

            // Initialize DatabaseManager
            dbManager = new DatabaseManager(this);

            // Open database connection
            dbManager.open();

            // Initialize database with sample data if empty
            dbManager.initDatabase();

            // Ensure default users exist (admin and demo)
            dbManager.ensureDefaultUsersExist();

            // Load clothing items to static list for backward compatibility
            clothingItemList = dbManager.getAllClothingItems();

            Log.d(TAG, "Database initialized successfully. Loaded " + clothingItemList.size() + " items.");
        } catch (SQLException | IOException e) {
            Log.e(TAG, "Error initializing database", e);
            Toast.makeText(this, "Error initializing database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            // Close database connection
            if (dbManager != null) {
                dbManager.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Update menu items based on login status
        MenuItem loginItem = menu.findItem(R.id.action_login_signup);
        MenuItem logoutItem = menu.findItem(R.id.action_logout);

        if (sessionManager.isLoggedIn()) {
            // User is logged in, show logout option
            loginItem.setVisible(false);
            logoutItem.setVisible(true);

            // Get current user and update title if needed
            User currentUser = sessionManager.getUserDetails();
            if (currentUser != null && getSupportActionBar() != null) {
                getSupportActionBar().setTitle("QClothing - " + currentUser.getName());
            }
        } else {
            // User is not logged in, show login option
            loginItem.setVisible(true);
            logoutItem.setVisible(false);

            // Reset title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("QClothing");
            }
        }

        return true;
    }

    private void updateMenuItems(Menu menu) {
        if (menu != null) {
            MenuItem loginItem = menu.findItem(R.id.action_login_signup);
            MenuItem logoutItem = menu.findItem(R.id.action_logout);

            if (sessionManager.isLoggedIn()) {
                // User is logged in, show logout option
                if (loginItem != null) loginItem.setVisible(false);
                if (logoutItem != null) logoutItem.setVisible(true);

                // Get current user and update title if needed
                User currentUser = sessionManager.getUserDetails();
                if (currentUser != null && getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("QClothing - " + currentUser.getName());
                }
            } else {
                // User is not logged in, show login option
                if (loginItem != null) loginItem.setVisible(true);
                if (logoutItem != null) logoutItem.setVisible(false);

                // Reset title
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("QClothing");
                }
            }
        }
    }

    // Update onResume to refresh menu when returning to activity
    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the menu to reflect current login state
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cart) {
            Intent cartIntent = new Intent(this, CartActivity.class);
            startActivity(cartIntent);
            return true;
        } else if (id == R.id.action_login_signup) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            return true;
        } else if (id == R.id.action_logout) {
            // Log out the user
            sessionManager.logoutUser();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            // Refresh the menu
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}