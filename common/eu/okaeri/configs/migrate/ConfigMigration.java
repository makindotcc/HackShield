/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.migrate;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.migrate.view.RawConfigView;
import lombok.NonNull;

@FunctionalInterface
public interface ConfigMigration {
    public boolean migrate(@NonNull OkaeriConfig var1, @NonNull RawConfigView var2);
}

