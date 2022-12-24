/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.audience.Audience
 *  net.kyori.adventure.platform.AudienceProvider
 *  net.kyori.adventure.platform.AudienceProvider$Builder
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.plugin.Plugin
 *  org.jetbrains.annotations.NotNull
 */
package net.kyori.adventure.platform.bungeecord;

import java.util.function.Predicate;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bungeecord.BungeeAudiencesImpl;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface BungeeAudiences
extends AudienceProvider {
    @NotNull
    public static BungeeAudiences create(@NotNull Plugin plugin) {
        return BungeeAudiencesImpl.instanceFor(plugin);
    }

    @NotNull
    public static Builder builder(@NotNull Plugin plugin) {
        return BungeeAudiencesImpl.builder(plugin);
    }

    @NotNull
    public Audience sender(@NotNull CommandSender var1);

    @NotNull
    public Audience player(@NotNull ProxiedPlayer var1);

    @NotNull
    public Audience filter(@NotNull Predicate<CommandSender> var1);

    public static interface Builder
    extends AudienceProvider.Builder<BungeeAudiences, Builder> {
    }
}

