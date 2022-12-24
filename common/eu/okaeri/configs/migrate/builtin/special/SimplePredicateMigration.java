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
import java.util.function.Predicate;
import lombok.NonNull;

public class SimplePredicateMigration<T>
implements ConfigMigration {
    private final String key;
    private final Predicate<T> predicate;

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
        Object value = view.get(this.key);
        return this.predicate.test(value);
    }

    public String toString() {
        return "SimplePredicateMigration(key=" + this.key + ", predicate=" + this.predicate + ")";
    }

    public SimplePredicateMigration(String key, Predicate<T> predicate) {
        this.key = key;
        this.predicate = predicate;
    }
}

