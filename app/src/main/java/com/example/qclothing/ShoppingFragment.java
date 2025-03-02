package com.example.qclothing;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qclothing.ClothingItemAdapter;
import com.example.qclothing.DatabaseHelper; // Correct import for DatabaseHelper
import com.example.qclothing.ClothingItem; // Correct import for ClothingItem

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections; // Correct import for Collections
import java.util.Comparator;  // Correct import for Comparator
import java.util.List;

public class ShoppingFragment extends Fragment {

    private RecyclerView clothingRecyclerView;
    private ClothingItemAdapter clothingItemAdapter;
    private Spinner categorySpinner;
    private EditText searchBar;
    private Spinner sortSpinner;

    private List<ClothingItem> clothingItemList;
    private List<ClothingItem> originalClothingItemList;
    private String currentCategoryFilter;
    private String currentSortOrder = "Mặc định";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        clothingRecyclerView = view.findViewById(R.id.clothing_recycler_view);
        categorySpinner = view.findViewById(R.id.category_spinner);
        searchBar = view.findViewById(R.id.search_bar);
        sortSpinner = view.findViewById(R.id.sort_spinner);

        clothingItemList = new ArrayList<>();
        originalClothingItemList = new ArrayList<>();

        clothingRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        clothingItemAdapter = new ClothingItemAdapter(getContext(), clothingItemList);
        clothingRecyclerView.setAdapter(clothingItemAdapter);

        setupCategorySpinner();
        setupSortSpinner();
        setupSearchBar();

        fetchClothingItemsFromSQLite();

        return view;
    }


    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.clothing_categories_array, android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        currentCategoryFilter = getString(R.string.category_all_categories);
        categorySpinner.setSelection(0, false);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategoryFilter = parent.getItemAtPosition(position).toString();
                applyFiltersAndSorting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }


    private void fetchClothingItemsFromSQLite() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = null;
        try { // BẮT ĐẦU try block - BAO QUANH TOÀN BỘ CODE TỪ ĐÂY

            dbHelper.prepareDataBase(); // Prepare database (copy if needed)
            db = dbHelper.openDataBase(); // Open database - Code có thể throw SQLException

            clothingItemList.clear();
            originalClothingItemList.clear();

            // --- Execute SQL Query to Fetch Clothing Items ---
            String query = "SELECT itemId, name, description, price, imageUrl, category FROM quanao";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    ClothingItem item = new ClothingItem();
                    item.setItemId(cursor.getString(cursor.getColumnIndexOrThrow("itemId")));
                    item.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                    item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")));
                    item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                    clothingItemList.add(item);
                    originalClothingItemList.add(item);
                } while (cursor.moveToNext());
            }
            cursor.close(); // Close Cursor
            clothingItemAdapter.notifyDataSetChanged();
            applyFiltersAndSorting();

        } catch (SQLException | IOException e) { // catch block - KHÔNG THAY ĐỔI
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to load items from SQLite database.", Toast.LENGTH_SHORT).show();
            Log.e("SQLite", "Error fetching clothing items from SQLite", e);
        } finally { // finally block - KHÔNG THAY ĐỔI
            if (db != null) {
                db.close(); // Close database connection in finally block
            }
            dbHelper.close(); // Close DatabaseHelper
        } // KẾT THÚC try-catch-finally block - BAO QUANH TOÀN BỘ CODE ĐẾN ĐÂY
    }



    private void applyFiltersAndSorting() {
        String searchText = searchBar.getText().toString().toLowerCase().trim();
        List<ClothingItem> filteredList = new ArrayList<>(originalClothingItemList);

        // --- Apply Category Filter ---
        if (!currentCategoryFilter.equals(getString(R.string.category_all_categories))) {
            List<ClothingItem> categoryFilteredList = new ArrayList<>();
            for (ClothingItem item : originalClothingItemList) {
                if (item.getCategory().equals(currentCategoryFilter)) {
                    categoryFilteredList.add(item);
                }
            }
            filteredList = categoryFilteredList;
        }


        // --- Apply Search Filter ---
        if (!searchText.isEmpty()) {
            List<ClothingItem> searchFilteredList = new ArrayList<>();
            for (ClothingItem item : filteredList) {
                if (item.getName().toLowerCase().contains(searchText)) {
                    searchFilteredList.add(item);
                }
            }
            filteredList = searchFilteredList;
        }

        // --- Apply Sorting ---
        sortClothingItems(filteredList, currentSortOrder);

        clothingItemList.clear();
        clothingItemList.addAll(filteredList);
        clothingItemAdapter.notifyDataSetChanged();
    }


    private void sortClothingItems(List<ClothingItem> listToSort, String sortOrder) {
        if (sortOrder.equals(getString(R.string.sort_price_low_to_high))) {
            Collections.sort(listToSort, Comparator.comparingDouble(ClothingItem::getPrice));
        } else if (sortOrder.equals(getString(R.string.sort_price_high_to_low))) {
            Collections.sort(listToSort, (item1, item2) -> Double.compare(item2.getPrice(), item1.getPrice()));
        } else if (sortOrder.equals(getString(R.string.sort_name_a_to_z))) {
            Collections.sort(listToSort, Comparator.comparing(ClothingItem::getName));
        } else if (sortOrder.equals(getString(R.string.sort_name_z_to_a))) {
            Collections.sort(listToSort, (item1, item2) -> item2.getName().compareToIgnoreCase(item1.getName()));
        }
    }


    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.sort_options, android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortOrder = parent.getItemAtPosition(position).toString();
                applyFiltersAndSorting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                applyFiltersAndSorting();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}