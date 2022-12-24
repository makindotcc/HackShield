/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {
    private Map<K, V> map = new HashMap();

    public MapBuilder add(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return this.map;
    }
}

