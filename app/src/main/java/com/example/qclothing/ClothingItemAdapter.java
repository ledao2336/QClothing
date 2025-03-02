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
import java.util.Locale;

public class ClothingItemAdapter extends RecyclerView.Adapter<ClothingItemAdapter.ClothingItemViewHolder> {

    private Context context;
    private List<ClothingItem> clothingItemList;
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;

    public ClothingItemAdapter(Context context, List<ClothingItem> clothingItemList) {
        this.context = context;
        this.clothingItemList = clothingItemList;
        this.databaseManager = new DatabaseManager(context);
        this.sessionManager = new SessionManager(context);
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

        // Format price
        String formattedPrice = String.format(Locale.getDefault(), "%.3f VND", clothingItem.getPrice());
        holder.itemPriceTextView.setText(formattedPrice);

        // Load image (we'll use a placeholder for now)
        // In a real app, you would use Glide or Picasso to load images
        holder.itemImageView.setImageResource(R.drawable.ic_launcher_foreground);

        // Set click listener for the whole item view
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDescriptionActivity.class);
            intent.putExtra("CLOTHING_ITEM", clothingItem); // Pass the ClothingItem object
            context.startActivity(intent);
        });

        // Set click listener for the Add to Cart button
        holder.addToCartButton.setOnClickListener(v -> {
            addToCart(clothingItem);
        });
    }

    @Override
    public int getItemCount() {
        return clothingItemList.size();
    }

    // Add item to cart
    private void addToCart(ClothingItem item) {
        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            // Redirect to login
            Intent loginIntent = new Intent(context, LoginActivity.class);
            loginIntent.putExtra("REDIRECT_TO_CART", true);
            context.startActivity(loginIntent);
            return;
        }

        // Get current user
        User currentUser = sessionManager.getUserDetails();

        try {
            // Open database connection
            databaseManager.open();

            // Add item to cart in database
            long result = databaseManager.addToCart(currentUser.getId(), item.getItemId(), 1);

            if (result > 0) {
                // Add to static cart for backward compatibility
                boolean itemExists = false;
                for (CartItem cartItem : CartActivity.cartItems) {
                    if (cartItem.getClothingItem().getItemId().equals(item.getItemId())) {
                        cartItem.increaseQuantity();
                        itemExists = true;
                        break;
                    }
                }

                if (!itemExists) {
                    CartActivity.cartItems.add(new CartItem(item, 1));
                }

                Toast.makeText(context, item.getName() + " đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Lỗi khi thêm vào giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();

            // Fallback: add to static cart
            boolean itemExists = false;
            for (CartItem cartItem : CartActivity.cartItems) {
                if (cartItem.getClothingItem().getItemId().equals(item.getItemId())) {
                    cartItem.increaseQuantity();
                    itemExists = true;
                    break;
                }
            }

            if (!itemExists) {
                CartActivity.cartItems.add(new CartItem(item, 1));
            }

            Toast.makeText(context, item.getName() + " đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
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