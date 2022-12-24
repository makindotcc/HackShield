/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.configurer;

import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

public class InMemoryConfigurer
extends Configurer {
    private Map<String, Object> map = new LinkedHashMap<String, Object>();

    @Override
    public void setValue(@NonNull String key, Object value, GenericsDeclaration type, FieldDeclaration field) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        this.map.put(key, value);
    }

    @Override
    public void setValueUnsafe(@NonNull String key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        this.map.put(key, value);
    }

    @Override
    public Object getValue(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.map.get(key);
    }

    @Override
    public Object remove(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.map.remove(key);
    }

    @Override
    public boolean keyExists(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.map.containsKey(key);
    }

    @Override
    public List<String> getAllKeys() {
        return Collections.unmodifiableList(new ArrayList<String>(this.map.keySet()));
    }

    @Override
    public void load(@NonNull InputStream inputStream, @NonNull ConfigDeclaration declaration) throws Exception {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is marked non-null but is null");
        }
        if (declaration == null) {
            throw new NullPointerException("declaration is marked non-null but is null");
        }
    }

    @Override
    public void write(@NonNull OutputStream outputStream, @NonNull ConfigDeclaration declaration) throws Exception {
        if (outputStream == null) {
            throw new NullPointerException("outputStream is marked non-null but is null");
        }
        if (declaration == null) {
            throw new NullPointerException("declaration is marked non-null but is null");
        }
    }
}

