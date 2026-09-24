package com.example.myecommerceapp;

import com.example.myecommerceapp.data.model.Clothing;
import com.example.myecommerceapp.data.model.Electronics;

import java.math.BigDecimal;

/** Shared fixtures for unit tests. */
public final class TestData {

    private TestData() {
    }

    public static Electronics laptop() {
        return electronics("elc-laptop", "Laptop Pro", "TechBrand", "25000.00", 5);
    }

    public static Electronics electronics(String id, String name, String brand, String price, int stock) {
        return new Electronics(id, name, brand, new BigDecimal(price), "", "", stock, 24);
    }

    public static Clothing tshirt() {
        return clothing("clt-tshirt", "Basic T-Shirt", "Avora", "350.00", 10);
    }

    public static Clothing clothing(String id, String name, String brand, String price, int stock) {
        return new Clothing(id, name, brand, new BigDecimal(price), "", "", stock, "M", "White");
    }
}
