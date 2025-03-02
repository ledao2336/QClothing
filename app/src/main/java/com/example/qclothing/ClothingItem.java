package com.example.qclothing;

import android.os.Parcel;
import android.os.Parcelable;

public class ClothingItem implements Parcelable {

    private String itemId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;

    public ClothingItem() {
        // Default constructor (required for Firebase if you use it later)
    }

    public ClothingItem(String name, String description, double price, String imageUrl, String category, String itemId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.itemId = itemId;
    }

    protected ClothingItem(Parcel in) {
        itemId = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        imageUrl = in.readString();
        category = in.readString();
    }

    public static final Creator<ClothingItem> CREATOR = new Creator<ClothingItem>() {
        @Override
        public ClothingItem createFromParcel(Parcel in) {
            return new ClothingItem(in);
        }

        @Override
        public ClothingItem[] newArray(int size) {
            return new ClothingItem[size];
        }
    };

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(imageUrl);
        dest.writeString(category);
    }
}