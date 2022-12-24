/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.util;

import pl.hackshield.auth.api.bossbar.BossInfo;
import pl.hackshield.auth.api.region.RegionInfo;
import pl.hackshield.auth.api.waypoint.WaypointOptions;

public class PacketUtil {
    public static int getFlags(BossInfo bossInfo) {
        int flags = 0;
        if (bossInfo.isDarkenSky()) {
            flags |= 1;
        }
        if (bossInfo.isPlayEndBossMusic()) {
            flags |= 2;
        }
        if (bossInfo.isCreateFog()) {
            flags |= 4;
        }
        if (bossInfo.isRenderBar()) {
            flags |= 8;
        }
        return flags;
    }

    public static int getFlags(WaypointOptions options) {
        int flags = 0;
        if (options.isShowInWaypointList()) {
            flags |= 1;
        }
        if (options.isShowDistance()) {
            flags |= 2;
        }
        if (options.isShowIcon()) {
            flags |= 4;
        }
        if (options.isShowName()) {
            flags |= 8;
        }
        if (options.isShowBeam()) {
            flags |= 0x10;
        }
        return flags;
    }

    public static int getFlags(RegionInfo regionInfo) {
        byte flags = 0;
        if (regionInfo.isBuildingAllowed()) {
            flags = (byte)(flags | true ? 1 : 0);
        }
        if (regionInfo.isDestroyingAllowed()) {
            flags = (byte)(flags | 2);
        }
        if (regionInfo.shouldRenderRegionBlocks()) {
            flags = (byte)(flags | 4);
        }
        return flags;
    }
}

