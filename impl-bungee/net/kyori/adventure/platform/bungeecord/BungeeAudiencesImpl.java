/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  net.kyori.adventure.audience.Audience
 *  net.kyori.adventure.identity.Identity
 *  net.kyori.adventure.platform.facet.FacetAudienceProvider
 *  net.kyori.adventure.platform.facet.Knob
 *  net.kyori.adventure.pointer.Pointered
 *  net.kyori.adventure.text.flattener.ComponentFlattener
 *  net.kyori.adventure.text.renderer.ComponentRenderer
 *  net.kyori.adventure.translation.GlobalTranslator
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.event.PlayerDisconnectEvent
 *  net.md_5.bungee.api.event.PostLoginEvent
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.api.plugin.Plugin
 *  net.md_5.bungee.event.EventHandler
 *  org.jetbrains.annotations.NotNull
 */
package net.kyori.adventure.platform.bungeecord;

import com.google.gson.Gson;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.bungeecord.BungeeAudience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.platform.bungeecord.BungeeFacet;
import net.kyori.adventure.platform.facet.FacetAudienceProvider;
import net.kyori.adventure.platform.facet.Knob;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

final class BungeeAudiencesImpl
extends FacetAudienceProvider<CommandSender, BungeeAudience>
implements BungeeAudiences {
    private static final Map<String, BungeeAudiences> INSTANCES;
    private final Plugin plugin;
    private final Listener listener;

    @NotNull
    static BungeeAudiences instanceFor(@NotNull Plugin plugin) {
        return BungeeAudiencesImpl.builder(plugin).build();
    }

    @NotNull
    static Builder builder(@NotNull Plugin plugin) {
        return new Builder(plugin);
    }

    BungeeAudiencesImpl(Plugin plugin, @NotNull ComponentRenderer<Pointered> componentRenderer) {
        super(componentRenderer);
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.listener = new Listener();
        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, (net.md_5.bungee.api.plugin.Listener)this.listener);
        CommandSender console = this.plugin.getProxy().getConsole();
        this.addViewer((Object)console);
        for (ProxiedPlayer player : this.plugin.getProxy().getPlayers()) {
            this.addViewer((Object)player);
        }
    }

    @Override
    @NotNull
    public Audience sender(@NotNull CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            return this.player((ProxiedPlayer)sender);
        }
        if (ProxyServer.getInstance().getConsole().equals((Object)sender)) {
            return this.console();
        }
        return this.createAudience(Collections.singletonList(sender));
    }

    @Override
    @NotNull
    public Audience player(@NotNull ProxiedPlayer player) {
        return this.player(player.getUniqueId());
    }

    @NotNull
    protected BungeeAudience createAudience(@NotNull Collection<CommandSender> viewers) {
        return new BungeeAudience(this, viewers);
    }

    @NotNull
    public ComponentFlattener flattener() {
        return BungeeFacet.FLATTENER;
    }

    public void close() {
        INSTANCES.remove(this.plugin.getDescription().getName());
        this.plugin.getProxy().getPluginManager().unregisterListener((net.md_5.bungee.api.plugin.Listener)this.listener);
        super.close();
    }

    static {
        Knob.OUT = message -> ProxyServer.getInstance().getLogger().log(Level.INFO, (String)message);
        Knob.ERR = (message, error) -> ProxyServer.getInstance().getLogger().log(Level.WARNING, (String)message, (Throwable)error);
        try {
            Field gsonField = ProxyServer.getInstance().getClass().getDeclaredField("gson");
            gsonField.setAccessible(true);
            Gson gson = (Gson)gsonField.get((Object)ProxyServer.getInstance());
            BungeeComponentSerializer.inject(gson);
        }
        catch (Throwable error2) {
            Knob.logError((Throwable)error2, (String)"Failed to inject ProxyServer gson", (Object[])new Object[0]);
        }
        INSTANCES = Collections.synchronizedMap(new HashMap(4));
    }

    public final class Listener
    implements net.md_5.bungee.api.plugin.Listener {
        @EventHandler(priority=-128)
        public void onLogin(PostLoginEvent event) {
            BungeeAudiencesImpl.this.addViewer((Object)event.getPlayer());
        }

        @EventHandler(priority=127)
        public void onDisconnect(PlayerDisconnectEvent event) {
            BungeeAudiencesImpl.this.removeViewer((Object)event.getPlayer());
        }
    }

    static final class Builder
    implements BungeeAudiences.Builder {
        @NotNull
        private final Plugin plugin;
        private ComponentRenderer<Pointered> componentRenderer;

        Builder(@NotNull Plugin plugin) {
            this.plugin = Objects.requireNonNull(plugin, "plugin");
            this.componentRenderer(ptr -> (Locale)ptr.getOrDefault(Identity.LOCALE, (Object)DEFAULT_LOCALE), (ComponentRenderer)GlobalTranslator.renderer());
        }

        @NotNull
        public Builder componentRenderer(@NotNull ComponentRenderer<Pointered> componentRenderer) {
            this.componentRenderer = Objects.requireNonNull(componentRenderer, "component renderer");
            return this;
        }

        public @NotNull BungeeAudiences.Builder partition(@NotNull Function<Pointered, ?> partitionFunction) {
            Objects.requireNonNull(partitionFunction, "partitionFunction");
            return this;
        }

        @NotNull
        public BungeeAudiences build() {
            return INSTANCES.computeIfAbsent(this.plugin.getDescription().getName(), name -> new BungeeAudiencesImpl(this.plugin, this.componentRenderer));
        }
    }
}

