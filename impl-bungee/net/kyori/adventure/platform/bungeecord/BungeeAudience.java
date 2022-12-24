/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.platform.facet.Facet
 *  net.kyori.adventure.platform.facet.Facet$ActionBar
 *  net.kyori.adventure.platform.facet.Facet$BossBar
 *  net.kyori.adventure.platform.facet.Facet$BossBar$Builder
 *  net.kyori.adventure.platform.facet.Facet$Chat
 *  net.kyori.adventure.platform.facet.Facet$Pointers
 *  net.kyori.adventure.platform.facet.Facet$TabList
 *  net.kyori.adventure.platform.facet.Facet$Title
 *  net.kyori.adventure.platform.facet.FacetAudience
 *  net.kyori.adventure.platform.facet.FacetAudienceProvider
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  org.jetbrains.annotations.NotNull
 */
package net.kyori.adventure.platform.bungeecord;

import java.util.Collection;
import java.util.function.Supplier;
import net.kyori.adventure.platform.bungeecord.BungeeAudiencesImpl;
import net.kyori.adventure.platform.bungeecord.BungeeFacet;
import net.kyori.adventure.platform.facet.Facet;
import net.kyori.adventure.platform.facet.FacetAudience;
import net.kyori.adventure.platform.facet.FacetAudienceProvider;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

final class BungeeAudience
extends FacetAudience<CommandSender> {
    private static final Collection<Facet.Chat<? extends CommandSender, ?>> CHAT = Facet.of((Supplier[])new Supplier[]{BungeeFacet.ChatPlayerSenderId::new, BungeeFacet.ChatPlayer::new, BungeeFacet.ChatConsole::new});
    private static final Collection<Facet.ActionBar<ProxiedPlayer, ?>> ACTION_BAR = Facet.of((Supplier[])new Supplier[]{BungeeFacet.ActionBar::new});
    private static final Collection<Facet.Title<ProxiedPlayer, ?, ?, ?>> TITLE = Facet.of((Supplier[])new Supplier[]{BungeeFacet.Title::new});
    private static final Collection<Facet.BossBar.Builder<ProxiedPlayer, ? extends Facet.BossBar<ProxiedPlayer>>> BOSS_BAR = Facet.of((Supplier[])new Supplier[]{BungeeFacet.BossBar.Builder::new});
    private static final Collection<Facet.TabList<ProxiedPlayer, ?>> TAB_LIST = Facet.of((Supplier[])new Supplier[]{BungeeFacet.TabList::new});
    private static final Collection<Facet.Pointers<? extends CommandSender>> POINTERS = Facet.of((Supplier[])new Supplier[]{BungeeFacet.CommandSenderPointers::new, BungeeFacet.PlayerPointers::new});

    BungeeAudience(@NotNull BungeeAudiencesImpl provider, @NotNull Collection<? extends CommandSender> viewers) {
        super((FacetAudienceProvider)provider, viewers, CHAT, ACTION_BAR, TITLE, null, null, null, BOSS_BAR, TAB_LIST, POINTERS);
    }
}

