package com.example.qclothing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.List;

public class EditItemFragment extends Fragment {

    private Spinner itemSelectionSpinner;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private EditText imageUrlEditText;
    private Spinner categorySpinner;
    private Button saveChangesButton;

    private List<ClothingItem> clothingItemList; // Reference to the same data as ShoppingFragment
    private ClothingItem selectedItemForEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);

        itemSelectionSpinner = view.findViewById(R.id.edit_item_selection_spinner);
        nameEditText = view.findViewById(R.id.edit_item_name_edit_text);
        descriptionEditText = view.findViewById(R.id.edit_item_description_edit_text);
        priceEditText = view.findViewById(R.id.edit_item_price_edit_text);
        imageUrlEditText = view.findViewById(R.id.edit_item_image_url_edit_text);
        categorySpinner = view.findViewById(R.id.edit_item_category_spinner);
        saveChangesButton = view.findViewById(R.id.save_changes_button);

        // Get reference to the same clothing item list as ShoppingFragment (for mock data)
        clothingItemList = MainActivity.clothingItemList; // Assuming static list in MainActivity

        // Populate item selection spinner
        populateItemSelectionSpinner();

        // Set up category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.clothing_categories_array, android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        itemSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemName = (String) parent.getItemAtPosition(position);
                selectedItemForEdit = findClothingItemByName(selectedItemName);
                if (selectedItemForEdit != null) {
                    populateEditFields(selectedItemForEdit);
                } else {
                    clearEditFields();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clearEditFields();
                selectedItemForEdit = null;
            }
        });

        saveChangesButton.setOnClickListener(v -> {
            if (selectedItemForEdit == null) {
                Toast.makeText(getContext(), "Không có sản phầm nào được chọn", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = nameEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String priceStr = priceEditText.getText().toString();
            String imageUrl = imageUrlEditText.getText().toString();
            String category = categorySpinner.getSelectedItem().toString();

            // Basic validation
            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng điền tên và giá", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                // Update selected ClothingItem's properties
                selectedItemForEdit.setName(name);
                selectedItemForEdit.setDescription(description);
                selectedItemForEdit.setPrice(price);
                selectedItemForEdit.setImageUrl(imageUrl);
                selectedItemForEdit.setCategory(category);

                Toast.makeText(getContext(), "Đã cập nhật thay đổi cho: " + name, Toast.LENGTH_SHORT).show();

                // Optionally refresh the item selection spinner
                populateItemSelectionSpinner(); // To reflect name changes in the spinner

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Định dạng giá tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void populateItemSelectionSpinner() {
        List<String> itemNames = new ArrayList<>();
        for (ClothingItem item : clothingItemList) {
            itemNames.add(item.getName());
        }
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, itemNames);
        itemSelectionSpinner.setAdapter(itemAdapter);
    }

    private ClothingItem findClothingItemByName(String itemName) {
        for (ClothingItem item : clothingItemList) {
            if (item.getName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    private void populateEditFields(ClothingItem item) {
        nameEditText.setText(item.getName());
        descriptionEditText.setText(item.getDescription());
        priceEditText.setText(String.valueOf(item.getPrice()));
        imageUrlEditText.setText(item.getImageUrl());
        // Select category in spinner (you might need to find the index based on category string)
        int categoryIndex = getCategoryIndex(item.getCategory());
        if (categoryIndex != -1) {
            categorySpinner.setSelection(categoryIndex);
        }
    }

    private void clearEditFields() {
        nameEditText.setText("");
        descriptionEditText.setText("");
        priceEditText.setText("");
        imageUrlEditText.setText("");
        categorySpinner.setSelection(0); // Reset category to first item
    }

    private int getCategoryIndex(String categoryName) {
        ArrayAdapter categoryAdapter = (ArrayAdapter) categorySpinner.getAdapter();
        if (categoryAdapter != null) {
            for (int i = 0; i < categoryAdapter.getCount(); i++) {
                if (categoryAdapter.getItem(i).toString().equals(categoryName)) {
                    return i;
                }
            }
        }
        return -1; // Category not found in spinner
    }
}