package com.techelevator;

public class Gum extends Item{

    public Gum(String position, String name, double price){
        super(position, name, price);
    }

    @Override
    public String toString() {
        return "Chew Chew, Yum!";
    }
}
