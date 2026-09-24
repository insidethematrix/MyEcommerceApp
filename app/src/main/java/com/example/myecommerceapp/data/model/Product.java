package com.example.myecommerceapp.data.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Base type for everything sold in the store. Each subclass defines its own tax rate,
 * and may additionally implement {@link Discountable}.
 *
 * Money is kept in {@link BigDecimal} to avoid floating point rounding errors.
 */
public abstract class Product {
    private final String id;
    private final String name;
    private final String brand;
    private final BigDecimal price;
    private final String description;
    private final String imageUrl;
    private final int stock;

    protected Product(String id, String name, String brand, BigDecimal price,
                      String description, String imageUrl, int stock) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.stock = stock;
    }

    /** Tax rate applied to this product, e.g. 0.20 for 20%. */
    public abstract BigDecimal getTaxRate();

    /** Category key stored in Firestore, see {@link ProductFactory}. */
    public abstract String getCategory();

    public BigDecimal calculateTax() {
        return price.multiply(getTaxRate()).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product other = (Product) o;
        return id.equals(other.id)
                && name.equals(other.name)
                && Objects.equals(brand, other.brand)
                && price.compareTo(other.price) == 0
                && Objects.equals(description, other.description)
                && Objects.equals(imageUrl, other.imageUrl)
                && stock == other.stock;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
