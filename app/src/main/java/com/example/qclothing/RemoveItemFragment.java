package com.example.qclothing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemoveItemFragment extends Fragment {

    private Spinner itemSelectionSpinner;
    private Button removeItemButton;
    private List<ClothingItem> clothingItemList; // Reference to the same data as ShoppingFragment

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remove_item, container, false);

        itemSelectionSpinner = view.findViewById(R.id.remove_item_selection_spinner);
        removeItemButton = view.findViewById(R.id.remove_item_button);

        // Get reference to the same clothing item list as ShoppingFragment (for mock data)
        clothingItemList = MainActivity.clothingItemList; // Assuming static list in MainActivity

        // Populate item selection spinner
        populateItemSelectionSpinner();

        removeItemButton.setOnClickListener(v -> {
            String selectedItemName = (String) itemSelectionSpinner.getSelectedItem();
            if (selectedItemName != null) {
                removeItemByName(selectedItemName);
                Toast.makeText(getContext(), "Đã xóa: " + selectedItemName, Toast.LENGTH_SHORT).show();
                populateItemSelectionSpinner(); // Refresh spinner after removal
            } else {
                Toast.makeText(getContext(), "Không có sản phẩm để xóa", Toast.LENGTH_SHORT).show();
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

    private void removeItemByName(String itemName) {
        ClothingItem itemToRemove = null;

        // Find the item to remove
        for (ClothingItem item : clothingItemList) {
            if (item.getName().equals(itemName)) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove != null) {
            // Create a DatabaseManager to update the database
            DatabaseManager dbManager = new DatabaseManager(getContext());
            try {
                dbManager.open();

                // Delete the item from the database
                int result = dbManager.deleteClothingItem(itemToRemove.getItemId());

                if (result > 0) {
                    // If database deletion succeeded, remove from in-memory list
                    Iterator<ClothingItem> iterator = clothingItemList.iterator();
                    while (iterator.hasNext()) {
                        ClothingItem item = iterator.next();
                        if (item.getName().equals(itemName)) {
                            iterator.remove(); // Safely remove using iterator
                            break;
                        }
                    }
                    Toast.makeText(getContext(), "Đã xóa: " + itemName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Không thể xóa sản phẩm trong cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                if (dbManager != null && dbManager.isOpen()) {
                    dbManager.close();
                }
            }
        }
    }
}