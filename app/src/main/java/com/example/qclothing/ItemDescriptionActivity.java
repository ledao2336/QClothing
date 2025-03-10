package com.example.qclothing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ItemDescriptionActivity extends AppCompatActivity {

    private ImageView itemImageView;
    private TextView itemNameTextView;
    private TextView itemPriceTextView;
    private TextView itemDescriptionTextView;
    private Button addToCartButton;

    private ClothingItem selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);

        itemImageView = findViewById(R.id.item_image_view);
        itemNameTextView = findViewById(R.id.item_name_text_view);
        itemPriceTextView = findViewById(R.id.item_price_text_view);
        itemDescriptionTextView = findViewById(R.id.item_description_text_view);
        addToCartButton = findViewById(R.id.add_to_cart_button_description);

        // Get the ClothingItem from the Intent
        selectedItem = getIntent().getParcelableExtra("CLOTHING_ITEM");

        if (selectedItem != null) {
            // Set item details in the UI
            itemNameTextView.setText(selectedItem.getName());
            itemPriceTextView.setText(String.format("%.3fVND", selectedItem.getPrice()));
            itemDescriptionTextView.setText(selectedItem.getDescription());

            // Load image with Glide
            if (selectedItem.getImageUrl() != null && !selectedItem.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(selectedItem.getImageUrl())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(R.drawable.ic_launcher_foreground)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(itemImageView);
            } else {
                // If no image URL, use placeholder
                itemImageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
        } else {
            // Handle error if item is not passed correctly
            Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if item data is missing
            return;
        }

        addToCartButton.setOnClickListener(v -> {
            // Add item to cart (using static cart in CartActivity for now)
            CartActivity.cartItems.add(new CartItem(selectedItem, 1)); // Default quantity 1
            Toast.makeText(ItemDescriptionActivity.this, selectedItem.getName() + " đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            finish(); // Go back to shopping after adding to cart
        });
    }
}