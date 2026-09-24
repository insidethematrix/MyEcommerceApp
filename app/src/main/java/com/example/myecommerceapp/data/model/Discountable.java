package com.example.myecommerceapp.data.model;

import java.math.BigDecimal;

/** Implemented by products that currently have a discount. */
public interface Discountable {
    /** Discount amount for a single unit, in TL. */
    BigDecimal calculateDiscount();

    /** Discount as a whole percentage, for display (e.g. 10). */
    int getDiscountPercent();
}
