package com.example.myecommerceapp.data;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Reads the bundled sample catalog (assets/sample_products.json) used to seed Firestore. */
public final class SampleDataLoader {
    private static final String FILE = "sample_products.json";

    private SampleDataLoader() {
    }

    public static Map<String, Map<String, Object>> load(Context context) throws IOException, JSONException {
        JSONObject root = new JSONObject(readAsset(context));
        Map<String, Map<String, Object>> products = new LinkedHashMap<>();
        for (Iterator<String> ids = root.keys(); ids.hasNext(); ) {
            String id = ids.next();
            JSONObject json = root.getJSONObject(id);
            Map<String, Object> fields = new HashMap<>();
            for (Iterator<String> keys = json.keys(); keys.hasNext(); ) {
                String key = keys.next();
                fields.put(key, json.get(key));
            }
            products.put(id, fields);
        }
        return products;
    }

    private static String readAsset(Context context) throws IOException {
        try (InputStream in = context.getAssets().open(FILE)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toString(StandardCharsets.UTF_8.name());
        }
    }
}
