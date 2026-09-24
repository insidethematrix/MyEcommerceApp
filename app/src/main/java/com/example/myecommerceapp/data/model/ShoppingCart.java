package com.example.myecommerceapp.data.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pure pricing logic for a set of cart items. Holds no Android or Firebase references,
 * so it is fully covered by plain JVM unit tests.
 */
public class ShoppingCart {
    private final List<CartItem> items = new ArrayList<>();

    public ShoppingCart() {
    }

    public ShoppingCart(List<CartItem> items) {
        this.items.addAll(items);
    }

    /** Adds one unit of the product, merging with an existing line if present. */
    public void addProduct(Product product) {
        int index = indexOf(product.getId());
        if (index == -1) {
            items.add(new CartItem(product, 1));
        } else {
            CartItem existing = items.get(index);
            items.set(index, existing.withQuantity(existing.getQuantity() + 1));
        }
    }

    /** Sets the quantity for a product; a quantity of zero or less removes it. */
    public void setQuantity(String productId, int quantity) {
        int index = indexOf(productId);
        if (index == -1) return;
        if (quantity <= 0) {
            items.remove(index);
        } else {
            items.set(index, items.get(index).withQuantity(quantity));
        }
    }

    public void removeProduct(String productId) {
        setQuantity(productId, 0);
    }

    public void clear() {
        items.clear();
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Total number of units, used for the cart badge. */
    public int getItemCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public BigDecimal getSubtotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : items) {
            sum = sum.add(item.getSubtotal());
        }
        return scale(sum);
    }

    public BigDecimal getTaxTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : items) {
            sum = sum.add(item.getTax());
        }
        return scale(sum);
    }

    public BigDecimal getDiscountTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : items) {
            sum = sum.add(item.getDiscount());
        }
        return scale(sum);
    }

    /** Subtotal + tax - discount. */
    public BigDecimal getTotal() {
        return getSubtotal().add(getTaxTotal()).subtract(getDiscountTotal());
    }

    private int indexOf(String productId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProduct().getId().equals(productId)) return i;
        }
        return -1;
    }

    private static BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
