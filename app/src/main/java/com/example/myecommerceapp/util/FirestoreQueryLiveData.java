package com.example.myecommerceapp.util;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Exposes a Firestore query as LiveData. The realtime listener is attached only while
 * the LiveData has active observers, so stopped screens get no updates and no listener
 * leaks after a screen is destroyed.
 */
public class FirestoreQueryLiveData<T> extends LiveData<Resource<T>> {

    public interface Mapper<T> {
        T map(QuerySnapshot snapshot);
    }

    private final Query query;
    private final Mapper<T> mapper;
    private ListenerRegistration registration;

    public FirestoreQueryLiveData(Query query, Mapper<T> mapper) {
        this.query = query;
        this.mapper = mapper;
        setValue(Resource.loading());
    }

    @Override
    protected void onActive() {
        registration = query.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                setValue(Resource.error(error.getMessage()));
            } else if (snapshot != null) {
                setValue(Resource.success(mapper.map(snapshot)));
            }
        });
    }

    @Override
    protected void onInactive() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }
}
