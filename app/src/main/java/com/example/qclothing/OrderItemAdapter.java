package com.example.qclothing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private Context context;
    private List<OrderItem> orderItems;

    public OrderItemAdapter(Context context, List<OrderItem> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        ClothingItem clothingItem = orderItem.getItem();
        
        holder.itemNameTextView.setText(clothingItem.getName());
        holder.itemPriceTextView.setText(String.format(Locale.getDefault(), "%.3f VND", orderItem.getPrice()));
        holder.itemQuantityTextView.setText("Số lượng: " + orderItem.getQuantity());
        holder.itemSubtotalTextView.setText(String.format(Locale.getDefault(), "Thành tiền: %.3f VND", orderItem.getSubtotal()));
        
        // Load image (we'll use a placeholder for now)
        // In a real app, you would use Glide or Picasso to load images
        holder.itemImageView.setImageResource(R.drawable.ic_launcher_foreground);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView itemPriceTextView;
        TextView itemQuantityTextView;
        TextView itemSubtotalTextView;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.order_item_image_view);
            itemNameTextView = itemView.findViewById(R.id.order_item_name_text_view);
            itemPriceTextView = itemView.findViewById(R.id.order_item_price_text_view);
            itemQuantityTextView = itemView.findViewById(R.id.order_item_quantity_text_view);
            itemSubtotalTextView = itemView.findViewById(R.id.order_item_subtotal_text_view);
        }
    }
}