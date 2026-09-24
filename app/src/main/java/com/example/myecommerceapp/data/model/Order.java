package com.example.myecommerceapp.data.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** A completed purchase, stored under users/{uid}/orders. */
public class Order {
    private final String id;
    private final Date createdAt;
    private final List<Line> lines;
    private final BigDecimal total;
    private final String status;

    public Order(String id, Date createdAt, List<Line> lines, BigDecimal total, String status) {
        this.id = id;
        this.createdAt = createdAt;
        this.lines = Collections.unmodifiableList(lines);
        this.total = total;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    /** Firestore ids are 20 characters; the first 8 are enough for the user to reference. */
    public static String shortId(String orderId) {
        return orderId.substring(0, Math.min(8, orderId.length())).toUpperCase(Locale.ROOT);
    }

    public String getShortId() {
        return shortId(id);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public List<Line> getLines() {
        return lines;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public int getItemCount() {
        int count = 0;
        for (Line line : lines) {
            count += line.getQuantity();
        }
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order other = (Order) o;
        return id.equals(other.id) && status.equals(other.status);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /** Snapshot of a product at purchase time, so later price changes don't alter history. */
    public static class Line {
        private final String productId;
        private final String name;
        private final int quantity;
        private final BigDecimal unitPrice;

        public Line(String productId, String name, int quantity, BigDecimal unitPrice) {
            this.productId = productId;
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getProductId() {
            return productId;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
    }
}
