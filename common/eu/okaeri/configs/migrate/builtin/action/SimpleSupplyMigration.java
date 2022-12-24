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
import java.util.function.Supplier;
import lombok.NonNull;

public class SimpleSupplyMigration
implements ConfigMigration {
    private final String key;
    private final Supplier supplier;

    @Override
    public boolean migrate(@NonNull OkaeriConfig config, @NonNull RawConfigView view) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (view == null) {
            throw new NullPointerException("view is marked non-null but is null");
        }
        if (view.exists(this.key)) {
            return false;
        }
        view.set(this.key, this.supplier.get());
        return true;
    }

    public String toString() {
        return "SimpleSupplyMigration(key=" + this.key + ", supplier=" + this.supplier + ")";
    }

    public SimpleSupplyMigration(String key, Supplier supplier) {
        this.key = key;
        this.supplier = supplier;
    }
}

