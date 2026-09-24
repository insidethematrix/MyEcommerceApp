package com.example.myecommerceapp.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProductFactoryTest {

    @Test
    public void electronicsCategory_createsElectronics() {
        Map<String, Object> data = base("electronics");
        data.put("warrantyMonths", 24L);

        Product product = ProductFactory.fromMap("p1", data);

        assertTrue(product instanceof Electronics);
        assertEquals(24, ((Electronics) product).getWarrantyMonths());
        assertTrue(product instanceof Discountable);
    }

    @Test
    public void clothingCategory_createsClothing() {
        Map<String, Object> data = base("clothing");
        data.put("size", "L");
        data.put("color", "Red");

        Product product = ProductFactory.fromMap("p1", data);

        assertTrue(product instanceof Clothing);
        assertEquals("L", ((Clothing) product).getSize());
        assertEquals("Red", ((Clothing) product).getColor());
    }

    @Test
    public void commonFields_areMapped() {
        Product product = ProductFactory.fromMap("p1", base("clothing"));

        assertEquals("p1", product.getId());
        assertEquals("Shirt", product.getName());
        assertEquals("Avora", product.getBrand());
        assertEquals(0, new BigDecimal("899.9").compareTo(product.getPrice()));
        assertEquals(7, product.getStock());
    }

    @Test
    public void integerPrice_fromFirestoreLong_isAccepted() {
        Map<String, Object> data = base("clothing");
        data.put("price", 500L);

        Product product = ProductFactory.fromMap("p1", data);

        assertEquals(0, new BigDecimal("500").compareTo(product.getPrice()));
    }

    @Test
    public void missingOptionalFields_useDefaults() {
        Map<String, Object> data = new HashMap<>();
        data.put("category", "electronics");
        data.put("name", "Mouse");
        data.put("price", 750.0);

        Electronics product = (Electronics) ProductFactory.fromMap("p1", data);

        assertEquals("", product.getBrand());
        assertEquals(0, product.getStock());
        assertEquals(0, product.getWarrantyMonths());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownCategory_throws() {
        ProductFactory.fromMap("p1", base("furniture"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingPrice_throws() {
        Map<String, Object> data = base("clothing");
        data.remove("price");
        ProductFactory.fromMap("p1", data);
    }

    private static Map<String, Object> base(String category) {
        Map<String, Object> data = new HashMap<>();
        data.put("category", category);
        data.put("name", "Shirt");
        data.put("brand", "Avora");
        data.put("price", 899.9);
        data.put("stock", 7L);
        return data;
    }
}
