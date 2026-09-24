package com.example.myecommerceapp.model;

import com.example.myecommerceapp.Product;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity){
        this.product=product;
        this.quantity=quantity;
    }
    public Product getProduct(){
        return product;
    }
    public void setProduct(Product product){
        this.product  = product;
    }
    public int getQuantity(){
        return quantity;
    }
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
    public double getSubTotal(){
        // Multiply the product's price by the quantity
        return product.getPrice() * quantity;
    }

}
