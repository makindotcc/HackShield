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
import java.util.List;
import java.util.Map;
import lombok.NonNull;

public class DeserializationData {
    @NonNull
    private Map<String, Object> data;
    @NonNull
    private Configurer configurer;
    @NonNull
    private SerdesContext context;

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(this.data);
    }

    public boolean containsKey(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.data.containsKey(key);
    }

    public Object getRaw(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.data.get(key);
    }

    public <T> T getDirect(@NonNull String key, @NonNull GenericsDeclaration genericType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (genericType == null) {
            throw new NullPointerException("genericType is marked non-null but is null");
        }
        Object object = this.data.get(key);
        return (T)this.configurer.resolveType(object, null, genericType.getType(), genericType, SerdesContext.of(this.configurer));
    }

    public <T> T get(@NonNull String key, @NonNull Class<T> valueType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (valueType == null) {
            throw new NullPointerException("valueType is marked non-null but is null");
        }
        Object object = this.data.get(key);
        return this.configurer.resolveType(object, null, valueType, null, SerdesContext.of(this.configurer));
    }

    public <T> Collection<T> getAsCollection(@NonNull String key, @NonNull GenericsDeclaration genericType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (genericType == null) {
            throw new NullPointerException("genericType is marked non-null but is null");
        }
        if (!Collection.class.isAssignableFrom(genericType.getType())) {
            throw new IllegalArgumentException("genericType.type must be a superclass of Collection: " + genericType);
        }
        return (Collection)this.getDirect(key, genericType);
    }

    public <T> List<T> getAsList(@NonNull String key, @NonNull Class<T> listValueType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (listValueType == null) {
            throw new NullPointerException("listValueType is marked non-null but is null");
        }
        GenericsDeclaration genericType = GenericsDeclaration.of(List.class, Collections.singletonList(listValueType));
        return (List)this.getAsCollection(key, genericType);
    }

    public <K, V> Map<K, V> getAsMap(@NonNull String key, @NonNull Class<K> mapKeyType, @NonNull Class<V> mapValueType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (mapKeyType == null) {
            throw new NullPointerException("mapKeyType is marked non-null but is null");
        }
        if (mapValueType == null) {
            throw new NullPointerException("mapValueType is marked non-null but is null");
        }
        GenericsDeclaration genericType = GenericsDeclaration.of(Map.class, Arrays.asList(mapKeyType, mapValueType));
        return (Map)this.getDirect(key, genericType);
    }

    public <K, V> Map<K, V> getAsMap(@NonNull String key, @NonNull GenericsDeclaration genericType) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (genericType == null) {
            throw new NullPointerException("genericType is marked non-null but is null");
        }
        if (!Map.class.isAssignableFrom(genericType.getType())) {
            throw new IllegalArgumentException("genericType.type must be a superclass of Map: " + genericType);
        }
        return (Map)this.getDirect(key, genericType);
    }

    public DeserializationData(@NonNull Map<String, Object> data, @NonNull Configurer configurer, @NonNull SerdesContext context) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        if (context == null) {
            throw new NullPointerException("context is marked non-null but is null");
        }
        this.data = data;
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

