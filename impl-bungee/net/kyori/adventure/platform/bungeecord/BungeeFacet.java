/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.audience.MessageType
 *  net.kyori.adventure.bossbar.BossBar
 *  net.kyori.adventure.bossbar.BossBar$Flag
 *  net.kyori.adventure.identity.Identity
 *  net.kyori.adventure.permission.PermissionChecker
 *  net.kyori.adventure.platform.facet.Facet
 *  net.kyori.adventure.platform.facet.Facet$ActionBar
 *  net.kyori.adventure.platform.facet.Facet$BossBar$Builder
 *  net.kyori.adventure.platform.facet.Facet$BossBarPacket
 *  net.kyori.adventure.platform.facet.Facet$Chat
 *  net.kyori.adventure.platform.facet.Facet$Message
 *  net.kyori.adventure.platform.facet.Facet$Pointers
 *  net.kyori.adventure.platform.facet.Facet$TabList
 *  net.kyori.adventure.platform.facet.Facet$Title
 *  net.kyori.adventure.platform.facet.FacetBase
 *  net.kyori.adventure.platform.facet.FacetComponentFlattener
 *  net.kyori.adventure.platform.facet.FacetComponentFlattener$Translator
 *  net.kyori.adventure.platform.facet.FacetPointers
 *  net.kyori.adventure.platform.facet.FacetPointers$Type
 *  net.kyori.adventure.platform.facet.Knob
 *  net.kyori.adventure.pointer.Pointers$Builder
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.flattener.ComponentFlattener
 *  net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  net.kyori.adventure.util.TriState
 *  net.md_5.bungee.api.ChatMessageType
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.Title
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.connection.Connection
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.chat.ComponentSerializer
 *  net.md_5.bungee.chat.TranslationRegistry
 *  net.md_5.bungee.protocol.DefinedPacket
 *  net.md_5.bungee.protocol.packet.BossBar
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.kyori.adventure.platform.bungeecord;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Supplier;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.permission.PermissionChecker;
import net.kyori.adventure.platform.facet.Facet;
import net.kyori.adventure.platform.facet.FacetBase;
import net.kyori.adventure.platform.facet.FacetComponentFlattener;
import net.kyori.adventure.platform.facet.FacetPointers;
import net.kyori.adventure.platform.facet.Knob;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.util.TriState;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.TranslationRegistry;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class BungeeFacet<V extends CommandSender>
extends FacetBase<V> {
    static final BaseComponent[] EMPTY_COMPONENT_ARRAY = new BaseComponent[0];
    private static final Collection<? extends FacetComponentFlattener.Translator<ProxyServer>> TRANSLATORS = Facet.of((Supplier[])new Supplier[]{Translator::new});
    static final ComponentFlattener FLATTENER = FacetComponentFlattener.get((Object)ProxyServer.getInstance(), TRANSLATORS);
    static final BungeeComponentSerializer MODERN = BungeeComponentSerializer.of(GsonComponentSerializer.gson(), LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().flattener(FLATTENER).build());
    static final BungeeComponentSerializer LEGACY = BungeeComponentSerializer.of(GsonComponentSerializer.builder().downsampleColors().emitLegacyHoverEvent().build(), LegacyComponentSerializer.builder().flattener(FLATTENER).build());

    protected BungeeFacet(@Nullable Class<? extends V> viewerClass) {
        super(viewerClass);
    }

    static class Translator
    extends FacetBase<ProxyServer>
    implements FacetComponentFlattener.Translator<ProxyServer> {
        private static final boolean SUPPORTED;

        Translator() {
            super(ProxyServer.class);
        }

        public boolean isSupported() {
            return super.isSupported() && SUPPORTED;
        }

        @NotNull
        public String valueOrDefault(@NotNull ProxyServer game, @NotNull String key) {
            return TranslationRegistry.INSTANCE.translate(key);
        }

        static {
            boolean supported;
            try {
                Class.forName("net.md_5.bungee.chat.TranslationRegistry");
                supported = true;
            }
            catch (ClassNotFoundException ex) {
                supported = false;
            }
            SUPPORTED = supported;
        }
    }

    static final class PlayerPointers
    extends BungeeFacet<ProxiedPlayer>
    implements Facet.Pointers<ProxiedPlayer> {
        PlayerPointers() {
            super(ProxiedPlayer.class);
        }

        public void contributePointers(ProxiedPlayer viewer, Pointers.Builder builder) {
            builder.withDynamic(Identity.UUID, ((ProxiedPlayer)viewer)::getUniqueId);
            builder.withDynamic(Identity.LOCALE, ((ProxiedPlayer)viewer)::getLocale);
            builder.withDynamic(FacetPointers.SERVER, () -> viewer.getServer().getInfo().getName());
            builder.withStatic(FacetPointers.TYPE, (Object)FacetPointers.Type.PLAYER);
        }
    }

    static final class CommandSenderPointers
    extends BungeeFacet<CommandSender>
    implements Facet.Pointers<CommandSender> {
        CommandSenderPointers() {
            super(CommandSender.class);
        }

        public void contributePointers(CommandSender viewer, Pointers.Builder builder) {
            builder.withDynamic(Identity.NAME, ((CommandSender)viewer)::getName);
            builder.withStatic(PermissionChecker.POINTER, (Object)((PermissionChecker)perm -> viewer.hasPermission(perm) ? TriState.TRUE : TriState.FALSE));
            if (!(viewer instanceof ProxiedPlayer)) {
                builder.withStatic(FacetPointers.TYPE, (Object)(viewer == ProxyServer.getInstance().getConsole() ? FacetPointers.Type.CONSOLE : FacetPointers.Type.OTHER));
            }
        }
    }

    static final class TabList
    extends Message
    implements Facet.TabList<ProxiedPlayer, BaseComponent[]> {
        TabList() {
        }

        public void send(ProxiedPlayer viewer, BaseComponent @Nullable [] header, BaseComponent @Nullable [] footer) {
            viewer.setTabHeader(header == null ? EMPTY_COMPONENT_ARRAY : header, footer == null ? EMPTY_COMPONENT_ARRAY : footer);
        }
    }

    static class BossBar
    extends Message
    implements Facet.BossBarPacket<ProxiedPlayer> {
        private final Set<ProxiedPlayer> viewers;
        private final net.md_5.bungee.protocol.packet.BossBar bar;
        private volatile boolean initialized = false;

        protected BossBar(@NotNull Collection<ProxiedPlayer> viewers) {
            this.viewers = new CopyOnWriteArraySet<ProxiedPlayer>(viewers);
            this.bar = new net.md_5.bungee.protocol.packet.BossBar(UUID.randomUUID(), 0);
        }

        public void bossBarInitialized(@NotNull net.kyori.adventure.bossbar.BossBar bar) {
            super.bossBarInitialized(bar);
            this.initialized = true;
            this.broadcastPacket(0);
        }

        public void bossBarNameChanged(@NotNull net.kyori.adventure.bossbar.BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
            if (!this.viewers.isEmpty()) {
                this.bar.setTitle(ComponentSerializer.toString((BaseComponent[])this.createMessage(this.viewers.iterator().next(), newName)));
                this.broadcastPacket(3);
            }
        }

        public void bossBarProgressChanged(@NotNull net.kyori.adventure.bossbar.BossBar bar, float oldPercent, float newPercent) {
            this.bar.setHealth(newPercent);
            this.broadcastPacket(2);
        }

        public void bossBarColorChanged(@NotNull net.kyori.adventure.bossbar.BossBar bar, // Could not load outer class - annotation placement on inner may be incorrect
         @NotNull BossBar.Color oldColor, // Could not load outer class - annotation placement on inner may be incorrect
         @NotNull BossBar.Color newColor) {
            this.bar.setColor(this.createColor(newColor));
            this.broadcastPacket(4);
        }

        public void bossBarOverlayChanged(@NotNull net.kyori.adventure.bossbar.BossBar bar, // Could not load outer class - annotation placement on inner may be incorrect
         @NotNull BossBar.Overlay oldOverlay, // Could not load outer class - annotation placement on inner may be incorrect
         @NotNull BossBar.Overlay newOverlay) {
            this.bar.setDivision(this.createOverlay(newOverlay));
            this.broadcastPacket(4);
        }

        public void bossBarFlagsChanged(@NotNull net.kyori.adventure.bossbar.BossBar bar, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {
            this.bar.setFlags(this.createFlag(this.bar.getFlags(), flagsAdded, flagsRemoved));
            this.broadcastPacket(5);
        }

        public void addViewer(@NotNull ProxiedPlayer viewer) {
            this.viewers.add(viewer);
            this.sendPacket(0, viewer);
        }

        public void removeViewer(@NotNull ProxiedPlayer viewer) {
            this.viewers.remove((Object)viewer);
            this.sendPacket(1, viewer);
        }

        public boolean isEmpty() {
            return !this.initialized || this.viewers.isEmpty();
        }

        public void close() {
            this.broadcastPacket(1);
            this.viewers.clear();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void broadcastPacket(int action) {
            if (this.isEmpty()) {
                return;
            }
            net.md_5.bungee.protocol.packet.BossBar bossBar = this.bar;
            synchronized (bossBar) {
                this.bar.setAction(action);
                for (ProxiedPlayer viewer : this.viewers) {
                    viewer.unsafe().sendPacket((DefinedPacket)this.bar);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void sendPacket(int action, ProxiedPlayer ... viewers) {
            net.md_5.bungee.protocol.packet.BossBar bossBar = this.bar;
            synchronized (bossBar) {
                int lastAction = this.bar.getAction();
                this.bar.setAction(action);
                for (ProxiedPlayer viewer : viewers) {
                    viewer.unsafe().sendPacket((DefinedPacket)this.bar);
                }
                this.bar.setAction(lastAction);
            }
        }

        static class Builder
        extends BungeeFacet<ProxiedPlayer>
        implements Facet.BossBar.Builder<ProxiedPlayer, BossBar> {
            protected Builder() {
                super(ProxiedPlayer.class);
            }

            public boolean isApplicable(@NotNull ProxiedPlayer viewer) {
                return super.isApplicable((Object)viewer) && viewer.getPendingConnection().getVersion() >= 356;
            }

            public @NotNull BossBar createBossBar(@NotNull Collection<ProxiedPlayer> viewers) {
                return new BossBar(viewers);
            }
        }
    }

    static class Title
    extends Message
    implements Facet.Title<ProxiedPlayer, BaseComponent[], net.md_5.bungee.api.Title, net.md_5.bungee.api.Title> {
        private static final net.md_5.bungee.api.Title CLEAR = ProxyServer.getInstance().createTitle().clear();
        private static final net.md_5.bungee.api.Title RESET = ProxyServer.getInstance().createTitle().reset();

        Title() {
        }

        public @NotNull net.md_5.bungee.api.Title createTitleCollection() {
            return ProxyServer.getInstance().createTitle();
        }

        public void contributeTitle(@NotNull net.md_5.bungee.api.Title coll, BaseComponent @NotNull [] title) {
            coll.title(title);
        }

        public void contributeSubtitle(@NotNull net.md_5.bungee.api.Title coll, BaseComponent @NotNull [] subtitle) {
            coll.subTitle(subtitle);
        }

        public void contributeTimes(@NotNull net.md_5.bungee.api.Title coll, int inTicks, int stayTicks, int outTicks) {
            if (inTicks > -1) {
                coll.fadeIn(inTicks);
            }
            if (stayTicks > -1) {
                coll.stay(stayTicks);
            }
            if (outTicks > -1) {
                coll.fadeOut(outTicks);
            }
        }

        @Nullable
        public net.md_5.bungee.api.Title completeTitle(@NotNull net.md_5.bungee.api.Title coll) {
            return coll;
        }

        public void showTitle(@NotNull ProxiedPlayer viewer, @NotNull net.md_5.bungee.api.Title title) {
            viewer.sendTitle(title);
        }

        public void clearTitle(@NotNull ProxiedPlayer viewer) {
            viewer.sendTitle(CLEAR);
        }

        public void resetTitle(@NotNull ProxiedPlayer viewer) {
            viewer.sendTitle(RESET);
        }
    }

    static class ActionBar
    extends Message
    implements Facet.ActionBar<ProxiedPlayer, BaseComponent[]> {
        ActionBar() {
        }

        public void sendMessage(@NotNull ProxiedPlayer viewer, BaseComponent @NotNull [] message) {
            viewer.sendMessage(ChatMessageType.ACTION_BAR, message);
        }
    }

    static class ChatPlayer
    extends Message
    implements Facet.Chat<ProxiedPlayer, BaseComponent[]> {
        ChatPlayer() {
        }

        @Nullable
        public ChatMessageType createType(@NotNull MessageType type) {
            if (type == MessageType.CHAT) {
                return ChatMessageType.CHAT;
            }
            if (type == MessageType.SYSTEM) {
                return ChatMessageType.SYSTEM;
            }
            Knob.logUnsupported((Object)((Object)this), (Object)type);
            return null;
        }

        public void sendMessage(@NotNull ProxiedPlayer viewer, @NotNull Identity source, BaseComponent @NotNull [] message, @NotNull MessageType type) {
            ChatMessageType chat = this.createType(type);
            if (chat != null) {
                viewer.sendMessage(chat, message);
            }
        }
    }

    static class ChatPlayerSenderId
    extends ChatPlayer
    implements Facet.Chat<ProxiedPlayer, BaseComponent[]> {
        private static final boolean SUPPORTED;

        ChatPlayerSenderId() {
        }

        public boolean isSupported() {
            return super.isSupported() && SUPPORTED;
        }

        @Override
        public void sendMessage(@NotNull ProxiedPlayer viewer, @NotNull Identity source, BaseComponent @NotNull [] message, @NotNull MessageType type) {
            if (type == MessageType.CHAT) {
                viewer.sendMessage(source.uuid(), message);
            } else {
                super.sendMessage(viewer, source, message, type);
            }
        }

        static {
            boolean supported;
            try {
                ProxiedPlayer.class.getMethod("sendMessage", UUID.class, BaseComponent.class);
                supported = true;
            }
            catch (NoSuchMethodException ex) {
                supported = false;
            }
            SUPPORTED = supported;
        }
    }

    static class Message
    extends BungeeFacet<ProxiedPlayer>
    implements Facet.Message<ProxiedPlayer, BaseComponent[]> {
        protected Message() {
            super(ProxiedPlayer.class);
        }

        public BaseComponent @NotNull [] createMessage(@NotNull ProxiedPlayer viewer, @NotNull Component message) {
            if (viewer.getPendingConnection().getVersion() >= 713) {
                return MODERN.serialize(message);
            }
            return LEGACY.serialize(message);
        }
    }

    static class ChatConsole
    extends BungeeFacet<CommandSender>
    implements Facet.Chat<CommandSender, BaseComponent[]> {
        protected ChatConsole() {
            super(CommandSender.class);
        }

        public boolean isApplicable(@NotNull CommandSender viewer) {
            return super.isApplicable((Object)viewer) && !(viewer instanceof Connection);
        }

        public BaseComponent @NotNull [] createMessage(@NotNull CommandSender viewer, @NotNull Component message) {
            return LEGACY.serialize(message);
        }

        public void sendMessage(@NotNull CommandSender viewer, @NotNull Identity source, BaseComponent @NotNull [] message, @NotNull MessageType type) {
            viewer.sendMessage(message);
        }
    }
}

