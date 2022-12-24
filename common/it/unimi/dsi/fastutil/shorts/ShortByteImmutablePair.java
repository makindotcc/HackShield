/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortBytePair;
import java.io.Serializable;
import java.util.Objects;

public class ShortByteImmutablePair
implements ShortBytePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final short left;
    protected final byte right;

    public ShortByteImmutablePair(short left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static ShortByteImmutablePair of(short left, byte right) {
        return new ShortByteImmutablePair(left, right);
    }

    @Override
    public short leftShort() {
        return this.left;
    }

    @Override
    public byte rightByte() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ShortBytePair) {
            return this.left == ((ShortBytePair)other).leftShort() && this.right == ((ShortBytePair)other).rightByte();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftShort() + "," + this.rightByte() + ">";
    }
}

