package com.example.myecommerceapp.ui.home;

import com.example.myecommerceapp.data.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Search and category filtering for the catalog. */
public final class ProductFilter {

    private ProductFilter() {
    }

    /**
     * @param query    matched case-insensitively against name and brand; blank matches all
     * @param category category key, or null for all categories
     */
    public static List<Product> apply(List<Product> products, String query, String category) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (category != null && !category.equals(product.getCategory())) continue;
            if (!needle.isEmpty()
                    && !product.getName().toLowerCase(Locale.ROOT).contains(needle)
                    && !product.getBrand().toLowerCase(Locale.ROOT).contains(needle)) continue;
            result.add(product);
        }
        return result;
    }
}
