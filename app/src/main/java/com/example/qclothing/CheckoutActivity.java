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

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView checkoutRecyclerView;
    private CartItemAdapter checkoutItemAdapter;
    private TextView totalPriceTextView;
    private Button confirmOrderButton;

    private List<CartItem> cartItems;
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;
    private User currentUser;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Set up back button in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.checkout);
        }

        checkoutRecyclerView = findViewById(R.id.checkout_recycler_view);
        totalPriceTextView = findViewById(R.id.checkout_total_price_text_view);
        confirmOrderButton = findViewById(R.id.confirm_order_button);

        // Initialize database and session managers
        databaseManager = new DatabaseManager(this);
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            // Redirect to login
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("REDIRECT_TO_CART", true);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Get current user
        currentUser = sessionManager.getUserDetails();

        // Load cart items
        loadCartItems();

        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkoutItemAdapter = new CartItemAdapter(this, cartItems);
        // Set read-only mode for checkout screen
        checkoutItemAdapter.setReadOnly(true);
        checkoutRecyclerView.setAdapter(checkoutItemAdapter);

        updateTotalPrice();

        confirmOrderButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(CheckoutActivity.this, "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
                return;
            }

            // Process order
            processOrder();
        });
    }

    private void loadCartItems() {
        try {
            // Open database connection
            databaseManager.open();

            // Get cart items for the current user
            cartItems = databaseManager.getCartItems(currentUser.getId());

            if (cartItems.isEmpty()) {
                // If database returned empty, try to use static cart as fallback
                cartItems = new ArrayList<>(CartActivity.cartItems);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Không thể tải giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();

            // Fallback to static cart items
            cartItems = new ArrayList<>(CartActivity.cartItems);
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
    }

    private void updateTotalPrice() {
        totalAmount = 0;
        for (CartItem cartItem : cartItems) {
            totalAmount += cartItem.getClothingItem().getPrice() * cartItem.getQuantity();
        }
        totalPriceTextView.setText(String.format(Locale.getDefault(), "%.3f VND", totalAmount));
    }

    private void processOrder() {
        try {
            // Open database connection
            databaseManager.open();

            // First, make a copy of the items to preserve them
            List<CartItem> orderItems = new ArrayList<>(cartItems);

            // Create order
            long orderId = databaseManager.createOrder(currentUser.getId(), totalAmount);

            if (orderId > 0) {
                // Order created successfully
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();

                // Clear static cart after the order is processed
                CartActivity.cartItems.clear();

                // Show order confirmation
                showOrderConfirmation(orderId);
            } else {
                // Failed to create order
                Toast.makeText(this, "Không thể tạo đơn hàng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tạo đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
    }

    private void showOrderConfirmation(long orderId) {
        // You can create a dedicated OrderConfirmationActivity, but for now,
        // we'll just show a toast and navigate back to MainActivity

        Toast.makeText(this, "Đơn hàng #" + orderId + " đã được tạo thành công!", Toast.LENGTH_LONG).show();

        // Navigate back to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}