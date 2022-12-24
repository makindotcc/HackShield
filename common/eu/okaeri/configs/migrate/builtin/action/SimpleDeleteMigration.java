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
import lombok.NonNull;

public class SimpleDeleteMigration
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
        if (!view.exists(this.key)) {
            return false;
        }
        view.remove(this.key);
        return true;
    }

    public String toString() {
        return "SimpleDeleteMigration(key=" + this.key + ")";
    }

    public SimpleDeleteMigration(String key) {
        this.key = key;
    }
}

