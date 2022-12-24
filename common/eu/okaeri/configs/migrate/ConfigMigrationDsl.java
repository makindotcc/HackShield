/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.migrate;

import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.builtin.action.SimpleCopyMigration;
import eu.okaeri.configs.migrate.builtin.action.SimpleDeleteMigration;
import eu.okaeri.configs.migrate.builtin.action.SimpleMoveMigration;
import eu.okaeri.configs.migrate.builtin.action.SimpleSupplyMigration;
import eu.okaeri.configs.migrate.builtin.action.SimpleUpdateMigration;
import eu.okaeri.configs.migrate.builtin.special.SimpleConditionalMigration;
import eu.okaeri.configs.migrate.builtin.special.SimpleExistsMigration;
import eu.okaeri.configs.migrate.builtin.special.SimpleMultiMigration;
import eu.okaeri.configs.migrate.builtin.special.SimpleNoopMigration;
import eu.okaeri.configs.migrate.builtin.special.SimpleNotMigration;
import eu.okaeri.configs.migrate.builtin.special.SimplePredicateMigration;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.NonNull;

public interface ConfigMigrationDsl {
    public static ConfigMigration copy(@NonNull String fromKey, @NonNull String toKey) {
        if (fromKey == null) {
            throw new NullPointerException("fromKey is marked non-null but is null");
        }
        if (toKey == null) {
            throw new NullPointerException("toKey is marked non-null but is null");
        }
        return new SimpleCopyMigration(fromKey, toKey);
    }

    public static ConfigMigration delete(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return new SimpleDeleteMigration(key);
    }

    public static ConfigMigration move(@NonNull String fromKey, @NonNull String toKey) {
        if (fromKey == null) {
            throw new NullPointerException("fromKey is marked non-null but is null");
        }
        if (toKey == null) {
            throw new NullPointerException("toKey is marked non-null but is null");
        }
        return new SimpleMoveMigration(fromKey, toKey);
    }

    public static ConfigMigration supply(@NonNull String key, @NonNull Supplier supplier) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (supplier == null) {
            throw new NullPointerException("supplier is marked non-null but is null");
        }
        return new SimpleSupplyMigration(key, supplier);
    }

    public static ConfigMigration update(@NonNull String key, @NonNull Function<Object, Object> function) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (function == null) {
            throw new NullPointerException("function is marked non-null but is null");
        }
        return new SimpleUpdateMigration(key, function);
    }

    public static ConfigMigration when(@NonNull ConfigMigration when, @NonNull ConfigMigration migrationTrue, @NonNull ConfigMigration migrationFalse) {
        if (when == null) {
            throw new NullPointerException("when is marked non-null but is null");
        }
        if (migrationTrue == null) {
            throw new NullPointerException("migrationTrue is marked non-null but is null");
        }
        if (migrationFalse == null) {
            throw new NullPointerException("migrationFalse is marked non-null but is null");
        }
        return new SimpleConditionalMigration(when, migrationTrue, migrationFalse);
    }

    public static ConfigMigration when(@NonNull ConfigMigration when, @NonNull ConfigMigration migrationTrue) {
        if (when == null) {
            throw new NullPointerException("when is marked non-null but is null");
        }
        if (migrationTrue == null) {
            throw new NullPointerException("migrationTrue is marked non-null but is null");
        }
        return new SimpleConditionalMigration(when, migrationTrue, ConfigMigrationDsl.noop(false));
    }

    public static ConfigMigration exists(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return new SimpleExistsMigration(key);
    }

    public static ConfigMigration multi(ConfigMigration ... migrations) {
        if (migrations == null) {
            throw new NullPointerException("migrations is marked non-null but is null");
        }
        return new SimpleMultiMigration(migrations);
    }

    public static ConfigMigration any(ConfigMigration ... migrations) {
        if (migrations == null) {
            throw new NullPointerException("migrations is marked non-null but is null");
        }
        return new SimpleMultiMigration(migrations);
    }

    public static ConfigMigration all(ConfigMigration ... migrations) {
        if (migrations == null) {
            throw new NullPointerException("migrations is marked non-null but is null");
        }
        return new SimpleMultiMigration(migrations, true);
    }

    public static ConfigMigration noop(boolean result) {
        return new SimpleNoopMigration(result);
    }

    public static ConfigMigration not(@NonNull ConfigMigration migration) {
        if (migration == null) {
            throw new NullPointerException("migration is marked non-null but is null");
        }
        return new SimpleNotMigration(migration);
    }

    public static <T> ConfigMigration match(@NonNull String key, @NonNull Predicate<T> predicate) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (predicate == null) {
            throw new NullPointerException("predicate is marked non-null but is null");
        }
        return new SimplePredicateMigration<T>(key, predicate);
    }
}

