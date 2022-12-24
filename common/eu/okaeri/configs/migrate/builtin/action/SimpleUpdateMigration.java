/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.migrate.builtin.action;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.view.RawConfigView;
import java.util.Objects;
import java.util.function.Function;
import lombok.NonNull;

public class SimpleUpdateMigration
implements ConfigMigration {
    private final String key;
    private final Function<Object, Object> function;

    @Override
    public boolean migrate(@NonNull OkaeriConfig config, @NonNull RawConfigView view) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (view == null) {
            throw new NullPointerException("view is marked non-null but is null");
        }
        Object oldValue = view.get(this.key);
        Object newValue = this.function.apply(oldValue);
        view.set(this.key, newValue);
        return !Objects.equals(oldValue, newValue);
    }

    public String toString() {
        return "SimpleUpdateMigration(key=" + this.key + ", function=" + this.function + ")";
    }

    public SimpleUpdateMigration(String key, Function<Object, Object> function) {
        this.key = key;
        this.function = function;
    }
}

