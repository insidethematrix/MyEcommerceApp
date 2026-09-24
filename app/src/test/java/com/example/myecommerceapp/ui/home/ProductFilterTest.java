package com.example.myecommerceapp.ui.home;

import static org.junit.Assert.assertEquals;

import com.example.myecommerceapp.TestData;
import com.example.myecommerceapp.data.model.Electronics;
import com.example.myecommerceapp.data.model.Product;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ProductFilterTest {
    private final List<Product> catalog = Arrays.asList(
            TestData.electronics("e1", "MacBook Pro", "Apple", "89999", 1),
            TestData.electronics("e2", "AirPods Max", "Apple", "21999", 1),
            TestData.clothing("c1", "Air Jordan 1", "Nike", "6999.9", 1));

    @Test
    public void blankQuery_matchesEverything() {
        assertEquals(3, ProductFilter.apply(catalog, "   ", null).size());
        assertEquals(3, ProductFilter.apply(catalog, null, null).size());
    }

    @Test
    public void search_isCaseInsensitive() {
        assertEquals(1, ProductFilter.apply(catalog, "MACBOOK", null).size());
    }

    @Test
    public void search_matchesBrand() {
        assertEquals(2, ProductFilter.apply(catalog, "apple", null).size());
    }

    @Test
    public void search_andCategory_mustBothMatch() {
        // "air" matches AirPods (electronics) and Air Jordan (clothing)
        List<Product> result = ProductFilter.apply(catalog, "air", Electronics.CATEGORY);

        assertEquals(1, result.size());
        assertEquals("e2", result.get(0).getId());
    }
}
