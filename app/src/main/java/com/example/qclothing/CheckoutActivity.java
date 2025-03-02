package com.example.qclothing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView checkoutRecyclerView;
    private CartItemAdapter checkoutItemAdapter;
    private TextView totalPriceTextView;
    private Button confirmOrderButton;

    private List<CartItem> cartItems; // Get cart items from CartActivity's static list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        checkoutRecyclerView = findViewById(R.id.checkout_recycler_view);
        totalPriceTextView = findViewById(R.id.checkout_total_price_text_view);
        confirmOrderButton = findViewById(R.id.confirm_order_button);

        // Get cart items from CartActivity's static list
        cartItems = CartActivity.cartItems;

        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkoutItemAdapter = new CartItemAdapter(this, cartItems); // Reuse CartItemAdapter
        checkoutRecyclerView.setAdapter(checkoutItemAdapter);

        updateTotalPrice();

        confirmOrderButton.setOnClickListener(v -> {
            // Simulate order confirmation
            Toast.makeText(CheckoutActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
            // For a real app, you would process the order here
            // ... (clear cart, navigate to order confirmation screen, etc.)

            // For now, just finish the checkout activity and go back to shopping
            finish();
        });
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getClothingItem().getPrice() * cartItem.getQuantity();
        }
        totalPriceTextView.setText(String.format("%.3fVND", totalPrice));
    }
}