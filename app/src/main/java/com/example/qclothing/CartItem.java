package com.example.qclothing;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * CartItem model class to represent an item in a user's shopping cart.
 */
public class CartItem implements Parcelable {
    private long id; // Database ID
    private ClothingItem clothingItem;
    private int quantity;

    public CartItem(ClothingItem clothingItem, int quantity) {
        this.clothingItem = clothingItem;
        this.quantity = quantity;
    }

    protected CartItem(Parcel in) {
        id = in.readLong();
        clothingItem = in.readParcelable(ClothingItem.class.getClassLoader());
        quantity = in.readInt();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ClothingItem getClothingItem() {
        return clothingItem;
    }

    public void setClothingItem(ClothingItem clothingItem) {
        this.clothingItem = clothingItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity() {
        this.quantity++;
    }

    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }
    
    public double getSubtotal() {
        return clothingItem.getPrice() * quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(clothingItem, flags);
        dest.writeInt(quantity);
    }
}