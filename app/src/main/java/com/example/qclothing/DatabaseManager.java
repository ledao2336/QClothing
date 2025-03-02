package com.example.qclothing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager provides CRUD operations and manages database transactions
 * for the clothing app.
 */
public class DatabaseManager {
    private static final String TAG = "DatabaseManager";
    
    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CLOTHING = "quanao";
    public static final String TABLE_CART = "cart";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_ORDER_ITEMS = "order_items";
    
    // Common Column Names
    public static final String COLUMN_ID = "id";
    
    // Users Table Columns
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_IS_ADMIN = "is_admin";
    
    // Clothing Table Columns
    public static final String COLUMN_ITEM_ID = "itemId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE_URL = "imageUrl";
    public static final String COLUMN_CATEGORY = "category";
    
    // Cart Table Columns
    public static final String COLUMN_CART_USER_ID = "user_id";
    public static final String COLUMN_CART_ITEM_ID = "item_id";
    public static final String COLUMN_CART_QUANTITY = "quantity";
    
    // Orders Table Columns
    public static final String COLUMN_ORDER_USER_ID = "user_id";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_ORDER_TOTAL = "total";
    public static final String COLUMN_ORDER_STATUS = "status";
    
    // Order Items Table Columns
    public static final String COLUMN_ORDER_ITEM_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_ITEM_ITEM_ID = "item_id";
    public static final String COLUMN_ORDER_ITEM_QUANTITY = "quantity";
    public static final String COLUMN_ORDER_ITEM_PRICE = "price";
    
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;
    
    // Constructor
    public DatabaseManager(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }
    
    // Open database connection
    public void open() throws SQLException, IOException {
        database = dbHelper.openDataBase();
    }
    
    // Close database connection
    public void close() {
        dbHelper.close();
    }
    
    // Check if database is open
    public boolean isOpen() {
        return database != null && database.isOpen();
    }
    
    // ====== USER OPERATIONS ======
    
    // Add a new user


    public void ensureDefaultUsersExist() {
        try {
            // Check for admin user
            User adminUser = getUserByEmailOrPhone("admin@example.com");
            if (adminUser == null) {
                Log.d(TAG, "Admin user not found, creating...");
                addUser("Admin", "admin@example.com", "", "adminpass", true);
            } else {
                Log.d(TAG, "Admin user exists with ID: " + adminUser.getId());
            }

            // Check for demo user
            User demoUser = getUserByEmailOrPhone("user@example.com");
            if (demoUser == null) {
                Log.d(TAG, "Demo user not found, creating...");
                addUser("Demo User", "user@example.com", "", "password", false);
            } else {
                Log.d(TAG, "Demo user exists with ID: " + demoUser.getId());
            }

            // List all users for verification
            debugListAllUsers();
        } catch (Exception e) {
            Log.e(TAG, "Error ensuring default users exist: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lists all users in the database (for debugging purposes)
     */
    public void debugListAllUsers() {
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_USERS, null);

            Log.d(TAG, "Total users in database: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_USER_NAME);
                int emailIndex = cursor.getColumnIndex(COLUMN_USER_EMAIL);
                int phoneIndex = cursor.getColumnIndex(COLUMN_USER_PHONE);
                int isAdminIndex = cursor.getColumnIndex(COLUMN_USER_IS_ADMIN);

                do {
                    StringBuilder userInfo = new StringBuilder("User: ");
                    if (idIndex != -1) userInfo.append("ID=").append(cursor.getLong(idIndex));
                    if (nameIndex != -1) userInfo.append(", Name=").append(cursor.getString(nameIndex));
                    if (emailIndex != -1) userInfo.append(", Email=").append(cursor.getString(emailIndex));
                    if (phoneIndex != -1) userInfo.append(", Phone=").append(cursor.getString(phoneIndex));
                    if (isAdminIndex != -1) userInfo.append(", IsAdmin=").append(cursor.getInt(isAdminIndex));

                    Log.d(TAG, userInfo.toString());
                } while (cursor.moveToNext());
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error listing users: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public long addUser(String name, String email, String phone, String password, boolean isAdmin) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PHONE, phone);
        values.put(COLUMN_USER_PASSWORD, password); // In a real app, hash this password
        values.put(COLUMN_USER_IS_ADMIN, isAdmin ? 1 : 0);
        
        return database.insert(TABLE_USERS, null, values);
    }
    
    // Check if user credentials are valid
    public User authenticateUser(String emailOrPhone, String password) {
        String query = "SELECT * FROM " + TABLE_USERS + 
                       " WHERE (" + COLUMN_USER_EMAIL + " = ? OR " + 
                       COLUMN_USER_PHONE + " = ?) AND " + 
                       COLUMN_USER_PASSWORD + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[] {emailOrPhone, emailOrPhone, password});
        
        User user = null;
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_USER_NAME);
            int emailIndex = cursor.getColumnIndex(COLUMN_USER_EMAIL);
            int phoneIndex = cursor.getColumnIndex(COLUMN_USER_PHONE);
            int isAdminIndex = cursor.getColumnIndex(COLUMN_USER_IS_ADMIN);
            
            user = new User();
            if (idIndex != -1) user.setId(cursor.getLong(idIndex));
            if (nameIndex != -1) user.setName(cursor.getString(nameIndex));
            if (emailIndex != -1) user.setEmail(cursor.getString(emailIndex));
            if (phoneIndex != -1) user.setPhone(cursor.getString(phoneIndex));
            if (isAdminIndex != -1) user.setAdmin(cursor.getInt(isAdminIndex) == 1);
        }
        
        cursor.close();
        return user;
    }
    
    // Get user by email or phone
    public User getUserByEmailOrPhone(String emailOrPhone) {
        String query = "SELECT * FROM " + TABLE_USERS + 
                       " WHERE " + COLUMN_USER_EMAIL + " = ? OR " + 
                       COLUMN_USER_PHONE + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[] {emailOrPhone, emailOrPhone});
        
        User user = null;
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_USER_NAME);
            int emailIndex = cursor.getColumnIndex(COLUMN_USER_EMAIL);
            int phoneIndex = cursor.getColumnIndex(COLUMN_USER_PHONE);
            int isAdminIndex = cursor.getColumnIndex(COLUMN_USER_IS_ADMIN);
            
            user = new User();
            if (idIndex != -1) user.setId(cursor.getLong(idIndex));
            if (nameIndex != -1) user.setName(cursor.getString(nameIndex));
            if (emailIndex != -1) user.setEmail(cursor.getString(emailIndex));
            if (phoneIndex != -1) user.setPhone(cursor.getString(phoneIndex));
            if (isAdminIndex != -1) user.setAdmin(cursor.getInt(isAdminIndex) == 1);
        }
        
        cursor.close();
        return user;
    }
    
    // ====== CLOTHING ITEM OPERATIONS ======
    
    // Add a new clothing item
    public long addClothingItem(ClothingItem item) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_ID, item.getItemId());
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_PRICE, item.getPrice());
        values.put(COLUMN_IMAGE_URL, item.getImageUrl());
        values.put(COLUMN_CATEGORY, item.getCategory());
        
        return database.insert(TABLE_CLOTHING, null, values);
    }
    
    // Update a clothing item
    public int updateClothingItem(ClothingItem item) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_PRICE, item.getPrice());
        values.put(COLUMN_IMAGE_URL, item.getImageUrl());
        values.put(COLUMN_CATEGORY, item.getCategory());
        
        return database.update(TABLE_CLOTHING, values, 
                COLUMN_ITEM_ID + " = ?", 
                new String[] {item.getItemId()});
    }
    
    // Delete a clothing item
    public int deleteClothingItem(String itemId) {
        return database.delete(TABLE_CLOTHING, 
                COLUMN_ITEM_ID + " = ?", 
                new String[] {itemId});
    }
    
    // Get all clothing items
    public List<ClothingItem> getAllClothingItems() {
        List<ClothingItem> items = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CLOTHING;
        
        Cursor cursor = database.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                int itemIdIndex = cursor.getColumnIndex(COLUMN_ITEM_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                int imageUrlIndex = cursor.getColumnIndex(COLUMN_IMAGE_URL);
                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                
                ClothingItem item = new ClothingItem();
                if (itemIdIndex != -1) item.setItemId(cursor.getString(itemIdIndex));
                if (nameIndex != -1) item.setName(cursor.getString(nameIndex));
                if (descriptionIndex != -1) item.setDescription(cursor.getString(descriptionIndex));
                if (priceIndex != -1) item.setPrice(cursor.getDouble(priceIndex));
                if (imageUrlIndex != -1) item.setImageUrl(cursor.getString(imageUrlIndex));
                if (categoryIndex != -1) item.setCategory(cursor.getString(categoryIndex));
                
                items.add(item);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return items;
    }
    
    // Get clothing items by category
    public List<ClothingItem> getClothingItemsByCategory(String category) {
        List<ClothingItem> items = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CLOTHING + 
                       " WHERE " + COLUMN_CATEGORY + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[] {category});
        
        if (cursor.moveToFirst()) {
            do {
                int itemIdIndex = cursor.getColumnIndex(COLUMN_ITEM_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                int imageUrlIndex = cursor.getColumnIndex(COLUMN_IMAGE_URL);
                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                
                ClothingItem item = new ClothingItem();
                if (itemIdIndex != -1) item.setItemId(cursor.getString(itemIdIndex));
                if (nameIndex != -1) item.setName(cursor.getString(nameIndex));
                if (descriptionIndex != -1) item.setDescription(cursor.getString(descriptionIndex));
                if (priceIndex != -1) item.setPrice(cursor.getDouble(priceIndex));
                if (imageUrlIndex != -1) item.setImageUrl(cursor.getString(imageUrlIndex));
                if (categoryIndex != -1) item.setCategory(cursor.getString(categoryIndex));
                
                items.add(item);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return items;
    }
    
    // Search clothing items by name
    public List<ClothingItem> searchClothingItems(String searchTerm) {
        List<ClothingItem> items = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CLOTHING + 
                       " WHERE " + COLUMN_NAME + " LIKE ? OR " + 
                       COLUMN_DESCRIPTION + " LIKE ?";
        
        String searchPattern = "%" + searchTerm + "%";
        Cursor cursor = database.rawQuery(query, new String[] {searchPattern, searchPattern});
        
        if (cursor.moveToFirst()) {
            do {
                int itemIdIndex = cursor.getColumnIndex(COLUMN_ITEM_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                int imageUrlIndex = cursor.getColumnIndex(COLUMN_IMAGE_URL);
                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                
                ClothingItem item = new ClothingItem();
                if (itemIdIndex != -1) item.setItemId(cursor.getString(itemIdIndex));
                if (nameIndex != -1) item.setName(cursor.getString(nameIndex));
                if (descriptionIndex != -1) item.setDescription(cursor.getString(descriptionIndex));
                if (priceIndex != -1) item.setPrice(cursor.getDouble(priceIndex));
                if (imageUrlIndex != -1) item.setImageUrl(cursor.getString(imageUrlIndex));
                if (categoryIndex != -1) item.setCategory(cursor.getString(categoryIndex));
                
                items.add(item);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return items;
    }
    
    // ====== CART OPERATIONS ======
    
    // Add item to cart
    public long addToCart(long userId, String itemId, int quantity) {
        // First check if item already exists in cart
        String query = "SELECT * FROM " + TABLE_CART + 
                       " WHERE " + COLUMN_CART_USER_ID + " = ? AND " + 
                       COLUMN_CART_ITEM_ID + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[] {String.valueOf(userId), itemId});
        
        if (cursor.moveToFirst()) {
            // Item exists, update quantity
            int quantityIndex = cursor.getColumnIndex(COLUMN_CART_QUANTITY);
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            
            int currentQuantity = 0;
            long cartItemId = -1;
            
            if (quantityIndex != -1) currentQuantity = cursor.getInt(quantityIndex);
            if (idIndex != -1) cartItemId = cursor.getLong(idIndex);
            
            ContentValues values = new ContentValues();
            values.put(COLUMN_CART_QUANTITY, currentQuantity + quantity);
            
            cursor.close();
            return database.update(TABLE_CART, values, 
                    COLUMN_ID + " = ?", 
                    new String[] {String.valueOf(cartItemId)});
        } else {
            // Item doesn't exist, insert new row
            ContentValues values = new ContentValues();
            values.put(COLUMN_CART_USER_ID, userId);
            values.put(COLUMN_CART_ITEM_ID, itemId);
            values.put(COLUMN_CART_QUANTITY, quantity);
            
            cursor.close();
            return database.insert(TABLE_CART, null, values);
        }
    }
    
    // Update cart item quantity
    public int updateCartItemQuantity(long cartItemId, int quantity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CART_QUANTITY, quantity);
        
        return database.update(TABLE_CART, values, 
                COLUMN_ID + " = ?", 
                new String[] {String.valueOf(cartItemId)});
    }
    
    // Remove item from cart
    public int removeFromCart(long cartItemId) {
        return database.delete(TABLE_CART, 
                COLUMN_ID + " = ?", 
                new String[] {String.valueOf(cartItemId)});
    }
    
    // Get cart items for user
    public List<CartItem> getCartItems(long userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String query = "SELECT c.*, i.* FROM " + TABLE_CART + " c " +
                       "JOIN " + TABLE_CLOTHING + " i ON c." + COLUMN_CART_ITEM_ID + " = i." + COLUMN_ITEM_ID +
                       " WHERE c." + COLUMN_CART_USER_ID + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[] {String.valueOf(userId)});
        
        if (cursor.moveToFirst()) {
            do {
                int cartIdIndex = cursor.getColumnIndex("c." + COLUMN_ID);
                int quantityIndex = cursor.getColumnIndex("c." + COLUMN_CART_QUANTITY);
                
                int itemIdIndex = cursor.getColumnIndex("i." + COLUMN_ITEM_ID);
                int nameIndex = cursor.getColumnIndex("i." + COLUMN_NAME);
                int descriptionIndex = cursor.getColumnIndex("i." + COLUMN_DESCRIPTION);
                int priceIndex = cursor.getColumnIndex("i." + COLUMN_PRICE);
                int imageUrlIndex = cursor.getColumnIndex("i." + COLUMN_IMAGE_URL);
                int categoryIndex = cursor.getColumnIndex("i." + COLUMN_CATEGORY);
                
                // If column indexes aren't found with table prefix, try without
                if (cartIdIndex == -1) cartIdIndex = cursor.getColumnIndex(COLUMN_ID);
                if (quantityIndex == -1) quantityIndex = cursor.getColumnIndex(COLUMN_CART_QUANTITY);
                if (itemIdIndex == -1) itemIdIndex = cursor.getColumnIndex(COLUMN_ITEM_ID);
                if (nameIndex == -1) nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                if (descriptionIndex == -1) descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                if (priceIndex == -1) priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                if (imageUrlIndex == -1) imageUrlIndex = cursor.getColumnIndex(COLUMN_IMAGE_URL);
                if (categoryIndex == -1) categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                
                ClothingItem clothingItem = new ClothingItem();
                if (itemIdIndex != -1) clothingItem.setItemId(cursor.getString(itemIdIndex));
                if (nameIndex != -1) clothingItem.setName(cursor.getString(nameIndex));
                if (descriptionIndex != -1) clothingItem.setDescription(cursor.getString(descriptionIndex));
                if (priceIndex != -1) clothingItem.setPrice(cursor.getDouble(priceIndex));
                if (imageUrlIndex != -1) clothingItem.setImageUrl(cursor.getString(imageUrlIndex));
                if (categoryIndex != -1) clothingItem.setCategory(cursor.getString(categoryIndex));
                
                int quantity = 1;
                if (quantityIndex != -1) quantity = cursor.getInt(quantityIndex);
                
                CartItem cartItem = new CartItem(clothingItem, quantity);
                // Set cart item id for database operations
                if (cartIdIndex != -1) cartItem.setId(cursor.getLong(cartIdIndex));
                
                cartItems.add(cartItem);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return cartItems;
    }
    
    // Clear cart for user
    public int clearCart(long userId) {
        return database.delete(TABLE_CART, 
                COLUMN_CART_USER_ID + " = ?", 
                new String[] {String.valueOf(userId)});
    }
    
    // ====== ORDER OPERATIONS ======
    
    // Create order from cart
    public long createOrder(long userId, double totalAmount) {
        database.beginTransaction();
        try {
            // 1. Create order
            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_ORDER_USER_ID, userId);
            orderValues.put(COLUMN_ORDER_DATE, System.currentTimeMillis());
            orderValues.put(COLUMN_ORDER_TOTAL, totalAmount);
            orderValues.put(COLUMN_ORDER_STATUS, "pending");
            
            long orderId = database.insert(TABLE_ORDERS, null, orderValues);
            
            // 2. Get cart items
            List<CartItem> cartItems = getCartItems(userId);
            
            // 3. Add order items
            for (CartItem cartItem : cartItems) {
                ContentValues itemValues = new ContentValues();
                itemValues.put(COLUMN_ORDER_ITEM_ORDER_ID, orderId);
                itemValues.put(COLUMN_ORDER_ITEM_ITEM_ID, cartItem.getClothingItem().getItemId());
                itemValues.put(COLUMN_ORDER_ITEM_QUANTITY, cartItem.getQuantity());
                itemValues.put(COLUMN_ORDER_ITEM_PRICE, cartItem.getClothingItem().getPrice());
                
                database.insert(TABLE_ORDER_ITEMS, null, itemValues);
            }
            
            // 4. Clear cart
            clearCart(userId);
            
            database.setTransactionSuccessful();
            return orderId;
        } finally {
            database.endTransaction();
        }
    }
    
    // Get orders for user
    public List<Order> getUserOrders(long userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_ORDERS + 
                       " WHERE " + COLUMN_ORDER_USER_ID + " = ?" +
                       " ORDER BY " + COLUMN_ORDER_DATE + " DESC";
        
        Cursor cursor = database.rawQuery(query, new String[] {String.valueOf(userId)});
        
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int dateIndex = cursor.getColumnIndex(COLUMN_ORDER_DATE);
                int totalIndex = cursor.getColumnIndex(COLUMN_ORDER_TOTAL);
                int statusIndex = cursor.getColumnIndex(COLUMN_ORDER_STATUS);
                
                Order order = new Order();
                if (idIndex != -1) order.setId(cursor.getLong(idIndex));
                if (dateIndex != -1) order.setOrderDate(cursor.getLong(dateIndex));
                if (totalIndex != -1) order.setTotal(cursor.getDouble(totalIndex));
                if (statusIndex != -1) order.setStatus(cursor.getString(statusIndex));
                order.setUserId(userId);
                
                // Get order items
                order.setItems(getOrderItems(order.getId()));
                
                orders.add(order);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return orders;
    }
    
    // Get order items
    private List<OrderItem> getOrderItems(long orderId) {
        List<OrderItem> items = new ArrayList<>();
        String query = "SELECT oi.*, i.* FROM " + TABLE_ORDER_ITEMS + " oi " +
                       "JOIN " + TABLE_CLOTHING + " i ON oi." + COLUMN_ORDER_ITEM_ITEM_ID + " = i." + COLUMN_ITEM_ID +
                       " WHERE oi." + COLUMN_ORDER_ITEM_ORDER_ID + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[] {String.valueOf(orderId)});
        
        if (cursor.moveToFirst()) {
            do {
                int quantityIndex = cursor.getColumnIndex(COLUMN_ORDER_ITEM_QUANTITY);
                int priceIndex = cursor.getColumnIndex(COLUMN_ORDER_ITEM_PRICE);
                
                int itemIdIndex = cursor.getColumnIndex(COLUMN_ITEM_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                int itemPriceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                int imageUrlIndex = cursor.getColumnIndex(COLUMN_IMAGE_URL);
                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                
                ClothingItem clothingItem = new ClothingItem();
                if (itemIdIndex != -1) clothingItem.setItemId(cursor.getString(itemIdIndex));
                if (nameIndex != -1) clothingItem.setName(cursor.getString(nameIndex));
                if (descriptionIndex != -1) clothingItem.setDescription(cursor.getString(descriptionIndex));
                if (itemPriceIndex != -1) clothingItem.setPrice(cursor.getDouble(itemPriceIndex));
                if (imageUrlIndex != -1) clothingItem.setImageUrl(cursor.getString(imageUrlIndex));
                if (categoryIndex != -1) clothingItem.setCategory(cursor.getString(categoryIndex));
                
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setItem(clothingItem);
                if (quantityIndex != -1) orderItem.setQuantity(cursor.getInt(quantityIndex));
                if (priceIndex != -1) orderItem.setPrice(cursor.getDouble(priceIndex));
                
                items.add(orderItem);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return items;
    }
    
    // Get all orders (for admin)
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u." + COLUMN_USER_NAME + " FROM " + TABLE_ORDERS + " o " +
                       "JOIN " + TABLE_USERS + " u ON o." + COLUMN_ORDER_USER_ID + " = u." + COLUMN_ID +
                       " ORDER BY o." + COLUMN_ORDER_DATE + " DESC";
        
        Cursor cursor = database.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("o." + COLUMN_ID);
                int userIdIndex = cursor.getColumnIndex("o." + COLUMN_ORDER_USER_ID);
                int dateIndex = cursor.getColumnIndex("o." + COLUMN_ORDER_DATE);
                int totalIndex = cursor.getColumnIndex("o." + COLUMN_ORDER_TOTAL);
                int statusIndex = cursor.getColumnIndex("o." + COLUMN_ORDER_STATUS);
                int userNameIndex = cursor.getColumnIndex("u." + COLUMN_USER_NAME);
                
                // If column indexes aren't found with table prefix, try without
                if (idIndex == -1) idIndex = cursor.getColumnIndex(COLUMN_ID);
                if (userIdIndex == -1) userIdIndex = cursor.getColumnIndex(COLUMN_ORDER_USER_ID);
                if (dateIndex == -1) dateIndex = cursor.getColumnIndex(COLUMN_ORDER_DATE);
                if (totalIndex == -1) totalIndex = cursor.getColumnIndex(COLUMN_ORDER_TOTAL);
                if (statusIndex == -1) statusIndex = cursor.getColumnIndex(COLUMN_ORDER_STATUS);
                if (userNameIndex == -1) userNameIndex = cursor.getColumnIndex(COLUMN_USER_NAME);
                
                Order order = new Order();
                if (idIndex != -1) order.setId(cursor.getLong(idIndex));
                if (userIdIndex != -1) order.setUserId(cursor.getLong(userIdIndex));
                if (dateIndex != -1) order.setOrderDate(cursor.getLong(dateIndex));
                if (totalIndex != -1) order.setTotal(cursor.getDouble(totalIndex));
                if (statusIndex != -1) order.setStatus(cursor.getString(statusIndex));
                if (userNameIndex != -1) order.setUserName(cursor.getString(userNameIndex));
                
                // Get order items
                order.setItems(getOrderItems(order.getId()));
                
                orders.add(order);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return orders;
    }
    
    // Update order status
    public int updateOrderStatus(long orderId, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_STATUS, status);
        
        return database.update(TABLE_ORDERS, values, 
                COLUMN_ID + " = ?", 
                new String[] {String.valueOf(orderId)});
    }

    // Initialize database with sample data from CSV
    // Initialize database with sample data
    public void initDatabase() {
        Log.d(TAG, "Initializing database with sample data...");

        try {
            // Check if tables are empty
            Cursor userCursor = database.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
            userCursor.moveToFirst();
            int userCount = userCursor.getInt(0);
            userCursor.close();

            Log.d(TAG, "Current user count: " + userCount);

            if (userCount == 0) {
                Log.d(TAG, "Adding sample users");
                // Add admin and demo user
                addUser("Admin", "admin@example.com", "", "adminpass", true);
                addUser("Demo User", "user@example.com", "", "password", false);
            }

            Cursor itemCursor = database.rawQuery("SELECT COUNT(*) FROM " + TABLE_CLOTHING, null);
            itemCursor.moveToFirst();
            int itemCount = itemCursor.getInt(0);
            itemCursor.close();

            Log.d(TAG, "Current clothing item count: " + itemCount);

            if (itemCount == 0) {
                Log.d(TAG, "Adding sample clothing items");
                // Add sample clothing items

                // T-shirt 1
                addClothingItem(new ClothingItem(
                        "T-shirt 1",
                        "Cool T-shirt",
                        250.000,
                        "https://bizweb.dktcdn.net/thumb/1024x1024/100/331/067/products/398434736-197314980077983-4483347381331007718-n.jpg?v=1701671138780",
                        "Áo",
                        "TSHIRT1"
                ));

                // Jeans 1
                addClothingItem(new ClothingItem(
                        "Jeans 1",
                        "Stylish Jeans",
                        500.000,
                        "https://hips.hearstapps.com/vader-prod.s3.amazonaws.com/1722862431-paige-jeans-66b0cb521552a.jpg?crop=0.8886666666666666xw:1xh;center,top&resize=980:*",
                        "Quần",
                        "JEANS1"
                ));

                // Jacket 1
                addClothingItem(new ClothingItem(
                        "Jacket 1",
                        "Warm Jacket",
                        750.000,
                        "https://m.media-amazon.com/images/I/51JQOeN1W1L._AC_UY1000_.jpg",
                        "Áo khoác",
                        "JACKET1"
                ));

                // Jeans 2
                addClothingItem(new ClothingItem(
                        "JEANS 2",
                        "Stylish Jeans",
                        1000.000,
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRh4P0zH18OcuFHx4DQMQ4wW0JLypcyC7DusA&s",
                        "Quần",
                        "JEANS2"
                ));

                // Leather Gloves
                addClothingItem(new ClothingItem(
                        "Leather Gloves",
                        "Fine leather gloves",
                        250.000,
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRMiHnK_-lh5JDJqpGtcwRjmWfH7xaWnxO4-A&s",
                        "Phụ kiện",
                        "GLOVES1"
                ));

                Log.d(TAG, "Sample data initialization complete");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database with sample data", e);
        }
    }
}