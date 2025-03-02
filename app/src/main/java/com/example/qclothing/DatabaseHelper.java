package com.example.qclothing;

import android.content.Context;
import android.database.SQLException;  // Changed from java.sql.SQLException
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = ""; // Will be updated in constructor
    private static String DB_NAME = "qclothing.db"; // Your database file name
    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1); // Database name, factory (null), version
        this.mContext = context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/"; // Path to internal storage databases
    }

    // Method to update or create database if needed
    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists()) {
                dbFile.delete(); // Delete old database if exists
            }
            copyDataBase(); // Copy new database from assets
            mNeedUpdate = false;
        }
    }

    // Check if database needs to be updated
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    // Copy database from assets folder to application's database folder
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    // Prepare database for use (create or update if needed)
    public void prepareDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            int dbVersion = getVersionId(); // Optional: Check database version if needed
            int appDbVersion = 1; // Your app's database version
            if (appDbVersion > dbVersion) { // Optional: Compare versions and update if app version is newer
                mNeedUpdate = true;
                updateDataBase();
            }
        } else {
            // Database doesn't exist, copy it from assets
            this.getReadableDatabase(); // Create empty database in system folder
            copyDataBase();
        }
    }

    // Open database for reading/writing
    public SQLiteDatabase openDataBase() throws SQLException, IOException {
        String path = DB_PATH + DB_NAME;
        if (mDataBase == null) {
            prepareDataBase(); // Ensure database is prepared before opening
            String mPath = DB_PATH + DB_NAME;
            mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return mDataBase;
    }

    // Close database connection
    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // No need to create database here, we are copying an existing one
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed in the future
        Log.w("DatabaseHelper", "Database version upgrade from " + oldVersion + " to " + newVersion);
        mNeedUpdate = true; // Trigger database update on next open
        try {
            updateDataBase(); // Update database
        } catch (IOException e) {
            e.printStackTrace(); // Log exception during upgrade
        }
    }

    // Optional: Method to get database version (if you have version info in your database)
    private int getVersionId() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        int version = db.getVersion();
        db.close();
        return version;
    }
}