package com.example.myecommerceapp;

public class Clothing extends Product {
    private String size;
    private String color;

    public Clothing(String id, String name, double price, String size, String color){
        super(id, name, price);
        this.size = size;
        this.color = color;
    }

    @Override
    public double calculateTax(){
        return getPrice()*0.08;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
