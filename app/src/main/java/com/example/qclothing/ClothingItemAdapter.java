package com.example.qclothing;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale; // Import Locale

public class ClothingItemAdapter extends RecyclerView.Adapter<ClothingItemAdapter.ClothingItemViewHolder> {

    private Context context;
    private List<ClothingItem> clothingItemList;

    public ClothingItemAdapter(Context context, List<ClothingItem> clothingItemList) {
        this.context = context;
        this.clothingItemList = clothingItemList;
    }

    @NonNull
    @Override
    public ClothingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_clothing, parent, false);
        return new ClothingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothingItemViewHolder holder, int position) {
        ClothingItem clothingItem = clothingItemList.get(position);
        holder.itemNameTextView.setText(clothingItem.getName());

        // --- SỬA ĐỔI ĐỂ SỬ DỤNG STRING RESOURCE currency_format_vnd ---
        String formattedPrice = String.format(Locale.getDefault(), ContextCompat.getString(context, R.string.currency_format_vnd), clothingItem.getPrice(), ContextCompat.getString(context, R.string.currency_symbol));
        holder.itemPriceTextView.setText(formattedPrice);
        // --- END SỬA ĐỔI ---

        // Load image using an image loading library like Glide or Picasso (omitted for brevity)
        // Example (using placeholder): holder.itemImageView.setImageResource(R.drawable.placeholder_image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDescriptionActivity.class);
            intent.putExtra("CLOTHING_ITEM", clothingItem); // Pass the ClothingItem object
            context.startActivity(intent);
        });

        holder.addToCartButton.setOnClickListener(v -> {
            // Add item to cart (using static cart in CartActivity for now)
            CartActivity.cartItems.add(new CartItem(clothingItem, 1)); // Default quantity 1
            Toast.makeText(context, clothingItem.getName() + " đã được thêm vào giỏ", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return clothingItemList.size();
    }

    public static class ClothingItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView itemPriceTextView;
        Button addToCartButton;

        public ClothingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.clothing_item_image_view);
            itemNameTextView = itemView.findViewById(R.id.clothing_item_name_text_view);
            itemPriceTextView = itemView.findViewById(R.id.clothing_item_price_text_view);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button_item);
        }
    }
}