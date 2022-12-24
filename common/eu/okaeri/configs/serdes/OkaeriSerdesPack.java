/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.serdes;

import eu.okaeri.configs.serdes.SerdesRegistry;
import lombok.NonNull;

public interface OkaeriSerdesPack {
    public void register(@NonNull SerdesRegistry var1);
}

