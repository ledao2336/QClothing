package com.example.qclothing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private DatabaseManager databaseManager;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.databaseManager = new DatabaseManager(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        
        holder.orderIdTextView.setText("Đơn hàng #" + order.getId());
        holder.customerNameTextView.setText("Khách hàng: " + order.getUserName());
        holder.orderDateTextView.setText("Ngày: " + order.getFormattedDate());
        holder.totalPriceTextView.setText("Tổng tiền: " + order.getFormattedTotal());
        holder.statusTextView.setText("Trạng thái: " + order.getStatus());
        
        // Set up items recycler view
        OrderItemAdapter itemAdapter = new OrderItemAdapter(context, order.getItems());
        holder.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.itemsRecyclerView.setAdapter(itemAdapter);
        
        // Initially hide items
        holder.itemsRecyclerView.setVisibility(View.GONE);
        
        // Set up status buttons
        if ("pending".equals(order.getStatus())) {
            holder.approveButton.setVisibility(View.VISIBLE);
            holder.completeButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.VISIBLE);
        } else if ("approved".equals(order.getStatus())) {
            holder.approveButton.setVisibility(View.GONE);
            holder.completeButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);
        } else {
            // completed or cancelled
            holder.approveButton.setVisibility(View.GONE);
            holder.completeButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);
        }
        
        // Set up click listeners
        holder.viewItemsButton.setOnClickListener(v -> {
            // Toggle items visibility
            boolean isVisible = holder.itemsRecyclerView.getVisibility() == View.VISIBLE;
            holder.itemsRecyclerView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            holder.viewItemsButton.setText(isVisible ? "Xem chi tiết" : "Ẩn chi tiết");
        });
        
        holder.approveButton.setOnClickListener(v -> {
            updateOrderStatus(order.getId(), "approved", holder);
        });
        
        holder.completeButton.setOnClickListener(v -> {
            updateOrderStatus(order.getId(), "completed", holder);
        });
        
        holder.cancelButton.setOnClickListener(v -> {
            updateOrderStatus(order.getId(), "cancelled", holder);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    
    private void updateOrderStatus(long orderId, String newStatus, OrderViewHolder holder) {
        try {
            // Open database connection
            databaseManager.open();
            
            // Update order status
            int result = databaseManager.updateOrderStatus(orderId, newStatus);
            
            if (result > 0) {
                // Update succeeded
                Toast.makeText(context, "Đã cập nhật trạng thái đơn hàng", Toast.LENGTH_SHORT).show();
                
                // Update UI
                holder.statusTextView.setText("Trạng thái: " + newStatus);
                
                // Update button visibility
                if ("approved".equals(newStatus)) {
                    holder.approveButton.setVisibility(View.GONE);
                    holder.completeButton.setVisibility(View.VISIBLE);
                    holder.cancelButton.setVisibility(View.VISIBLE);
                } else if ("completed".equals(newStatus) || "cancelled".equals(newStatus)) {
                    holder.approveButton.setVisibility(View.GONE);
                    holder.completeButton.setVisibility(View.GONE);
                    holder.cancelButton.setVisibility(View.GONE);
                }
                
                // Update order in list
                for (Order order : orderList) {
                    if (order.getId() == orderId) {
                        order.setStatus(newStatus);
                        break;
                    }
                }
            } else {
                // Update failed
                Toast.makeText(context, "Không thể cập nhật trạng thái đơn hàng", Toast.LENGTH_SHORT).show();
            }
            
        } catch (Exception e) {
            Toast.makeText(context, "Lỗi cập nhật trạng thái: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView;
        TextView customerNameTextView;
        TextView orderDateTextView;
        TextView totalPriceTextView;
        TextView statusTextView;
        Button viewItemsButton;
        RecyclerView itemsRecyclerView;
        Button approveButton;
        Button completeButton;
        Button cancelButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.order_id_text_view);
            customerNameTextView = itemView.findViewById(R.id.customer_name_text_view);
            orderDateTextView = itemView.findViewById(R.id.order_date_text_view);
            totalPriceTextView = itemView.findViewById(R.id.order_total_text_view);
            statusTextView = itemView.findViewById(R.id.order_status_text_view);
            viewItemsButton = itemView.findViewById(R.id.view_items_button);
            itemsRecyclerView = itemView.findViewById(R.id.order_items_recycler_view);
            approveButton = itemView.findViewById(R.id.approve_order_button);
            completeButton = itemView.findViewById(R.id.complete_order_button);
            cancelButton = itemView.findViewById(R.id.cancel_order_button);
        }
    }
}