package com.example.qclothing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    
    private DatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        ordersRecyclerView = view.findViewById(R.id.orders_recycler_view);
        
        // Initialize order list and adapter
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(), orderList);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersRecyclerView.setAdapter(orderAdapter);
        
        // Initialize database manager
        databaseManager = new DatabaseManager(getContext());
        
        // Load orders
        loadOrders();

        return view;
    }
    
    private void loadOrders() {
        try {
            // Open database connection
            databaseManager.open();
            
            // Get all orders
            List<Order> orders = databaseManager.getAllOrders();
            
            // Update adapter
            orderList.clear();
            orderList.addAll(orders);
            orderAdapter.notifyDataSetChanged();
            
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi tải đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload orders when fragment becomes visible
        loadOrders();
    }
}