/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.nbt.api;

import java.util.Objects;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.util.Codec;
import org.jetbrains.annotations.NotNull;

final class BinaryTagHolderImpl
implements BinaryTagHolder {
    private final String string;

    BinaryTagHolderImpl(String string) {
        this.string = Objects.requireNonNull(string, "string");
    }

    @Override
    @NotNull
    public String string() {
        return this.string;
    }

    @Override
    @NotNull
    public <T, DX extends Exception> T get(@NotNull Codec<T, String, DX, ?> codec) throws DX {
        return codec.decode(this.string);
    }

    public int hashCode() {
        return 31 * this.string.hashCode();
    }

    public boolean equals(Object that) {
        if (!(that instanceof BinaryTagHolderImpl)) {
            return false;
        }
        return this.string.equals(((BinaryTagHolderImpl)that).string);
    }

    public String toString() {
        return this.string;
    }
}

