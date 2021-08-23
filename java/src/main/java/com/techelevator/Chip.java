package com.techelevator;

public class Chip extends Item{

    public Chip(String position, String name, double price){
        super(position, name, price);
    }

    @Override
    public String toString() {
        return "Crunch Crunch, Yum!";
    }
}
