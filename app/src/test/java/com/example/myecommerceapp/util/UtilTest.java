package com.example.myecommerceapp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.math.BigDecimal;

public class UtilTest {

    @Test
    public void priceFormatter_usesTurkishLiraFormat() {
        assertEquals("₺25.000,00", PriceFormatter.format(new BigDecimal("25000")));
        assertEquals("₺899,90", PriceFormatter.format(new BigDecimal("899.9")));
    }

    @Test
    public void validEmails_areAccepted() {
        assertTrue(Validators.isValidEmail("ahmet@example.com"));
        assertTrue(Validators.isValidEmail("first.last+shop@mail.co.uk"));
    }

    @Test
    public void invalidEmails_areRejected() {
        assertFalse(Validators.isValidEmail(null));
        assertFalse(Validators.isValidEmail(""));
        assertFalse(Validators.isValidEmail("ahmet"));
        assertFalse(Validators.isValidEmail("ahmet@example"));
        assertFalse(Validators.isValidEmail("@example.com"));
    }

    @Test
    public void password_needsMinimumLength() {
        assertFalse(Validators.isValidPassword("12345"));
        assertTrue(Validators.isValidPassword("123456"));
        assertFalse(Validators.isValidPassword(null));
    }

    @Test
    public void event_isDeliveredOnlyOnce() {
        Event<String> event = new Event<>("hello");

        assertEquals("hello", event.getContentIfNotHandled());
        assertNull(event.getContentIfNotHandled());
        assertEquals("hello", event.peekContent());
    }
}
