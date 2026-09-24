package com.example.myecommerceapp.data.model;

import java.math.BigDecimal;

/** A product in the cart together with how many units were added. */
public class CartItem {
    private final Product product;
    private final int quantity;

    public CartItem(Product product, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public CartItem withQuantity(int newQuantity) {
        return new CartItem(product, newQuantity);
    }

    /** Price before tax and discount. */
    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTax() {
        return product.calculateTax().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getDiscount() {
        if (product instanceof Discountable) {
            return ((Discountable) product).calculateDiscount().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getTax()).subtract(getDiscount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        CartItem other = (CartItem) o;
        return quantity == other.quantity && product.equals(other.product);
    }

    @Override
    public int hashCode() {
        return product.hashCode() * 31 + quantity;
    }
}
