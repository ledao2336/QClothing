package com.example.qclothing;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static String DB_PATH = ""; // Will be updated in constructor
    private static String DB_NAME = "qclothing.db"; // Your database file name
    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    // New flag to control database initialization
    private static final int FORCE_REINITIALIZE = 1; // Use 1 to force reinitialize, 0 to use assets

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1); // Database name, factory (null), version
        this.mContext = context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/"; // Path to internal storage databases

        // Create databases directory if it doesn't exist
        File dbDir = new File(DB_PATH);
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
    }

    // Method to update or create database if needed
    public void updateDataBase() throws IOException {
        if (mNeedUpdate || FORCE_REINITIALIZE == 1) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists()) {
                dbFile.delete(); // Delete old database if exists
            }

            if (FORCE_REINITIALIZE == 1) {
                // If flag is 1, create a new database using initDatabase method
                createEmptyDatabase();
            } else {
                // Otherwise, try to copy from assets
                copyDataBase();
            }

            mNeedUpdate = false;
        }
    }

    // Create an empty database that will be populated programmatically
    private void createEmptyDatabase() throws IOException {
        try {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);

            // Create tables manually when creating a new database
            createTables(db);

            db.close();
            Log.d(TAG, "Empty database created successfully with tables");
        } catch (Exception e) {
            Log.e(TAG, "Error creating empty database", e);
            throw new IOException("Error creating empty database");
        }
    }

    // Add a method to create tables that can be used both in initialization and upgrade
    private void createTables(SQLiteDatabase db) {
        try {
            // Create users table
            db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "email TEXT," +
                    "phone TEXT," +
                    "password TEXT NOT NULL," +
                    "is_admin INTEGER DEFAULT 0)");

            // Create clothing table
            db.execSQL("CREATE TABLE IF NOT EXISTS quanao (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "itemId TEXT UNIQUE NOT NULL," +
                    "name TEXT NOT NULL," +
                    "description TEXT," +
                    "price REAL NOT NULL," +
                    "imageUrl TEXT," +
                    "category TEXT)");

            // Create cart table
            db.execSQL("CREATE TABLE IF NOT EXISTS cart (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "item_id TEXT NOT NULL," +
                    "quantity INTEGER DEFAULT 1," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (item_id) REFERENCES quanao(itemId))");

            // Create orders table
            db.execSQL("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "order_date INTEGER NOT NULL," +
                    "total REAL NOT NULL," +
                    "status TEXT DEFAULT 'pending'," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");

            // Create order_items table
            db.execSQL("CREATE TABLE IF NOT EXISTS order_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "order_id INTEGER NOT NULL," +
                    "item_id TEXT NOT NULL," +
                    "quantity INTEGER DEFAULT 1," +
                    "price REAL NOT NULL," +
                    "FOREIGN KEY (order_id) REFERENCES orders(id)," +
                    "FOREIGN KEY (item_id) REFERENCES quanao(itemId))");

            Log.d(TAG, "Tables created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating tables", e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Call the method to create tables when the database is first created
        createTables(db);
    }

    // Check if database needs to be updated
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    // Copy database from assets folder to application's database folder
    private void copyDataBase() throws IOException {
        try {
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
            Log.d(TAG, "Database copied successfully from assets");
        } catch (IOException e) {
            Log.e(TAG, "Error copying database from assets", e);

            // If there's no database in assets, create an empty database
            this.getReadableDatabase();
            Log.d(TAG, "Created empty database instead");
            throw e; // Re-throw to inform caller about the issue
        }
    }

    // Prepare database for use (create or update if needed)
    public void prepareDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            Log.d(TAG, "Database exists, checking version");
            int dbVersion = getVersionId(); // Check database version if needed
            int appDbVersion = 1; // Your app's database version

            // Force update if flag is set
            if (FORCE_REINITIALIZE == 1 || appDbVersion > dbVersion) {
                Log.d(TAG, "Database needs update, current version: " + dbVersion);
                mNeedUpdate = true;
                updateDataBase();
            }
        } else {
            Log.d(TAG, "Database doesn't exist, creating new one");
            // Database doesn't exist, create it
            this.getReadableDatabase(); // Create empty database in system folder
            close();
            try {
                // If flag is 1, create an empty database to be initialized programmatically
                if (FORCE_REINITIALIZE == 1) {
                    createEmptyDatabase();
                } else {
                    // Otherwise, try to copy from assets
                    copyDataBase();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error copying/creating database", e);
            }
        }
    }

    // Create database tables manually if needed
    private void createTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Create users table
            db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "email TEXT," +
                    "phone TEXT," +
                    "password TEXT NOT NULL," +
                    "is_admin INTEGER DEFAULT 0)");

            // Create clothing table
            db.execSQL("CREATE TABLE IF NOT EXISTS quanao (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "itemId TEXT UNIQUE NOT NULL," +
                    "name TEXT NOT NULL," +
                    "description TEXT," +
                    "price REAL NOT NULL," +
                    "imageUrl TEXT," +
                    "category TEXT)");

            // Create cart table
            db.execSQL("CREATE TABLE IF NOT EXISTS cart (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "item_id TEXT NOT NULL," +
                    "quantity INTEGER DEFAULT 1," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (item_id) REFERENCES quanao(itemId))");

            // Create orders table
            db.execSQL("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "order_date INTEGER NOT NULL," +
                    "total REAL NOT NULL," +
                    "status TEXT DEFAULT 'pending'," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");

            // Create order_items table
            db.execSQL("CREATE TABLE IF NOT EXISTS order_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "order_id INTEGER NOT NULL," +
                    "item_id TEXT NOT NULL," +
                    "quantity INTEGER DEFAULT 1," +
                    "price REAL NOT NULL," +
                    "FOREIGN KEY (order_id) REFERENCES orders(id)," +
                    "FOREIGN KEY (item_id) REFERENCES quanao(itemId))");

            Log.d(TAG, "Tables created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating tables", e);
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

    // Method to get database version (if you have version info in your database)
    private int getVersionId() {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
            int version = db.getVersion();
            db.close();
            return version;
        } catch (Exception e) {
            Log.e(TAG, "Error getting database version", e);
            return 0;
        }
    }
}