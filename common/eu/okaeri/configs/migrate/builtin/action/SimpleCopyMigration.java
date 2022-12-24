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
import lombok.NonNull;

public class SimpleCopyMigration
implements ConfigMigration {
    private final String fromKey;
    private final String toKey;

    @Override
    public boolean migrate(@NonNull OkaeriConfig config, @NonNull RawConfigView view) {
        Object oldValue;
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (view == null) {
            throw new NullPointerException("view is marked non-null but is null");
        }
        if (!view.exists(this.fromKey)) {
            return false;
        }
        Object targetValue = view.get(this.fromKey);
        return !Objects.equals(targetValue, oldValue = view.set(this.toKey, targetValue));
    }

    public String toString() {
        return "SimpleCopyMigration(fromKey=" + this.fromKey + ", toKey=" + this.toKey + ")";
    }

    public SimpleCopyMigration(String fromKey, String toKey) {
        this.fromKey = fromKey;
        this.toKey = toKey;
    }
}

