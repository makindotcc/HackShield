/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.migrate.view;

import eu.okaeri.configs.OkaeriConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NonNull;

public class RawConfigView {
    private final OkaeriConfig config;
    private String nestedSeparator = "\\.";

    public boolean exists(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        Map<String, Object> document = this.config.asMap(this.config.getConfigurer(), true);
        return this.valueExists(document, key);
    }

    public Object get(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        Map<String, Object> document = this.config.asMap(this.config.getConfigurer(), true);
        return this.valueExtract(document, key);
    }

    public Object set(@NonNull String key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        Map<String, Object> document = this.config.asMap(this.config.getConfigurer(), true);
        Object old = this.valuePut(document, key, value);
        this.config.load(document);
        return old;
    }

    public Object remove(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        Map<String, Object> document = this.config.asMap(this.config.getConfigurer(), true);
        Object old = this.valueRemove(document, key);
        if (key.split(this.nestedSeparator).length == 1) {
            this.config.getConfigurer().remove(key);
        }
        this.config.load(document);
        return old;
    }

    protected boolean valueExists(Map document, String path) {
        String[] split = path.split(this.nestedSeparator);
        for (int i = 0; i < split.length; ++i) {
            String part = split[i];
            if (i == split.length - 1) {
                return document.containsKey(part);
            }
            Object element = document.get(part);
            if (!(element instanceof Map)) {
                return false;
            }
            document = (Map)element;
        }
        return false;
    }

    protected Object valueExtract(Map document, String path) {
        String[] split = path.split(this.nestedSeparator);
        for (int i = 0; i < split.length; ++i) {
            String part = split[i];
            Object element = document.get(part);
            if (i == split.length - 1) {
                return element;
            }
            if (!(element instanceof Map)) {
                String elementStr = element == null ? "null" : element.getClass().getSimpleName();
                throw new IllegalArgumentException("Cannot extract '" + path + "': not deep enough (ended at index " + i + " [" + part + ":" + elementStr + "])");
            }
            document = (Map)element;
        }
        return null;
    }

    protected Object valuePut(Map document, String path, Object value) {
        String[] split = path.split(this.nestedSeparator);
        for (int i = 0; i < split.length; ++i) {
            String part = split[i];
            if (i == split.length - 1) {
                return document.put(part, value);
            }
            Object element = document.get(part);
            if (element instanceof Map) {
                document = (Map)element;
                continue;
            }
            if (element != null) {
                String elementStr = element.getClass().getSimpleName();
                throw new IllegalArgumentException("Cannot insert '" + path + "': type conflict (ended at index " + i + " [" + part + ":" + elementStr + "])");
            }
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            document.put(part, map);
            document = map;
        }
        throw new IllegalArgumentException("Cannot put '" + path + "'");
    }

    protected Object valueRemove(Map document, String path) {
        String[] split = path.split(this.nestedSeparator);
        for (int i = 0; i < split.length; ++i) {
            String part = split[i];
            if (i == split.length - 1) {
                return document.remove(part);
            }
            Object element = document.get(part);
            if (!(element instanceof Map)) {
                return null;
            }
            document = (Map)element;
        }
        return null;
    }

    public RawConfigView(OkaeriConfig config, String nestedSeparator) {
        this.config = config;
        this.nestedSeparator = nestedSeparator;
    }

    public RawConfigView(OkaeriConfig config) {
        this.config = config;
    }
}

