package com.techelevator;

public class Drink extends Item{

    public Drink(String position, String name, double price){
        super(position, name, price);
    }

    @Override
    public String toString() {
        return "Glug Glug, Yum!";
    }
}
