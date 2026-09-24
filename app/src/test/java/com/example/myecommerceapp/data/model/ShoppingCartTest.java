package com.example.myecommerceapp.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.myecommerceapp.TestData;

import org.junit.Test;

import java.math.BigDecimal;

public class ShoppingCartTest {

    @Test
    public void emptyCart_hasZeroTotals() {
        ShoppingCart cart = new ShoppingCart();

        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItemCount());
        assertMoney("0.00", cart.getTotal());
    }

    @Test
    public void electronics_getsTwentyPercentTaxAndTenPercentDiscount() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(TestData.laptop()); // 25,000

        assertMoney("25000.00", cart.getSubtotal());
        assertMoney("5000.00", cart.getTaxTotal());
        assertMoney("2500.00", cart.getDiscountTotal());
        assertMoney("27500.00", cart.getTotal());
    }

    @Test
    public void clothing_getsEightPercentTaxAndNoDiscount() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(TestData.tshirt()); // 350

        assertMoney("28.00", cart.getTaxTotal());
        assertMoney("0.00", cart.getDiscountTotal());
        assertMoney("378.00", cart.getTotal());
    }

    @Test
    public void addingSameProductTwice_mergesIntoOneLine() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(TestData.tshirt());
        cart.addProduct(TestData.tshirt());

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(2, cart.getItemCount());
        assertMoney("756.00", cart.getTotal());
    }

    @Test
    public void mixedCart_sumsEveryLine() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(TestData.laptop());
        cart.addProduct(TestData.tshirt());
        cart.setQuantity(TestData.tshirt().getId(), 3);

        // 27,500 for the laptop + 3 x 378 for the shirts
        assertMoney("28634.00", cart.getTotal());
        assertEquals(4, cart.getItemCount());
    }

    @Test
    public void setQuantityToZero_removesLine() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(TestData.laptop());

        cart.setQuantity(TestData.laptop().getId(), 0);

        assertTrue(cart.isEmpty());
    }

    @Test
    public void removeProduct_leavesOtherLines() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(TestData.laptop());
        cart.addProduct(TestData.tshirt());

        cart.removeProduct(TestData.laptop().getId());

        assertEquals(1, cart.getItems().size());
        assertEquals(TestData.tshirt().getId(), cart.getItems().get(0).getProduct().getId());
    }

    @Test
    public void setQuantity_forUnknownProduct_isIgnored() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(TestData.tshirt());

        cart.setQuantity("does-not-exist", 5);

        assertEquals(1, cart.getItemCount());
    }

    @Test
    public void fractionalPrices_areRoundedToKurus() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(TestData.clothing("c1", "Socks", "Avora", "99.99", 5));

        // 8% of 99.99 = 7.9992, rounded half-up to 8.00
        assertMoney("8.00", cart.getTaxTotal());
        assertMoney("107.99", cart.getTotal());
    }

    @Test
    public void clear_removesEverything() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(TestData.laptop());
        cart.addProduct(TestData.tshirt());

        cart.clear();

        assertTrue(cart.isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void items_cannotBeModifiedFromOutside() {
        ShoppingCart cart = new ShoppingCart();
        cart.getItems().add(new CartItem(TestData.laptop(), 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cartItem_rejectsZeroQuantity() {
        new CartItem(TestData.laptop(), 0);
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(new BigDecimal(expected), actual);
    }
}
