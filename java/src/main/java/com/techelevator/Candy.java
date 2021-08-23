package com.techelevator;

public class Candy extends Item{

    public Candy(String position, String name, double price){
        super(position, name, price);
    }

    @Override
    public String toString() {
        return "Munch Munch, Yum!";
    }
}
