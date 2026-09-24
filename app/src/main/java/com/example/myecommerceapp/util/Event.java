package com.example.myecommerceapp.util;

/**
 * One-shot LiveData payload (toasts, navigation) that must not be re-delivered
 * when the observer re-subscribes, e.g. after a screen rotation.
 */
public class Event<T> {
    private final T content;
    private boolean handled;

    public Event(T content) {
        this.content = content;
    }

    /** Returns the content once, then null on subsequent calls. */
    public T getContentIfNotHandled() {
        if (handled) return null;
        handled = true;
        return content;
    }

    public T peekContent() {
        return content;
    }
}
