package com.example.myecommerceapp.util;

import java.util.regex.Pattern;

/** Input validation kept free of Android APIs so it can be unit tested on the JVM. */
public final class Validators {
    public static final int MIN_PASSWORD_LENGTH = 6;

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validators() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }
}
