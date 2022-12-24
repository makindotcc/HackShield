/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.tablist;

import pl.hackshield.auth.api.HsPacket;

public class ForceTablistPacket
implements HsPacket {
    private boolean show;

    public ForceTablistPacket(boolean show) {
        this.show = show;
    }

    public ForceTablistPacket() {
        this.show = true;
    }

    public boolean isShow() {
        return this.show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ForceTablistPacket)) {
            return false;
        }
        ForceTablistPacket other = (ForceTablistPacket)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return this.isShow() == other.isShow();
    }

    protected boolean canEqual(Object other) {
        return other instanceof ForceTablistPacket;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isShow() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "ForceTablistPacket(show=" + this.isShow() + ")";
    }
}

