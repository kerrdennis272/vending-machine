package com.techelevator;

public abstract class Item {
    private String position;
    private String itemName;
    private double price;
    private int quantity = 5;

    public Item(String position, String itemName, double price){
        this.position = position;
        this.itemName = itemName;
        this.price = price;
    }

    @Override
    public abstract String toString();

    public String getPosition() {
        return position;
    }

    public String getItemName() {
        return itemName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
