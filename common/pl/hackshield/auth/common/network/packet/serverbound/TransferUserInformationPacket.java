/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.serverbound;

import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;
import pl.hackshield.auth.common.user.CommonHackShieldUser;

public final class TransferUserInformationPacket
implements HackShieldPacket {
    private final CommonHackShieldUser user;

    public TransferUserInformationPacket(IPacketBuffer packetBuffer) {
        this.user = new CommonHackShieldUser();
        this.user.setEncryptionStateAuthorized(packetBuffer.getBuffer().readBoolean());
        if (!this.user.isAuthorized()) {
            return;
        }
        this.user.setHandshakeStateAuthorized(true);
        this.user.setMinecraftID(packetBuffer.readUUID());
        this.user.setAccountID(packetBuffer.readUUID());
        this.user.setCurrentBattlePassPremium(packetBuffer.getBuffer().readBoolean());
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        packetBuffer.getBuffer().writeBoolean(this.user.isAuthorized());
        if (!this.user.isAuthorized()) {
            return;
        }
        packetBuffer.writeUUID(this.user.getMinecraftID());
        packetBuffer.writeUUID(this.user.getAccountID());
        packetBuffer.getBuffer().writeBoolean(this.user.isCurrentBattlePassPremium());
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
        packetHandler.handlePacket(this);
    }

    public CommonHackShieldUser getUser() {
        return this.user;
    }

    public TransferUserInformationPacket(CommonHackShieldUser user) {
        this.user = user;
    }
}

