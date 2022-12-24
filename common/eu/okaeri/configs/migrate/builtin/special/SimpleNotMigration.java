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

public class SimpleNotMigration
implements ConfigMigration {
    private final ConfigMigration migration;

    @Override
    public boolean migrate(@NonNull OkaeriConfig config, @NonNull RawConfigView view) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (view == null) {
            throw new NullPointerException("view is marked non-null but is null");
        }
        return !this.migration.migrate(config, view);
    }

    public String toString() {
        return "SimpleNotMigration(migration=" + this.migration + ")";
    }

    public SimpleNotMigration(ConfigMigration migration) {
        this.migration = migration;
    }
}

