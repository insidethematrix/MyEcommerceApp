package com.example.myecommerceapp.util;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Pads a screen's root view so content isn't drawn under system bars in edge-to-edge mode. */
public final class InsetsHelper {

    private InsetsHelper() {
    }

    /**
     * @param padBottom false when a child (e.g. BottomNavigationView) handles the bottom inset
     */
    public static void applySystemBarPadding(View root, boolean padBottom) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            // Include the keyboard so focused inputs stay visible
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(insets.left, insets.top, insets.right, padBottom ? insets.bottom : 0);
            return windowInsets;
        });
    }
}
