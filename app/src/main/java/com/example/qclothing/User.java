package com.example.qclothing;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User model class to represent a user in the application.
 */
public class User implements Parcelable {
    private long id;
    private String name;
    private String email;
    private String phone;
    private boolean isAdmin;
    
    public User() {
        // Default constructor
    }
    
    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.isAdmin = false;
    }
    
    // Getters and setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
    
    // Parcelable implementation
    protected User(Parcel in) {
        id = in.readLong();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        isAdmin = in.readByte() != 0;
    }
    
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }
        
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeByte((byte) (isAdmin ? 1 : 0));
    }
}