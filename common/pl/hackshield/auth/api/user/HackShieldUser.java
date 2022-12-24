/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.user;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public interface HackShieldUser {
    @Nullable(value="before enabling C<->S encryption")
    public @Nullable(value="before enabling C<->S encryption") UUID getMinecraftID();

    @Nullable(value="before HackShield API request")
    public @Nullable(value="before HackShield API request") UUID getAccountID();

    @Nullable(value="before pre login state")
    public @Nullable(value="before pre login state") String[] getHardwareIds();

    public boolean isAuthorized();
}

