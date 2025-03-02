package com.example.qclothing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;
    private boolean readOnly = false; // Add readOnly flag

    public CartItemAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.databaseManager = new DatabaseManager(context);
        this.sessionManager = new SessionManager(context);
    }

    public void updateCartItems(List<CartItem> newItems) {
        this.cartItemList.clear();
        this.cartItemList.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_item, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.itemNameTextView.setText(cartItem.getClothingItem().getName());
        holder.itemPriceTextView.setText(String.format(Locale.getDefault(), "%.3f VND", cartItem.getClothingItem().getPrice()));
        holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));

        // Load image (we'll use a placeholder for now)
        // In a real app, you would use Glide or Picasso to load images
        holder.itemImageView.setImageResource(R.drawable.ic_launcher_foreground);

        holder.increaseQuantityButton.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            updateCartItemQuantity(cartItem, newQuantity);
            holder.quantityTextView.setText(String.valueOf(newQuantity));
            if (context instanceof CartActivity) {
                ((CartActivity) context).onCartUpdated(); // Update total price in CartActivity
            }
        });

        holder.decreaseQuantityButton.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                int newQuantity = cartItem.getQuantity() - 1;
                updateCartItemQuantity(cartItem, newQuantity);
                holder.quantityTextView.setText(String.valueOf(newQuantity));
                if (context instanceof CartActivity) {
                    ((CartActivity) context).onCartUpdated(); // Update total price in CartActivity
                }
            } else {
                Toast.makeText(context, "Số lượng không được bé hơn 1", Toast.LENGTH_SHORT).show();
            }
        });

        holder.removeItemButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                CartItem itemToRemove = cartItemList.get(adapterPosition);
                removeCartItem(itemToRemove);
                cartItemList.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                if (context instanceof CartActivity) {
                    ((CartActivity) context).onCartUpdated(); // Update total price in CartActivity
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    // Update cart item quantity in the database
    private void updateCartItemQuantity(CartItem cartItem, int newQuantity) {
        try {
            // Update in memory first
            cartItem.setQuantity(newQuantity);

            // Check if user is logged in
            if (!sessionManager.isLoggedIn()) {
                return;
            }

            // Get current user
            User currentUser = sessionManager.getUserDetails();

            // Open database connection
            databaseManager.open();

            // Update quantity in database
            if (cartItem.getId() > 0) {
                // Item already has a database ID, update it
                databaseManager.updateCartItemQuantity(cartItem.getId(), newQuantity);
            } else {
                // Item doesn't have a database ID yet, add it
                databaseManager.addToCart(currentUser.getId(), cartItem.getClothingItem().getItemId(), newQuantity);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Lỗi cập nhật số lượng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
    }

    // Remove cart item from the database
    private void removeCartItem(CartItem cartItem) {
        try {
            // Check if user is logged in
            if (!sessionManager.isLoggedIn()) {
                return;
            }

            // Open database connection
            databaseManager.open();

            // Remove item from database
            if (cartItem.getId() > 0) {
                databaseManager.removeFromCart(cartItem.getId());
            }
        } catch (Exception e) {
            Toast.makeText(context, "Lỗi xóa sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView itemPriceTextView;
        TextView quantityTextView;
        Button increaseQuantityButton;
        Button decreaseQuantityButton;
        Button removeItemButton;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.cart_item_image_view);
            itemNameTextView = itemView.findViewById(R.id.cart_item_name_text_view);
            itemPriceTextView = itemView.findViewById(R.id.cart_item_price_text_view);
            quantityTextView = itemView.findViewById(R.id.quantity_text_view);
            increaseQuantityButton = itemView.findViewById(R.id.increase_quantity_button);
            decreaseQuantityButton = itemView.findViewById(R.id.decrease_quantity_button);
            removeItemButton = itemView.findViewById(R.id.remove_item_button_cart);
        }
    }
}