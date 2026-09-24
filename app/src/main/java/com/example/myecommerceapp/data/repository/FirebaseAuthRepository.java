package com.example.myecommerceapp.data.repository;

import com.example.myecommerceapp.util.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirebaseAuthRepository implements AuthRepository {
    private final FirebaseAuth auth;

    public FirebaseAuthRepository(FirebaseAuth auth) {
        this.auth = auth;
    }

    @Override
    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    @Override
    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    @Override
    public String getCurrentUserEmail() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    @Override
    public String getCurrentUserName() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getDisplayName() : null;
    }

    @Override
    public void login(String email, String password, ResultCallback<Void> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void register(String name, String email, String password, ResultCallback<Void> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .onSuccessTask(result -> result.getUser().updateProfile(
                        new UserProfileChangeRequest.Builder().setDisplayName(name).build()))
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void logout() {
        auth.signOut();
    }
}
