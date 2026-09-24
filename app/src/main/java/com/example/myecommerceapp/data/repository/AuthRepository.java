package com.example.myecommerceapp.data.repository;

import com.example.myecommerceapp.util.ResultCallback;

public interface AuthRepository {
    boolean isLoggedIn();

    /** Firebase uid of the signed-in user, or null. */
    String getCurrentUserId();

    String getCurrentUserEmail();

    String getCurrentUserName();

    void login(String email, String password, ResultCallback<Void> callback);

    void register(String name, String email, String password, ResultCallback<Void> callback);

    void logout();
}
