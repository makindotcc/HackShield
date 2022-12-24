/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.migrate.builtin.special;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.view.RawConfigView;
import lombok.NonNull;

public class SimpleExistsMigration
implements ConfigMigration {
    private final String key;

    @Override
    public boolean migrate(@NonNull OkaeriConfig config, @NonNull RawConfigView view) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (view == null) {
            throw new NullPointerException("view is marked non-null but is null");
        }
        return view.exists(this.key);
    }

    public String toString() {
        return "SimpleExistsMigration(key=" + this.key + ")";
    }

    public SimpleExistsMigration(String key) {
        this.key = key;
    }
}

