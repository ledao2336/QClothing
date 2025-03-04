package com.example.qclothing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OrderConfirmationActivity extends AppCompatActivity {

    private TextView orderIdTextView;
    private Button returnToShoppingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        orderIdTextView = findViewById(R.id.order_id_text_view);
        returnToShoppingButton = findViewById(R.id.return_to_shopping_button);

        // Get order ID from intent
        long orderId = getIntent().getLongExtra("ORDER_ID", -1);

        if (orderId != -1) {
            orderIdTextView.setText(getString(R.string.order_id_format, orderId));
        }

        returnToShoppingButton.setOnClickListener(v -> {
            // Navigate back to main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}