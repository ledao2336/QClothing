package com.example.qclothing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Declare and initialize clothingItemList as public static
    public static List<ClothingItem> clothingItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize mock data here in MainActivity's onCreate (or a better place if needed)
        if (clothingItemList.isEmpty()) { // Initialize only if it's empty, to avoid re-initializing on config changes
            clothingItemList.addAll(createMockClothingItems());
        }


        if (savedInstanceState == null) {
            // Load ShoppingFragment initially
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ShoppingFragment());
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu); // You'll need to create main_menu.xml in res/menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cart) {
            Intent cartIntent = new Intent(this, CartActivity.class);
            startActivity(cartIntent);
            return true;
        } else if (id == R.id.action_login_signup) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Move createMockClothingItems method to MainActivity for easier access to clothingItemList
    private List<ClothingItem> createMockClothingItems() {
        List<ClothingItem> items = new ArrayList<>();
        items.add(new ClothingItem("T-Shirt 1", "Cool T-shirt", 25.00, "image_url_1", getString(R.string.category_tops), "TSHIRT1")); // Use getString(R.string.category_tops)
        items.add(new ClothingItem("Jeans 1", "Stylish Jeans", 50.00, "image_url_2", getString(R.string.category_bottoms), "JEANS1")); // Use getString(R.string.category_bottoms)
        items.add(new ClothingItem("Dress 1", "Elegant Dress", 75.00, "image_url_3", getString(R.string.category_dresses), "DRESS1")); // Use getString(R.string.category_dresses)
        items.add(new ClothingItem("Jacket 1", "Warm Jacket", 90.00, "image_url_4", getString(R.string.category_outerwear), "JACKET1")); // Use getString(R.string.category_outerwear)
        items.add(new ClothingItem("T-Shirt 2", "Another T-shirt", 20.00, "image_url_5", getString(R.string.category_tops), "TSHIRT2")); // Use getString(R.string.category_tops)
        items.add(new ClothingItem("Skirt 1", "Nice Skirt", 40.00, "image_url_6", getString(R.string.category_bottoms), "SKIRT1")); // Use getString(R.string.category_bottoms)
        items.add(new ClothingItem("Bloomer", "American Bloomer", 80.00, "image_url_7", getString(R.string.category_bottoms), "BLOOMER"));
        items.add(new ClothingItem("Craft Top", "Hand-crafted designer top", 65.00, "image_url_8", getString(R.string.category_tops), "CRAFT_TOP1"));
        items.add(new ClothingItem("Tailored Trouser", "Classic tailored trousers", 70.00, "image_url_9", getString(R.string.category_bottoms), "TROUSER1"));
        items.add(new ClothingItem("Leather Gloves", "Fine leather gloves", 35.00, "image_url_10", getString(R.string.category_accessories), "GLOVES1")); // Use getString(R.string.category_accessories)
        items.add(new ClothingItem("Khaki Pants", "Durable khaki pants", 45.00, "image_url_11", getString(R.string.category_bottoms), "KHAKI_PANTS1"));
        items.add(new ClothingItem("Mini Skirt", "Trendy mini skirt", 30.00, "image_url_12", getString(R.string.category_bottoms), "MINI_SKIRT1"));
        items.add(new ClothingItem("Short Pants", "Casual short pants", 35.00, "image_url_13", getString(R.string.category_bottoms), "SHORT_PANTS1"));
        items.add(new ClothingItem("Wide Leg Pants", "Comfortable wide leg pants", 55.00, "image_url_14", getString(R.string.category_bottoms), "WIDE_LEG_PANTS1"));
        items.add(new ClothingItem("Fedora Hat", "Stylish fedora hat", 40.00, "image_url_15", getString(R.string.category_accessories), "FEDORA_HAT1"));

        return items;
    }
}