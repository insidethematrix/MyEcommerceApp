package com.example.myecommerceapp;

import android.app.Application;
import android.content.Context;

public class AvoraApp extends Application {
    private AppContainer container;

    @Override
    public void onCreate() {
        super.onCreate();
        container = new AppContainer();
    }

    public static AppContainer container(Context context) {
        return ((AvoraApp) context.getApplicationContext()).container;
    }
}
