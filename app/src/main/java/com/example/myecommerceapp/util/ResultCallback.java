package com.example.myecommerceapp.util;

/** Single-shot async result used by repositories for one-off operations. */
public interface ResultCallback<T> {
    void onSuccess(T result);

    void onError(String message);
}
