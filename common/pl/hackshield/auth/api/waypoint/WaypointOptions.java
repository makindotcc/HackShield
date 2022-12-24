/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.waypoint;

import java.io.Serializable;

public final class WaypointOptions
implements Serializable {
    private boolean showInWaypointList = true;
    private boolean showDistance = true;
    private boolean showIcon = true;
    private boolean showName = true;
    private boolean showBeam = true;
    private boolean showTextBackground = true;
    private double showBeamDistance = 100.0;
    private long removeAfter;
    private int beamHeight = 255;
    private int beamWidth = 5;

    public boolean isShowInWaypointList() {
        return this.showInWaypointList;
    }

    public boolean isShowDistance() {
        return this.showDistance;
    }

    public boolean isShowIcon() {
        return this.showIcon;
    }

    public boolean isShowName() {
        return this.showName;
    }

    public boolean isShowBeam() {
        return this.showBeam;
    }

    public boolean isShowTextBackground() {
        return this.showTextBackground;
    }

    public double getShowBeamDistance() {
        return this.showBeamDistance;
    }

    public long getRemoveAfter() {
        return this.removeAfter;
    }

    public int getBeamHeight() {
        return this.beamHeight;
    }

    public int getBeamWidth() {
        return this.beamWidth;
    }

    public void setShowInWaypointList(boolean showInWaypointList) {
        this.showInWaypointList = showInWaypointList;
    }

    public void setShowDistance(boolean showDistance) {
        this.showDistance = showDistance;
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
    }

    public void setShowBeam(boolean showBeam) {
        this.showBeam = showBeam;
    }

    public void setShowTextBackground(boolean showTextBackground) {
        this.showTextBackground = showTextBackground;
    }

    public void setShowBeamDistance(double showBeamDistance) {
        this.showBeamDistance = showBeamDistance;
    }

    public void setRemoveAfter(long removeAfter) {
        this.removeAfter = removeAfter;
    }

    public void setBeamHeight(int beamHeight) {
        this.beamHeight = beamHeight;
    }

    public void setBeamWidth(int beamWidth) {
        this.beamWidth = beamWidth;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WaypointOptions)) {
            return false;
        }
        WaypointOptions other = (WaypointOptions)o;
        if (this.isShowInWaypointList() != other.isShowInWaypointList()) {
            return false;
        }
        if (this.isShowDistance() != other.isShowDistance()) {
            return false;
        }
        if (this.isShowIcon() != other.isShowIcon()) {
            return false;
        }
        if (this.isShowName() != other.isShowName()) {
            return false;
        }
        if (this.isShowBeam() != other.isShowBeam()) {
            return false;
        }
        if (this.isShowTextBackground() != other.isShowTextBackground()) {
            return false;
        }
        if (Double.compare(this.getShowBeamDistance(), other.getShowBeamDistance()) != 0) {
            return false;
        }
        if (this.getRemoveAfter() != other.getRemoveAfter()) {
            return false;
        }
        if (this.getBeamHeight() != other.getBeamHeight()) {
            return false;
        }
        return this.getBeamWidth() == other.getBeamWidth();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isShowInWaypointList() ? 79 : 97);
        result = result * 59 + (this.isShowDistance() ? 79 : 97);
        result = result * 59 + (this.isShowIcon() ? 79 : 97);
        result = result * 59 + (this.isShowName() ? 79 : 97);
        result = result * 59 + (this.isShowBeam() ? 79 : 97);
        result = result * 59 + (this.isShowTextBackground() ? 79 : 97);
        long $showBeamDistance = Double.doubleToLongBits(this.getShowBeamDistance());
        result = result * 59 + (int)($showBeamDistance >>> 32 ^ $showBeamDistance);
        long $removeAfter = this.getRemoveAfter();
        result = result * 59 + (int)($removeAfter >>> 32 ^ $removeAfter);
        result = result * 59 + this.getBeamHeight();
        result = result * 59 + this.getBeamWidth();
        return result;
    }

    public String toString() {
        return "WaypointOptions(showInWaypointList=" + this.isShowInWaypointList() + ", showDistance=" + this.isShowDistance() + ", showIcon=" + this.isShowIcon() + ", showName=" + this.isShowName() + ", showBeam=" + this.isShowBeam() + ", showTextBackground=" + this.isShowTextBackground() + ", showBeamDistance=" + this.getShowBeamDistance() + ", removeAfter=" + this.getRemoveAfter() + ", beamHeight=" + this.getBeamHeight() + ", beamWidth=" + this.getBeamWidth() + ")";
    }
}

