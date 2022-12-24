/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.serdes;

import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.SerdesContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NonNull;

public class SerializationData {
    @NonNull
    private final Configurer configurer;
    @NonNull
    private final SerdesContext context;
    private Map<String, Object> data = new LinkedHashMap<String, Object>();

    public void clear() {
        this.data.clear();
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(this.data);
    }

    public void addRaw(@NonNull String key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        this.data.put(key, value);
    }

    public void add(@NonNull String key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        value = this.configurer.simplify(value, null, SerdesContext.of(this.configurer), true);
        this.data.put(key, value);
    }

    public void add(@NonNull String key, Object value, @NonNull GenericsDeclaration genericType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (genericType == null) {
            throw new NullPointerException("genericType is marked non-null but is null");
        }
        value = this.configurer.simplify(value, genericType, SerdesContext.of(this.configurer), true);
        this.data.put(key, value);
    }

    public <T> void add(@NonNull String key, Object value, @NonNull Class<T> valueType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (valueType == null) {
            throw new NullPointerException("valueType is marked non-null but is null");
        }
        GenericsDeclaration genericType = GenericsDeclaration.of(valueType);
        this.add(key, value, genericType);
    }

    public void addCollection(@NonNull String key, Collection<?> collection, @NonNull GenericsDeclaration genericType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (genericType == null) {
            throw new NullPointerException("genericType is marked non-null but is null");
        }
        Object object = this.configurer.simplifyCollection(collection, genericType, SerdesContext.of(this.configurer), true);
        this.data.put(key, object);
    }

    public <T> void addCollection(@NonNull String key, Collection<?> collection, @NonNull Class<T> collectionValueType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (collectionValueType == null) {
            throw new NullPointerException("collectionValueType is marked non-null but is null");
        }
        GenericsDeclaration genericType = GenericsDeclaration.of(collection, Collections.singletonList(collectionValueType));
        this.addCollection(key, collection, genericType);
    }

    public <T> void addArray(@NonNull String key, T[] array, @NonNull Class<T> arrayValueType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (arrayValueType == null) {
            throw new NullPointerException("arrayValueType is marked non-null but is null");
        }
        this.addCollection(key, array == null ? null : Arrays.asList(array), arrayValueType);
    }

    public void addAsMap(@NonNull String key, Map<?, ?> map, @NonNull GenericsDeclaration genericType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (genericType == null) {
            throw new NullPointerException("genericType is marked non-null but is null");
        }
        Object object = this.configurer.simplifyMap(map, genericType, SerdesContext.of(this.configurer), true);
        this.data.put(key, object);
    }

    public <K, V> void addAsMap(@NonNull String key, Map<K, V> map, @NonNull Class<K> mapKeyType, @NonNull Class<V> mapValueType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (mapKeyType == null) {
            throw new NullPointerException("mapKeyType is marked non-null but is null");
        }
        if (mapValueType == null) {
            throw new NullPointerException("mapValueType is marked non-null but is null");
        }
        GenericsDeclaration genericType = GenericsDeclaration.of(map, Arrays.asList(mapKeyType, mapValueType));
        this.addAsMap(key, map, genericType);
    }

    public void addFormatted(@NonNull String key, @NonNull String format, Object value) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (format == null) {
            throw new NullPointerException("format is marked non-null but is null");
        }
        if (value == null) {
            this.data.put(key, null);
            return;
        }
        this.add(key, String.format(format, value));
    }

    public SerializationData(@NonNull Configurer configurer, @NonNull SerdesContext context) {
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        if (context == null) {
            throw new NullPointerException("context is marked non-null but is null");
        }
        this.configurer = configurer;
        this.context = context;
    }

    @NonNull
    public Configurer getConfigurer() {
        return this.configurer;
    }

    @NonNull
    public SerdesContext getContext() {
        return this.context;
    }
}

