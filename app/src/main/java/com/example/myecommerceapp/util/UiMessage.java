package com.example.myecommerceapp.util;

import android.content.Context;

import com.example.myecommerceapp.R;

import java.util.Arrays;

/**
 * Text to show to the user: either a localized string resource (with format args) or a raw
 * message from the backend. ViewModels produce these without needing a Context.
 */
public final class UiMessage {
    private final int res;
    private final Object[] args;
    private final String text;

    private UiMessage(int res, Object[] args, String text) {
        this.res = res;
        this.args = args;
        this.text = text;
    }

    public static UiMessage of(int res, Object... args) {
        return new UiMessage(res, args, null);
    }

    public static UiMessage text(String text) {
        return new UiMessage(0, new Object[0], text);
    }

    public String resolve(Context context) {
        if (res != 0) return context.getString(res, args);
        if (text != null && !text.isEmpty()) return text;
        return context.getString(R.string.error_generic);
    }

    public int getRes() {
        return res;
    }

    public Object[] getArgs() {
        return Arrays.copyOf(args, args.length);
    }

    public String getText() {
        return text;
    }
}
