/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.data;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Comments;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import pl.hackshield.auth.common.data.Mod;
import pl.hackshield.auth.common.data.PassiveVerification;
import pl.hackshield.auth.common.data.ServerSettings;
import pl.hackshield.auth.common.util.MapBuilder;

public final class Config
extends OkaeriConfig {
    @Comments(value={@Comment(value={"PL: Ta wiadomo\u015b\u0107 pojawi si\u0119 je\u017celi gracz b\u0119dzie pr\u00f3bowa\u0142 wej\u015b\u0107 na serwer bez klienta HackShield."}), @Comment(value={"ENG: This message will be displayed to players who try to join your server without HackShield client."})})
    public List<String> disconnectMessage = Arrays.asList("&5Ten serwer jest zabezpieczony przed cheaterami!", "", "&7Aby dolaczyc do tego serwera musisz uzyc klienta &5HackShield.", "&7Mozesz go pobrac ze strony: &5www.HackShield.pl");
    @Comments(value={@Comment(value={"PL:"}), @Comment(value={"@enabled: W\u0142\u0105cz je\u015bli chcesz by gracze mogli do\u0142\u0105czy\u0107 na serwer nie maj\u0105c Klienta HackShield"}), @Comment(value={"@servers: (dotyczy tylko proxy)"}), @Comment(value={"Lista serwer\u00f3w do kt\u00f3rych b\u0119d\u0105 mogli do\u0142\u0105czy\u0107 gracze bez posiadania klienta HackShield (nazwy serwer\u00f3w z configu proxy)"}), @Comment(value={"U\u017cyj '*' (wraz z apostrofami) je\u015bli chcesz by dotyczy\u0142o to ca\u0142ej sieci"}), @Comment(value={"ENG:"}), @Comment(value={"@enabled: Enable if you want to players to be able to join the server without the HackShield client"}), @Comment(value={"@servers: (Concerns only proxy)"}), @Comment(value={"List of servers that players can join without the HackShield client (proxy server names)"}), @Comment(value={"Use '*' (with single quotes) if you want apply to the entire network"})})
    public PassiveVerification passiveVerification = new PassiveVerification();
    @Comments(value={@Comment(value={"PL:"}), @Comment(value={"@moveOnUnloadedChunks: Czy mo\u017cna porusza\u0107 si\u0119 po nieza\u0142adowanych chunkach"}), @Comment(value={"@showHeadsOnTablist: Czy wy\u015bwietla\u0107 g\u0142\u00f3wki obok nick\u00f3w na Tabli\u015bcie"}), @Comment(value={"@showPingOnTablist: Czy pokazywa\u0107 ping na Tabli\u015bcie"}), @Comment(value={"@disableF3Ashortcut: Czy wy\u0142\u0105czy\u0107 skr\u00f3t F3"}), @Comment(value={"@disableSocialInteractionsScreen: "}), @Comment(value={"@sendNotificationIfSocialInteractionsAreDisabled: "}), @Comment(value={"ENG:"})})
    public ServerSettings settings = new ServerSettings();
    @Comments(value={@Comment(value={"PL: Lista mod\u00f3w"}), @Comment(value={"ENG: Mod list"})})
    public List<Mod> mods = Arrays.asList(new Mod("Block Outline"), new Mod("Crosshair"), new Mod("Item Physics"), new Mod("OldAnimations"), new Mod("ActionBar"), new Mod("ActiveEffects"), new Mod("Armor Status"), new Mod("BossBar"), new Mod("Auto Sprint"), new Mod("Chat"), new Mod("Compass"), new Mod("Coordinates"), new Mod("FPS"), new Mod("Keystrokes"), new Mod("Minimap"), new Mod("ReachDisplay"), new Mod("Sidebar"), new Mod("Tablist"), new Mod("ChunkAnimator"), new Mod("Perspective"), new Mod("Binds", true, new MapBuilder<String, Integer>().add("cooldown", 10).add("blockedCommands", Collections.singletonList("/zablokowana komenda")).build()), new Mod("BetterInfo"), new Mod("CPS Counter"), new Mod("Tnt Timer"), new Mod("Time Changer"), new Mod("MotionBlur"));

    public Component getDisconnectMessageAsComponent() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(String.join((CharSequence)"\n", this.disconnectMessage));
    }

    public List<Mod> getModsWithSettings() {
        return this.mods.stream().filter(mod -> mod.getSettings().size() > 0).collect(Collectors.toList());
    }
}

