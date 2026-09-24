package com.example.myecommerceapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.myecommerceapp.data.model.Product;
import com.example.myecommerceapp.data.model.ProductFactory;
import com.example.myecommerceapp.util.FirestoreQueryLiveData;
import com.example.myecommerceapp.util.Resource;
import com.example.myecommerceapp.util.ResultCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreProductRepository implements ProductRepository {
    static final String COLLECTION = "products";
    private static final String TAG = "ProductRepository";

    private final FirebaseFirestore db;
    private final LiveData<Resource<List<Product>>> products;

    public FirestoreProductRepository(FirebaseFirestore db) {
        this.db = db;
        // A single shared listener: every screen observes the same live catalog
        this.products = new FirestoreQueryLiveData<>(
                db.collection(COLLECTION).orderBy("name"),
                snapshot -> {
                    List<Product> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        try {
                            result.add(ProductFactory.fromMap(doc.getId(), doc.getData()));
                        } catch (IllegalArgumentException e) {
                            // A single malformed document must not break the whole catalog
                            Log.w(TAG, "Skipping product " + doc.getId() + ": " + e.getMessage());
                        }
                    }
                    return result;
                });
    }

    @Override
    public LiveData<Resource<List<Product>>> getProducts() {
        return products;
    }

    @Override
    public void seedProducts(Map<String, Map<String, Object>> seed, ResultCallback<Void> callback) {
        WriteBatch batch = db.batch();
        for (Map.Entry<String, Map<String, Object>> entry : seed.entrySet()) {
            batch.set(db.collection(COLLECTION).document(entry.getKey()), entry.getValue());
        }
        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
