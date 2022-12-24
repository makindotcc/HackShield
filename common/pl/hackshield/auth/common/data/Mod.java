/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Mod
implements Serializable {
    private String name;
    private boolean enabled;
    private Map<String, Object> settings;

    public Mod(String name) {
        this(name, true);
    }

    public Mod(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
        this.settings = new HashMap<String, Object>();
    }

    public String toString() {
        return this.name;
    }

    public Mod(String name, boolean enabled, Map<String, Object> settings) {
        this.name = name;
        this.enabled = enabled;
        this.settings = settings;
    }

    public Mod() {
    }

    public String getName() {
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Map<String, Object> getSettings() {
        return this.settings;
    }
}

