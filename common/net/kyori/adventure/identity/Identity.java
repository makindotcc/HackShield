/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.identity;

import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;
import net.kyori.adventure.identity.IdentityImpl;
import net.kyori.adventure.identity.NilIdentity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.text.Component;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

public interface Identity
extends Examinable {
    public static final Pointer<String> NAME = Pointer.pointer(String.class, Key.key("adventure", "name"));
    public static final Pointer<UUID> UUID = Pointer.pointer(UUID.class, Key.key("adventure", "uuid"));
    public static final Pointer<Component> DISPLAY_NAME = Pointer.pointer(Component.class, Key.key("adventure", "display_name"));
    public static final Pointer<Locale> LOCALE = Pointer.pointer(Locale.class, Key.key("adventure", "locale"));

    @NotNull
    public static Identity nil() {
        return NilIdentity.INSTANCE;
    }

    @NotNull
    public static Identity identity(@NotNull UUID uuid) {
        if (uuid.equals(NilIdentity.NIL_UUID)) {
            return NilIdentity.INSTANCE;
        }
        return new IdentityImpl(uuid);
    }

    @NotNull
    public UUID uuid();

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("uuid", this.uuid()));
    }
}

