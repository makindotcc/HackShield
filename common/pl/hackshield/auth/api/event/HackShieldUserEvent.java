/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerEvent
 */
package pl.hackshield.auth.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import pl.hackshield.auth.api.user.HackShieldUser;

public abstract class HackShieldUserEvent
extends PlayerEvent {
    protected HackShieldUser user;

    public HackShieldUserEvent(@NotNull Player who, @NotNull HackShieldUser user) {
        super(who);
        this.user = user;
    }

    @NotNull
    public final HackShieldUser getUser() {
        return this.user;
    }
}

