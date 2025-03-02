package com.example.qclothing;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * OrderItem model class to represent an item in an order.
 */
public class OrderItem implements Parcelable {
    private long orderId;
    private ClothingItem item;
    private int quantity;
    private double price; // Price at time of order (may differ from current item price)
    
    public OrderItem() {
        // Default constructor
    }
    
    public OrderItem(ClothingItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.price = item.getPrice(); // Use current item price by default
    }
    
    // Getters and setters
    public long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    
    public ClothingItem getItem() {
        return item;
    }
    
    public void setItem(ClothingItem item) {
        this.item = item;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public double getSubtotal() {
        return price * quantity;
    }
    
    // Parcelable implementation
    protected OrderItem(Parcel in) {
        orderId = in.readLong();
        item = in.readParcelable(ClothingItem.class.getClassLoader());
        quantity = in.readInt();
        price = in.readDouble();
    }
    
    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }
        
        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(orderId);
        dest.writeParcelable(item, flags);
        dest.writeInt(quantity);
        dest.writeDouble(price);
    }
}