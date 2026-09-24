package com.example.myecommerceapp.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myecommerceapp.R;
import com.example.myecommerceapp.data.repository.AuthRepository;
import com.example.myecommerceapp.util.Resource;
import com.example.myecommerceapp.util.ResultCallback;
import com.example.myecommerceapp.util.Validators;

/** Shared by the login and register screens. */
public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Resource<Void>> authState = new MutableLiveData<>();

    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }

    /** Null until the user submits; then LOADING followed by SUCCESS or ERROR. */
    public LiveData<Resource<Void>> getAuthState() {
        return authState;
    }

    public void login(String email, String password) {
        email = email.trim();
        if (email.isEmpty() || password.isEmpty()) {
            authState.setValue(Resource.error(R.string.error_empty_fields));
            return;
        }
        if (!Validators.isValidEmail(email)) {
            authState.setValue(Resource.error(R.string.error_invalid_email));
            return;
        }
        authState.setValue(Resource.loading());
        authRepository.login(email, password, callback());
    }

    public void register(String name, String email, String password) {
        name = name.trim();
        email = email.trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            authState.setValue(Resource.error(R.string.error_empty_fields));
            return;
        }
        if (!Validators.isValidEmail(email)) {
            authState.setValue(Resource.error(R.string.error_invalid_email));
            return;
        }
        if (!Validators.isValidPassword(password)) {
            authState.setValue(Resource.error(R.string.error_short_password, Validators.MIN_PASSWORD_LENGTH));
            return;
        }
        authState.setValue(Resource.loading());
        authRepository.register(name, email, password, callback());
    }

    private ResultCallback<Void> callback() {
        return new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                authState.setValue(Resource.success(null));
            }

            @Override
            public void onError(String message) {
                authState.setValue(Resource.error(message));
            }
        };
    }
}
