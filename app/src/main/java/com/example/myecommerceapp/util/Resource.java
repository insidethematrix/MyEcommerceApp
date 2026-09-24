package com.example.myecommerceapp.util;

/** Wraps a value with its loading state so the UI can render loading / success / error. */
public final class Resource<T> {

    public enum Status { LOADING, SUCCESS, ERROR }

    private final Status status;
    private final T data;
    private final UiMessage error;

    private Resource(Status status, T data, UiMessage error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    /** Error with a raw message, typically from Firebase. */
    public static <T> Resource<T> error(String message) {
        return new Resource<>(Status.ERROR, null, UiMessage.text(message));
    }

    /** Error with a localized string resource. */
    public static <T> Resource<T> error(int messageRes, Object... args) {
        return new Resource<>(Status.ERROR, null, UiMessage.of(messageRes, args));
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    /** Non-null only when the status is ERROR. */
    public UiMessage getError() {
        return error;
    }
}
