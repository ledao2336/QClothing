package com.example.qclothing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartItemAdapter cartItemAdapter;
    private TextView totalPriceTextView;
    private Button checkoutButton;

    // For backwards compatibility, maintain the static cart
    public static List<CartItem> cartItems = new ArrayList<>();
    
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;
    private User currentUser;
    private List<CartItem> userCartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        
        // Set up back button in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.shopping_cart);
        }

        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        totalPriceTextView = findViewById(R.id.total_price_text_view);
        checkoutButton = findViewById(R.id.checkout_button);
        
        // Initialize database and session managers
        databaseManager = new DatabaseManager(this);
        sessionManager = new SessionManager(this);
        
        // Check if user is logged in
        if (sessionManager.isLoggedIn()) {
            currentUser = sessionManager.getUserDetails();
            loadCartFromDatabase();
        } else {
            // If not logged in, redirect to login
            redirectToLogin();
            return;
        }

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userCartItems = new ArrayList<>();
        cartItemAdapter = new CartItemAdapter(this, userCartItems);
        cartRecyclerView.setAdapter(cartItemAdapter);

        updateTotalPrice(); // Initial total price calculation

        checkoutButton.setOnClickListener(v -> {
            if (userCartItems.isEmpty()) {
                Toast.makeText(CartActivity.this, "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent checkoutIntent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(checkoutIntent);
        });
    }
    
    private void loadCartFromDatabase() {
        try {
            // Open database connection
            databaseManager.open();
            
            // Get cart items for the current user
            userCartItems = databaseManager.getCartItems(currentUser.getId());
            
            // Update adapter
            if (cartItemAdapter != null) {
                cartItemAdapter.updateCartItems(userCartItems);
            }
            
            // Update total price
            updateTotalPrice();
            
        } catch (Exception e) {
            Toast.makeText(this, "Không thể tải giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            
            // Fallback to static cart items
            userCartItems = new ArrayList<>(cartItems);
            if (cartItemAdapter != null) {
                cartItemAdapter.updateCartItems(userCartItems);
            }
            
            updateTotalPrice();
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
    }
    
    private void redirectToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.putExtra("REDIRECT_TO_CART", true);
        startActivity(loginIntent);
        finish();
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItem cartItem : userCartItems) {
            totalPrice += cartItem.getClothingItem().getPrice() * cartItem.getQuantity();
        }
        totalPriceTextView.setText(String.format(Locale.getDefault(), "%.3f VND", totalPrice));
    }

    // Call this method from adapter when cart items change (quantity update, remove item)
    public void onCartUpdated() {
        updateTotalPrice();
        
        // Update the database
        try {
            databaseManager.open();
            
            // Currently update is handled by adapter methods directly
            // Here we could do additional operations if needed
            
        } catch (Exception e) {
            Toast.makeText(this, "Không thể cập nhật giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
        
        // Update static cart for backward compatibility
        cartItems = new ArrayList<>(userCartItems);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload cart data when returning to activity
        if (sessionManager.isLoggedIn()) {
            loadCartFromDatabase();
        }
    }
}