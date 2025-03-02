package com.example.qclothing;

public class CartItem {

    private ClothingItem clothingItem;
    private int quantity;

    public CartItem(ClothingItem clothingItem, int quantity) {
        this.clothingItem = clothingItem;
        this.quantity = quantity;
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
}