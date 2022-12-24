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
import java.util.Arrays;
import lombok.NonNull;

public class SimpleMultiMigration
implements ConfigMigration {
    private final ConfigMigration[] migrations;
    private boolean requireAll = false;

    @Override
    public boolean migrate(@NonNull OkaeriConfig config, @NonNull RawConfigView view) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (view == null) {
            throw new NullPointerException("view is marked non-null but is null");
        }
        long performed = Arrays.stream(this.migrations).filter(migration -> migration.migrate(config, view)).count();
        return this.requireAll ? performed == (long)this.migrations.length : performed > 0L;
    }

    public String toString() {
        return "SimpleMultiMigration(migrations=" + Arrays.deepToString(this.migrations) + ", requireAll=" + this.requireAll + ")";
    }

    public SimpleMultiMigration(ConfigMigration[] migrations, boolean requireAll) {
        this.migrations = migrations;
        this.requireAll = requireAll;
    }

    public SimpleMultiMigration(ConfigMigration[] migrations) {
        this.migrations = migrations;
    }
}

