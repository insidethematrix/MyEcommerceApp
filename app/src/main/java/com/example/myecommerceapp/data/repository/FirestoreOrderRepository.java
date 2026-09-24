package com.example.myecommerceapp.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myecommerceapp.data.model.CartItem;
import com.example.myecommerceapp.data.model.Order;
import com.example.myecommerceapp.data.model.ShoppingCart;
import com.example.myecommerceapp.util.FirestoreQueryLiveData;
import com.example.myecommerceapp.util.Resource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Orders live at users/{uid}/orders/{orderId} and are never modified after creation. */
public class FirestoreOrderRepository implements OrderRepository {
    private static final String STATUS_PLACED = "placed";

    private final FirebaseFirestore db;

    public FirestoreOrderRepository(FirebaseFirestore db) {
        this.db = db;
    }

    private CollectionReference ordersOf(String uid) {
        return db.collection("users").document(uid).collection("orders");
    }

    @Override
    public void placeOrder(String uid, ShoppingCart cart, PlaceOrderCallback callback) {
        DocumentReference orderRef = ordersOf(uid).document();
        CollectionReference cartRef = FirestoreCartRepository.cartOf(db, uid);
        List<CartItem> items = new ArrayList<>(cart.getItems());

        db.runTransaction(transaction -> {
            // Firestore requires every read in a transaction to happen before any write
            List<DocumentSnapshot> productDocs = new ArrayList<>();
            for (CartItem item : items) {
                productDocs.add(transaction.get(
                        db.collection(FirestoreProductRepository.COLLECTION)
                                .document(item.getProduct().getId())));
            }

            List<Map<String, Object>> lines = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                CartItem item = items.get(i);
                Long stock = productDocs.get(i).getLong("stock");
                if (stock == null || stock < item.getQuantity()) {
                    throw new OutOfStockException(item.getProduct().getName());
                }
                transaction.update(productDocs.get(i).getReference(),
                        "stock", stock - item.getQuantity());
                transaction.delete(cartRef.document(item.getProduct().getId()));

                Map<String, Object> line = new HashMap<>();
                line.put("productId", item.getProduct().getId());
                line.put("name", item.getProduct().getName());
                line.put("quantity", item.getQuantity());
                line.put("unitPrice", item.getProduct().getPrice().doubleValue());
                lines.add(line);
            }

            Map<String, Object> order = new HashMap<>();
            order.put("items", lines);
            order.put("subtotal", cart.getSubtotal().doubleValue());
            order.put("tax", cart.getTaxTotal().doubleValue());
            order.put("discount", cart.getDiscountTotal().doubleValue());
            order.put("total", cart.getTotal().doubleValue());
            order.put("status", STATUS_PLACED);
            order.put("createdAt", FieldValue.serverTimestamp());
            transaction.set(orderRef, order);
            return orderRef.getId();
        }).addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> {
                    OutOfStockException outOfStock = findOutOfStock(e);
                    if (outOfStock != null) {
                        callback.onOutOfStock(outOfStock.productName);
                    } else {
                        callback.onError(e.getMessage());
                    }
                });
    }

    @Override
    public LiveData<Resource<List<Order>>> observeOrders(String uid) {
        return new FirestoreQueryLiveData<>(
                ordersOf(uid).orderBy("createdAt", Query.Direction.DESCENDING),
                snapshot -> {
                    List<Order> orders = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        orders.add(toOrder(doc));
                    }
                    return orders;
                });
    }

    @SuppressWarnings("unchecked")
    private static Order toOrder(DocumentSnapshot doc) {
        List<Order.Line> lines = new ArrayList<>();
        Object rawItems = doc.get("items");
        if (rawItems instanceof List) {
            for (Object raw : (List<Object>) rawItems) {
                Map<String, Object> line = (Map<String, Object>) raw;
                lines.add(new Order.Line(
                        (String) line.get("productId"),
                        (String) line.get("name"),
                        ((Number) line.get("quantity")).intValue(),
                        BigDecimal.valueOf(((Number) line.get("unitPrice")).doubleValue())));
            }
        }
        // A freshly written order still has a pending server timestamp; estimate it locally
        Date createdAt = doc.getDate("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
        Double total = doc.getDouble("total");
        String status = doc.getString("status");
        return new Order(doc.getId(),
                createdAt != null ? createdAt : new Date(),
                lines,
                BigDecimal.valueOf(total != null ? total : 0),
                status != null ? status : STATUS_PLACED);
    }

    private static OutOfStockException findOutOfStock(Throwable e) {
        while (e != null) {
            if (e instanceof OutOfStockException) return (OutOfStockException) e;
            e = e.getCause();
        }
        return null;
    }

    /**
     * Thrown inside the transaction to abort it. It is not a FirebaseFirestoreException,
     * so Firestore does not treat it as retryable contention.
     */
    static class OutOfStockException extends RuntimeException {
        final String productName;

        OutOfStockException(String productName) {
            super("Out of stock: " + productName);
            this.productName = productName;
        }
    }
}
