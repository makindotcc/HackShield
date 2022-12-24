/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.configurer;

import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.configurer.WrappedConfigurer;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.SerdesContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

public class InMemoryWrappedConfigurer
extends WrappedConfigurer {
    private final Map<String, Object> map;

    public InMemoryWrappedConfigurer(@NonNull Configurer configurer, @NonNull Map<String, Object> map) {
        super(configurer);
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        if (map == null) {
            throw new NullPointerException("map is marked non-null but is null");
        }
        this.map = map;
    }

    @Override
    public List<String> getAllKeys() {
        return Collections.unmodifiableList(new ArrayList<String>(this.map.keySet()));
    }

    @Override
    public boolean keyExists(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.map.containsKey(key);
    }

    @Override
    public Object getValue(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.map.get(key);
    }

    @Override
    public <T> T getValue(@NonNull String key, @NonNull Class<T> clazz, GenericsDeclaration genericType, @NonNull SerdesContext serdesContext) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        Object value = this.getValue(key);
        if (value == null) {
            return null;
        }
        return this.resolveType(value, GenericsDeclaration.of(value), clazz, genericType, serdesContext);
    }

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
}

