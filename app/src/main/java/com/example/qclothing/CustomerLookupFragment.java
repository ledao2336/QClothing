package com.example.qclothing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerLookupFragment extends Fragment {

    private EditText emailPhoneEditText;
    private Button lookupButton;
    private TextView resultTextView;
    private RecyclerView cartItemsRecyclerView;
    
    private DatabaseManager databaseManager;
    private CartItemAdapter cartItemAdapter;
    private List<CartItem> cartItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_lookup, container, false);

        emailPhoneEditText = view.findViewById(R.id.lookup_email_phone_edit_text);
        lookupButton = view.findViewById(R.id.lookup_button);
        resultTextView = view.findViewById(R.id.lookup_result_text_view);
        cartItemsRecyclerView = view.findViewById(R.id.lookup_cart_items_recycler_view);
        
        // Initialize RecyclerView
        cartItems = new ArrayList<>();
        cartItemAdapter = new CartItemAdapter(getContext(), cartItems);
        //cartItemAdapter.setReadOnly(true); // Read-only mode
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItemsRecyclerView.setAdapter(cartItemAdapter);
        
        // Initialize database manager
        databaseManager = new DatabaseManager(getContext());

        lookupButton.setOnClickListener(v -> {
            String emailPhone = emailPhoneEditText.getText().toString().trim();
            
            if (emailPhone.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập email hoặc số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            
            lookupCustomer(emailPhone);
        });

        return view;
    }
    
    private void lookupCustomer(String emailPhone) {
        try {
            // Open database connection
            databaseManager.open();
            
            // Get user by email or phone
            User user = databaseManager.getUserByEmailOrPhone(emailPhone);
            
            if (user != null) {
                // Display user info
                String userInfo = String.format(
                        "Tên: %s\nEmail: %s\nSĐT: %s",
                        user.getName(),
                        user.getEmail() != null ? user.getEmail() : "N/A",
                        user.getPhone() != null ? user.getPhone() : "N/A"
                );
                resultTextView.setText(userInfo);
                
                // Load cart items
                List<CartItem> userCartItems = databaseManager.getCartItems(user.getId());
                
                // Display cart items
                cartItems.clear();
                cartItems.addAll(userCartItems);
                cartItemAdapter.notifyDataSetChanged();
                
                // Show cart total
                double total = 0;
                for (CartItem cartItem : userCartItems) {
                    total += cartItem.getSubtotal();
                }
                
                // Append total to result text
                userInfo += String.format(
                        "\n\nTổng giá trị giỏ hàng: %.3f VND",
                        total
                );
                resultTextView.setText(userInfo);
                
            } else {
                resultTextView.setText("Không tìm thấy khách hàng với email/SĐT này");
                cartItems.clear();
                cartItemAdapter.notifyDataSetChanged();
            }
            
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi tra cứu khách hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            
            resultTextView.setText("Lỗi tra cứu khách hàng");
            cartItems.clear();
            cartItemAdapter.notifyDataSetChanged();
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
    }
}