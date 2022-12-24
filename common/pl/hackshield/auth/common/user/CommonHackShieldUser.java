/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.user;

import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.hackshield.auth.api.user.HackShieldUser;
import pl.hackshield.auth.common.network.IHackShieldConnection;

public class CommonHackShieldUser
implements HackShieldUser {
    @Nullable
    private UUID minecraftID;
    @Nullable
    private UUID accountID;
    public boolean currentBattlePassPremium;
    private byte authorizationLevel;
    private String[] hardwareIds;
    @Nullable
    private String token;
    private IHackShieldConnection connection;

    public CommonHackShieldUser(IHackShieldConnection connection) {
        this.connection = connection;
    }

    @Nullable
    public final String getToken() {
        return this.token;
    }

    public IHackShieldConnection getConnection() {
        return this.connection;
    }

    public void setConnection(IHackShieldConnection connection) {
        this.connection = connection;
    }

    public final void setToken(@NotNull String token) {
        this.token = Objects.requireNonNull(token, "Token cannot be null!");
    }

    @Override
    @Nullable(value="before enabling C<->S encryption")
    public final @Nullable(value="before enabling C<->S encryption") UUID getMinecraftID() {
        return this.minecraftID;
    }

    @Override
    @Nullable(value="before HackShield API request")
    public final @Nullable(value="before HackShield API request") UUID getAccountID() {
        return this.accountID;
    }

    @Override
    @Nullable(value="before pre login state")
    public final @Nullable(value="before pre login state") String[] getHardwareIds() {
        return this.hardwareIds;
    }

    public void setCurrentBattlePassPremium(boolean currentBattlePassPremium) {
        this.currentBattlePassPremium = currentBattlePassPremium;
    }

    public boolean isCurrentBattlePassPremium() {
        return this.currentBattlePassPremium;
    }

    public final void setHardwareIds(@NotNull String[] hardwareIds) {
        this.hardwareIds = Objects.requireNonNull(hardwareIds, "Hardware IDs cannot be null!");
    }

    public final void setMinecraftID(@NotNull UUID minecraftID) {
        this.minecraftID = Objects.requireNonNull(minecraftID, "Minecraft player ID cannot be null!");
    }

    public final void setAccountID(@NotNull UUID accountID) {
        this.accountID = Objects.requireNonNull(accountID, "HackShield account ID cannot be null!");
    }

    public final boolean isHandshakeStateAuthorized() {
        return (this.authorizationLevel & 1) > 0;
    }

    public final void setHandshakeStateAuthorized(boolean handshakeStateAuthorized) {
        this.authorizationLevel = handshakeStateAuthorized ? (byte)(this.authorizationLevel | 1) : (byte)(this.authorizationLevel & 0xFFFFFFFE);
    }

    @Override
    public final boolean isAuthorized() {
        return (this.authorizationLevel & 2) > 0;
    }

    public final void setEncryptionStateAuthorized(boolean encryptionStateAuthorized) {
        this.authorizationLevel = encryptionStateAuthorized ? (byte)(this.authorizationLevel | 2) : (byte)(this.authorizationLevel & 0xFFFFFFFD);
    }

    public CommonHackShieldUser() {
    }
}

