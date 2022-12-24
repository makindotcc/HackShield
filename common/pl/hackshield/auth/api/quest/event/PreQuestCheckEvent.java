/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.HandlerList
 */
package pl.hackshield.auth.api.quest.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import pl.hackshield.auth.api.event.HackShieldUserEvent;
import pl.hackshield.auth.api.quest.condition.base.Condition;
import pl.hackshield.auth.api.user.HackShieldUser;

public class PreQuestCheckEvent
extends HackShieldUserEvent
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final Condition condition;

    public PreQuestCheckEvent(@NotNull Player who, @NotNull HackShieldUser user, Condition condition) {
        super(who, user);
        this.condition = condition;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }
}

