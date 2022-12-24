/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.proxy.Player
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  org.bukkit.entity.Player
 */
package pl.hackshield.auth.api;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.icon.Icon;
import pl.hackshield.auth.api.quest.condition.base.Condition;
import pl.hackshield.auth.api.user.HackShieldUser;

public interface HackShieldAPI {
    public HackShieldUser getUser(UUID var1);

    default public HackShieldUser getUser(Player player) {
        throw new UnsupportedOperationException("The method is available only on the game server!");
    }

    default public HackShieldUser getUser(com.velocitypowered.api.proxy.Player player) {
        throw new UnsupportedOperationException("The method is available only on the proxy server!");
    }

    default public HackShieldUser getUser(ProxiedPlayer player) {
        throw new UnsupportedOperationException("The method is available only on the proxy server!");
    }

    public void sendPacket(HackShieldUser var1, HsPacket var2);

    public void sendPackets(HackShieldUser var1, Collection<HsPacket> var2);

    public void registerIcon(Icon var1);

    public void unregisterIcon(UUID var1);

    default public Component getDisconnectMessage() {
        return null;
    }

    default public void setDisplayIconAboveHeadRequestHandler(Consumer<UUID> handler) {
        throw new UnsupportedOperationException("The method is available only on the game server!");
    }

    default public boolean addPassiveVerificationServer(String serverName) {
        throw new UnsupportedOperationException("The method is available only on the proxy server!");
    }

    default public boolean removePassiveVerificationServer(String serverName) {
        throw new UnsupportedOperationException("The method is available only on the proxy server!");
    }

    default public void fireQuestCheck(HackShieldUser user, Condition condition) {
        throw new UnsupportedOperationException("The method is available only on the game server!");
    }
}

