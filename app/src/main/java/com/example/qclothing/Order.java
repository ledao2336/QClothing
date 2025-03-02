package com.example.qclothing;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Order model class to represent an order in the application.
 */
public class Order implements Parcelable {
    private long id;
    private long userId;
    private String userName; // For admin view
    private long orderDate;
    private double total;
    private String status;
    private List<OrderItem> items;
    
    public Order() {
        items = new ArrayList<>();
    }
    
    // Getters and setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getUserId() {
        return userId;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public long getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(new Date(orderDate));
    }
    
    public String getFormattedTotal() {
        return String.format(Locale.getDefault(), "%.3f VND", total);
    }
    
    // Parcelable implementation
    protected Order(Parcel in) {
        id = in.readLong();
        userId = in.readLong();
        userName = in.readString();
        orderDate = in.readLong();
        total = in.readDouble();
        status = in.readString();
        items = new ArrayList<>();
        in.readTypedList(items, OrderItem.CREATOR);
    }
    
    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }
        
        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(userId);
        dest.writeString(userName);
        dest.writeLong(orderDate);
        dest.writeDouble(total);
        dest.writeString(status);
        dest.writeTypedList(items);
    }
}