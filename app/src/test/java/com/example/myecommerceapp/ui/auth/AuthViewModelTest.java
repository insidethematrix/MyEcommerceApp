package com.example.myecommerceapp.ui.auth;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.myecommerceapp.R;
import com.example.myecommerceapp.data.repository.AuthRepository;
import com.example.myecommerceapp.util.Resource;
import com.example.myecommerceapp.util.ResultCallback;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class AuthViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutor = new InstantTaskExecutorRule();

    private AuthRepository repository;
    private AuthViewModel viewModel;

    @Before
    public void setUp() {
        repository = mock(AuthRepository.class);
        viewModel = new AuthViewModel(repository);
    }

    @Test
    public void login_withEmptyFields_failsWithoutCallingFirebase() {
        viewModel.login("  ", "");

        assertErrorRes(R.string.error_empty_fields);
        verify(repository, never()).login(anyString(), anyString(), any());
    }

    @Test
    public void login_withInvalidEmail_fails() {
        viewModel.login("not-an-email", "secret123");

        assertErrorRes(R.string.error_invalid_email);
        verify(repository, never()).login(anyString(), anyString(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void login_trimsEmail_andReportsSuccess() {
        viewModel.login("  ahmet@example.com ", "secret123");

        assertEquals(Resource.Status.LOADING, viewModel.getAuthState().getValue().getStatus());
        ArgumentCaptor<ResultCallback<Void>> callback = ArgumentCaptor.forClass(ResultCallback.class);
        verify(repository).login(eq("ahmet@example.com"), eq("secret123"), callback.capture());

        callback.getValue().onSuccess(null);

        assertEquals(Resource.Status.SUCCESS, viewModel.getAuthState().getValue().getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void login_firebaseError_isShownToUser() {
        viewModel.login("ahmet@example.com", "wrong-password");
        ArgumentCaptor<ResultCallback<Void>> callback = ArgumentCaptor.forClass(ResultCallback.class);
        verify(repository).login(anyString(), anyString(), callback.capture());

        callback.getValue().onError("The password is invalid.");

        Resource<Void> state = viewModel.getAuthState().getValue();
        assertEquals(Resource.Status.ERROR, state.getStatus());
        assertEquals("The password is invalid.", state.getError().getText());
    }

    @Test
    public void register_withShortPassword_failsWithMinimumLength() {
        viewModel.register("Ahmet", "ahmet@example.com", "123");

        assertErrorRes(R.string.error_short_password);
        assertArrayEquals(new Object[]{6}, viewModel.getAuthState().getValue().getError().getArgs());
        verify(repository, never()).register(anyString(), anyString(), anyString(), any());
    }

    @Test
    public void register_withValidInput_callsRepository() {
        viewModel.register(" Ahmet ", "ahmet@example.com", "secret123");

        verify(repository).register(eq("Ahmet"), eq("ahmet@example.com"), eq("secret123"), any());
    }

    private void assertErrorRes(int expected) {
        Resource<Void> state = viewModel.getAuthState().getValue();
        assertEquals(Resource.Status.ERROR, state.getStatus());
        assertEquals(expected, state.getError().getRes());
    }
}
