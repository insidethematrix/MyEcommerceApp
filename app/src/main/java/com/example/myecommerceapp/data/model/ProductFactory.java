package com.example.myecommerceapp.data.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Builds the right {@link Product} subclass from a Firestore document.
 *
 * Firestore's automatic toObject() mapping can't handle abstract classes, so every product
 * document stores a {@code category} discriminator and this factory picks the subclass.
 */
public final class ProductFactory {

    private ProductFactory() {
    }

    /**
     * @throws IllegalArgumentException if the category is unknown or a required field is missing
     */
    public static Product fromMap(String id, Map<String, Object> data) {
        String category = requireString(data, "category");
        String name = requireString(data, "name");
        BigDecimal price = BigDecimal.valueOf(requireNumber(data, "price").doubleValue());
        String brand = optString(data, "brand");
        String description = optString(data, "description");
        String imageUrl = optString(data, "imageUrl");
        int stock = optNumber(data, "stock", 0).intValue();

        switch (category) {
            case Electronics.CATEGORY:
                int warranty = optNumber(data, "warrantyMonths", 0).intValue();
                return new Electronics(id, name, brand, price, description, imageUrl, stock, warranty);
            case Clothing.CATEGORY:
                return new Clothing(id, name, brand, price, description, imageUrl, stock,
                        optString(data, "size"), optString(data, "color"));
            default:
                throw new IllegalArgumentException("Unknown product category: " + category);
        }
    }

    private static String requireString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Missing field: " + key);
        }
        return (String) value;
    }

    private static Number requireNumber(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (!(value instanceof Number)) {
            throw new IllegalArgumentException("Missing field: " + key);
        }
        return (Number) value;
    }

    private static String optString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value instanceof String ? (String) value : "";
    }

    private static Number optNumber(Map<String, Object> data, String key, Number fallback) {
        Object value = data.get(key);
        return value instanceof Number ? (Number) value : fallback;
    }
}
