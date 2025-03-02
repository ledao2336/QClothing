package com.example.qclothing;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShoppingFragment extends Fragment {

    private static final String TAG = "ShoppingFragment";

    private RecyclerView clothingRecyclerView;
    private ClothingItemAdapter clothingItemAdapter;
    private Spinner categorySpinner;
    private EditText searchBar;
    private Spinner sortSpinner;

    private List<ClothingItem> clothingItemList;
    private List<ClothingItem> originalClothingItemList;
    private String currentCategoryFilter;
    private String currentSortOrder = "Mặc định";

    private DatabaseManager databaseManager;

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

        // Initialize database manager
        databaseManager = new DatabaseManager(getContext());

        // Fetch data from database
        fetchClothingItemsFromDatabase();

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

    private void fetchClothingItemsFromDatabase() {
        try {
            // Open database connection
            databaseManager.open();

            // Clear current lists
            clothingItemList.clear();
            originalClothingItemList.clear();

            // Get all clothing items from database
            List<ClothingItem> items = databaseManager.getAllClothingItems();

            if (items != null && !items.isEmpty()) {
                Log.d(TAG, "Loaded " + items.size() + " items from database");
                // Add items to our lists
                clothingItemList.addAll(items);
                originalClothingItemList.addAll(items);
            } else {
                Log.d(TAG, "No items found in database, falling back to static list");
                // If database returned no items, fall back to static list in MainActivity
                clothingItemList.addAll(MainActivity.clothingItemList);
                originalClothingItemList.addAll(MainActivity.clothingItemList);
            }

            // Notify adapter that data has changed
            clothingItemAdapter.notifyDataSetChanged();

            // Apply filters and sorting
            applyFiltersAndSorting();

        } catch (Exception e) {
            Log.e(TAG, "Error fetching clothing items from database", e);
            Toast.makeText(getContext(), "Failed to load items from database", Toast.LENGTH_SHORT).show();

            // If database access fails, use static list from MainActivity
            clothingItemList.clear();
            originalClothingItemList.clear();
            clothingItemList.addAll(MainActivity.clothingItemList);
            originalClothingItemList.addAll(MainActivity.clothingItemList);
            clothingItemAdapter.notifyDataSetChanged();
            applyFiltersAndSorting();
        } finally {
            // Close database connection
            if (databaseManager != null && databaseManager.isOpen()) {
                databaseManager.close();
            }
        }
    }

    private void applyFiltersAndSorting() {
        String searchText = searchBar.getText().toString().toLowerCase().trim();
        List<ClothingItem> filteredList = new ArrayList<>(originalClothingItemList);

        // Apply Category Filter
        if (!currentCategoryFilter.equals(getString(R.string.category_all_categories))) {
            List<ClothingItem> categoryFilteredList = new ArrayList<>();
            for (ClothingItem item : originalClothingItemList) {
                if (item.getCategory() != null && item.getCategory().equals(currentCategoryFilter)) {
                    categoryFilteredList.add(item);
                }
            }
            filteredList = categoryFilteredList;
        }

        // Apply Search Filter
        if (!searchText.isEmpty()) {
            List<ClothingItem> searchFilteredList = new ArrayList<>();
            for (ClothingItem item : filteredList) {
                if ((item.getName() != null && item.getName().toLowerCase().contains(searchText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(searchText))) {
                    searchFilteredList.add(item);
                }
            }
            filteredList = searchFilteredList;
        }

        // Apply Sorting
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

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to fragment
        fetchClothingItemsFromDatabase();
    }
}