/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.event.Subscribe
 *  com.velocitypowered.api.event.permission.PermissionsSetupEvent
 *  com.velocitypowered.proxy.connection.client.ConnectedPlayer
 */
package pl.hackshield.auth.impl.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import pl.hackshield.auth.impl.velocity.handler.HackShieldConnection;

public final class PermissionsSetupListener {
    @Subscribe
    public void onPermissionsSetup(PermissionsSetupEvent event) {
        if (!(event.getSubject() instanceof ConnectedPlayer)) {
            return;
        }
        ConnectedPlayer player = (ConnectedPlayer)event.getSubject();
        HackShieldConnection connection = (HackShieldConnection)player.getConnection().getChannel().pipeline().get("hackshield-handler");
        connection.setDisconnector(((ConnectedPlayer)player)::disconnect);
    }
}

