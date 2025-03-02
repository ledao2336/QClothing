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

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;

    public CartItemAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
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
        holder.itemPriceTextView.setText(String.format("%.3fVND", cartItem.getClothingItem().getPrice()));
        holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
        // Load image using an image loading library like Glide or Picasso (omitted for brevity)
        // Example (using placeholder): holder.itemImageView.setImageResource(R.drawable.placeholder_image);

        holder.increaseQuantityButton.setOnClickListener(v -> {
            cartItem.increaseQuantity();
            holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
            if (context instanceof CartActivity) {
                ((CartActivity) context).onCartUpdated(); // Update total price in CartActivity
            }
        });

        holder.decreaseQuantityButton.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.decreaseQuantity();
                holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
                if (context instanceof CartActivity) {
                    ((CartActivity) context).onCartUpdated(); // Update total price in CartActivity
                }
            } else {
                // Optionally remove item from cart if quantity is decreased to 0
                Toast.makeText(context, " Số lượng không được bé hơn 1", Toast.LENGTH_SHORT).show();
            }
        });

        holder.removeItemButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
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