package com.example.qclothing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartItemAdapter cartItemAdapter;
    private TextView totalPriceTextView;
    private Button checkoutButton;

    // For now, use a static cart (replace with proper cart management later)
    public static List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        totalPriceTextView = findViewById(R.id.total_price_text_view);
        checkoutButton = findViewById(R.id.checkout_button);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItemAdapter = new CartItemAdapter(this, cartItems); // Pass the static cart list
        cartRecyclerView.setAdapter(cartItemAdapter);

        updateTotalPrice(); // Initial total price calculation

        checkoutButton.setOnClickListener(v -> {
            Intent checkoutIntent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(checkoutIntent);
        });
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getClothingItem().getPrice() * cartItem.getQuantity();
        }
        totalPriceTextView.setText(String.format("%.3f", totalPrice));
    }

    // Call this method from adapter when cart items change (quantity update, remove item)
    public void onCartUpdated() {
        updateTotalPrice();
    }
}