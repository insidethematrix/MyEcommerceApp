package com.example.myecommerceapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.myecommerceapp.data.model.ShoppingCart;
import com.example.myecommerceapp.databinding.ActivityMainBinding;
import com.example.myecommerceapp.ui.auth.LoginActivity;
import com.example.myecommerceapp.ui.cart.CartFragment;
import com.example.myecommerceapp.ui.cart.CartViewModel;
import com.example.myecommerceapp.ui.home.HomeFragment;
import com.example.myecommerceapp.ui.profile.ProfileFragment;
import com.example.myecommerceapp.util.InsetsHelper;
import com.example.myecommerceapp.util.Resource;
import com.google.android.material.badge.BadgeDrawable;

/**
 * Hosts the three tabs. Fragments are shown/hidden rather than replaced so each tab keeps
 * its scroll position and search state when switching.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG_HOME = "home";
    private static final String TAG_CART = "cart";
    private static final String TAG_PROFILE = "profile";

    private ActivityMainBinding binding;
    private OnBackPressedCallback backToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AvoraApp.container(this).authRepository.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsHelper.applySystemBarPadding(binding.getRoot(), false);

        // Back from Cart or Profile returns to Shop before leaving the app
        backToHome = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                selectTab(R.id.nav_home);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backToHome);

        // After a rotation the FragmentManager restores which tab is shown/hidden by itself
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new HomeFragment(), TAG_HOME)
                    .add(R.id.fragmentContainer, new CartFragment(), TAG_CART)
                    .add(R.id.fragmentContainer, new ProfileFragment(), TAG_PROFILE)
                    .commitNow();
            showTab(R.id.nav_home);
        }

        binding.bottomNav.setOnItemSelectedListener(item -> {
            showTab(item.getItemId());
            return true;
        });

        CartViewModel cartViewModel =
                new ViewModelProvider(this, new ViewModelFactory(this)).get(CartViewModel.class);
        cartViewModel.getCart().observe(this, this::updateCartBadge);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // BottomNavigationView restores its selection here, after onCreate
        backToHome.setEnabled(binding.bottomNav.getSelectedItemId() != R.id.nav_home);
    }

    /** Switches tabs programmatically, e.g. "Start shopping" from the empty cart. */
    public void selectTab(int itemId) {
        binding.bottomNav.setSelectedItemId(itemId);
    }

    private void showTab(int itemId) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        String selected = tagFor(itemId);
        for (String tag : new String[]{TAG_HOME, TAG_CART, TAG_PROFILE}) {
            Fragment fragment = fm.findFragmentByTag(tag);
            if (fragment == null) continue;
            if (tag.equals(selected)) {
                tx.show(fragment);
            } else {
                tx.hide(fragment);
            }
        }
        tx.commit();
        backToHome.setEnabled(itemId != R.id.nav_home);
    }

    private static String tagFor(int itemId) {
        if (itemId == R.id.nav_cart) return TAG_CART;
        if (itemId == R.id.nav_profile) return TAG_PROFILE;
        return TAG_HOME;
    }

    private void updateCartBadge(Resource<ShoppingCart> cart) {
        if (cart.getData() == null) return;
        int count = cart.getData().getItemCount();
        BadgeDrawable badge = binding.bottomNav.getOrCreateBadge(R.id.nav_cart);
        badge.setVisible(count > 0);
        badge.setNumber(count);
    }
}
