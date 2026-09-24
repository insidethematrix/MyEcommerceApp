package com.example.myecommerceapp.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myecommerceapp.util.FirestoreQueryLiveData;
import com.example.myecommerceapp.util.Resource;
import com.example.myecommerceapp.util.ResultCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** Cart lines live at users/{uid}/cart/{productId} = { quantity, addedAt }. */
public class FirestoreCartRepository implements CartRepository {
    private static final String FIELD_QUANTITY = "quantity";
    private static final String FIELD_ADDED_AT = "addedAt";

    private final FirebaseFirestore db;

    public FirestoreCartRepository(FirebaseFirestore db) {
        this.db = db;
    }

    static CollectionReference cartOf(FirebaseFirestore db, String uid) {
        return db.collection("users").document(uid).collection("cart");
    }

    @Override
    public LiveData<Resource<Map<String, Integer>>> observeCart(String uid) {
        return new FirestoreQueryLiveData<>(
                cartOf(db, uid).orderBy(FIELD_ADDED_AT),
                snapshot -> {
                    Map<String, Integer> quantities = new LinkedHashMap<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Long quantity = doc.getLong(FIELD_QUANTITY);
                        if (quantity != null && quantity > 0) {
                            quantities.put(doc.getId(), quantity.intValue());
                        }
                    }
                    return quantities;
                });
    }

    @Override
    public void addToCart(String uid, String productId, ResultCallback<Void> callback) {
        DocumentReference ref = cartOf(db, uid).document(productId);
        // Transaction so the first add sets addedAt while later adds only bump the quantity
        db.runTransaction(transaction -> {
            DocumentSnapshot current = transaction.get(ref);
            if (current.exists()) {
                transaction.update(ref, FIELD_QUANTITY, FieldValue.increment(1));
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put(FIELD_QUANTITY, 1);
                data.put(FIELD_ADDED_AT, FieldValue.serverTimestamp());
                transaction.set(ref, data);
            }
            return null;
        }).addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void setQuantity(String uid, String productId, int quantity) {
        if (quantity <= 0) {
            cartOf(db, uid).document(productId).delete();
        } else {
            cartOf(db, uid).document(productId).update(FIELD_QUANTITY, quantity);
        }
    }

    @Override
    public void clearCart(String uid) {
        cartOf(db, uid).get().addOnSuccessListener(snapshot -> {
            WriteBatch batch = db.batch();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                batch.delete(doc.getReference());
            }
            batch.commit();
        });
    }
}
