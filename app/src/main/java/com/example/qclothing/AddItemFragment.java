package com.example.qclothing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddItemFragment extends Fragment {

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private EditText imageUrlEditText;
    private Spinner categorySpinner;
    private Button addItemButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        nameEditText = view.findViewById(R.id.add_item_name_edit_text);
        descriptionEditText = view.findViewById(R.id.add_item_description_edit_text);
        priceEditText = view.findViewById(R.id.add_item_price_edit_text);
        imageUrlEditText = view.findViewById(R.id.add_item_image_url_edit_text);
        categorySpinner = view.findViewById(R.id.add_item_category_spinner);
        addItemButton = view.findViewById(R.id.add_item_button);

        // Set up category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.clothing_categories_array, android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        addItemButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String priceStr = priceEditText.getText().toString();
            String imageUrl = imageUrlEditText.getText().toString();
            String category = categorySpinner.getSelectedItem().toString();

            // Basic validation
            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Hãy nhập tên và giá của sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                // Create new ClothingItem and add to mock data (replace with real data source)
                ClothingItem newItem = new ClothingItem(name, description, price, imageUrl, category, generateItemId(name));

                // Add to the static list
                MainActivity.clothingItemList.add(newItem);

                // IMPORTANT: Add to database
                DatabaseManager dbManager = new DatabaseManager(getContext());
                try {
                    dbManager.open();
                    long result = dbManager.addClothingItem(newItem);
                    if (result > 0) {
                        Toast.makeText(getContext(), "Thêm sản phẩm: " + name, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Không thể thêm sản phẩm vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    if (dbManager != null && dbManager.isOpen()) {
                        dbManager.close();
                    }
                }

                // Clear input fields after adding
                nameEditText.setText("");
                descriptionEditText.setText("");
                priceEditText.setText("");
                imageUrlEditText.setText("");
                categorySpinner.setSelection(0); // Reset to first category

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Định dạng giá không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Simple method to generate a unique Item ID (improve in real app)
    private String generateItemId(String itemName) {
        return itemName.toUpperCase().replaceAll(" ", "_") + "_" + System.currentTimeMillis();
    }
}