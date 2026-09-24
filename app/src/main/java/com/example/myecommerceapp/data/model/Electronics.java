package com.example.myecommerceapp.data.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Electronics extends Product implements Discountable {
    public static final String CATEGORY = "electronics";
    static final BigDecimal TAX_RATE = new BigDecimal("0.20");
    static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");

    private final int warrantyMonths;

    public Electronics(String id, String name, String brand, BigDecimal price,
                       String description, String imageUrl, int stock, int warrantyMonths) {
        super(id, name, brand, price, description, imageUrl, stock);
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public BigDecimal getTaxRate() {
        return TAX_RATE;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public BigDecimal calculateDiscount() {
        return getPrice().multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int getDiscountPercent() {
        return DISCOUNT_RATE.movePointRight(2).intValue();
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && warrantyMonths == ((Electronics) o).warrantyMonths;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
