package com.example.qclothing;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager handles user login sessions using SharedPreferences.
 */
public class SessionManager {
    // Shared Preferences
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    
    // Shared preferences file name
    private static final String PREF_NAME = "QClothingPref";
    
    // Shared preferences keys
    private static final String IS_LOGGED_IN = "IsLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_IS_ADMIN = "isAdmin";
    
    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    /**
     * Create login session
     */
    public void createLoginSession(User user) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, user.getId());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putBoolean(KEY_IS_ADMIN, user.isAdmin());
        
        // Commit changes
        editor.commit();
    }
    
    /**
     * Check login method will check user login status
     * If not logged in, redirect to login page
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }
    
    /**
     * Get stored session data
     */
    public User getUserDetails() {
        if (!isLoggedIn()) {
            return null;
        }
        
        User user = new User();
        user.setId(pref.getLong(KEY_USER_ID, 0));
        user.setName(pref.getString(KEY_NAME, null));
        user.setEmail(pref.getString(KEY_EMAIL, null));
        user.setPhone(pref.getString(KEY_PHONE, null));
        user.setAdmin(pref.getBoolean(KEY_IS_ADMIN, false));
        
        return user;
    }
    
    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clear all data from shared preferences
        editor.clear();
        editor.commit();
    }
}