package com.example.myecommerceapp.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/** Formats amounts as Turkish Lira, e.g. "₺25.000,00". */
public final class PriceFormatter {
    private static final Locale TURKEY = Locale.forLanguageTag("tr-TR");

    private PriceFormatter() {
    }

    public static String format(BigDecimal amount) {
        // NumberFormat is not thread-safe, so create one per call
        return NumberFormat.getCurrencyInstance(TURKEY).format(amount);
    }
}
