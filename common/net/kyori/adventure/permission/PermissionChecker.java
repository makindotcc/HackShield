/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.permission;

import java.util.function.Predicate;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.permission.PermissionCheckers;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;

public interface PermissionChecker
extends Predicate<String> {
    public static final Pointer<PermissionChecker> POINTER = Pointer.pointer(PermissionChecker.class, Key.key("adventure", "permission"));

    @NotNull
    public static PermissionChecker always(TriState state) {
        if (state == TriState.TRUE) {
            return PermissionCheckers.TRUE;
        }
        if (state == TriState.FALSE) {
            return PermissionCheckers.FALSE;
        }
        return PermissionCheckers.NOT_SET;
    }

    @NotNull
    public TriState value(String var1);

    @Override
    default public boolean test(String permission) {
        return this.value(permission) == TriState.TRUE;
    }
}

