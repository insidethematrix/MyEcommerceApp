package com.example.myecommerceapp.data.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Clothing extends Product {
    public static final String CATEGORY = "clothing";
    static final BigDecimal TAX_RATE = new BigDecimal("0.08");

    private final String size;
    private final String color;

    public Clothing(String id, String name, String brand, BigDecimal price,
                    String description, String imageUrl, int stock, String size, String color) {
        super(id, name, brand, price, description, imageUrl, stock);
        this.size = size;
        this.color = color;
    }

    @Override
    public BigDecimal getTaxRate() {
        return TAX_RATE;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Clothing other = (Clothing) o;
        return Objects.equals(size, other.size) && Objects.equals(color, other.color);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
