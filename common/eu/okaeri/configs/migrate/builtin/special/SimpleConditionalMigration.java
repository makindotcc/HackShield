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

public class SimpleConditionalMigration
implements ConfigMigration {
    private final ConfigMigration when;
    private final ConfigMigration migrationTrue;
    private final ConfigMigration migrationFalse;

    @Override
    public boolean migrate(@NonNull OkaeriConfig config, @NonNull RawConfigView view) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (view == null) {
            throw new NullPointerException("view is marked non-null but is null");
        }
        if (this.when.migrate(config, view)) {
            return this.migrationTrue.migrate(config, view);
        }
        return this.migrationFalse.migrate(config, view);
    }

    public String toString() {
        return "SimpleConditionalMigration(when=" + this.when + ", migrationTrue=" + this.migrationTrue + ", migrationFalse=" + this.migrationFalse + ")";
    }

    public SimpleConditionalMigration(ConfigMigration when, ConfigMigration migrationTrue, ConfigMigration migrationFalse) {
        this.when = when;
        this.migrationTrue = migrationTrue;
        this.migrationFalse = migrationFalse;
    }
}

