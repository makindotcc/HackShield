/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.migrate.builtin;

import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.builtin.special.SimpleMultiMigration;

public class NamedMigration
extends SimpleMultiMigration {
    private final String description;

    public NamedMigration(String description, ConfigMigration ... migrations) {
        super(migrations);
        this.description = description;
    }

    @Override
    public String toString() {
        return "NamedMigration(description=" + this.getDescription() + ")";
    }

    public String getDescription() {
        return this.description;
    }
}

