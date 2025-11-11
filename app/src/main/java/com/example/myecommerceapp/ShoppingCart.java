package com.example.myecommerceapp;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<Product> productsInCart= new ArrayList<>();

    public ShoppingCart(){}
    public void addProduct(Product product){
        productsInCart.add(product);
    }
    public double calculateTotalPrice(){
        double total=0.0;
        for(Product item :productsInCart ){
            total+=item.getPrice()+ item.calculateTax();
        }
        return total;
    }
}
