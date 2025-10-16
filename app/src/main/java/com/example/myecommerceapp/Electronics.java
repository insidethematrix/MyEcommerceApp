package com.example.myecommerceapp;

public class Electronics extends Product {
    private String brand;
    private int warrantyPeriod;

    public Electronics(String id, String name, double price, String brand, int warrantyPeriod){
        super(id, name,price);
        this.brand = brand;
        this.warrantyPeriod = warrantyPeriod;
    }

    @Override
    public double calculateTax(){
        return getPrice()*0.20;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getWarrantyPeriod() {
        return warrantyPeriod;
    }

    public void setWarrantyPeriod(int warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }
}
