/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntLongPair;
import java.io.Serializable;
import java.util.Objects;

public class IntLongImmutablePair
implements IntLongPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final int left;
    protected final long right;

    public IntLongImmutablePair(int left, long right) {
        this.left = left;
        this.right = right;
    }

    public static IntLongImmutablePair of(int left, long right) {
        return new IntLongImmutablePair(left, right);
    }

    @Override
    public int leftInt() {
        return this.left;
    }

    @Override
    public long rightLong() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntLongPair) {
            return this.left == ((IntLongPair)other).leftInt() && this.right == ((IntLongPair)other).rightLong();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.long2int(this.right);
    }

    public String toString() {
        return "<" + this.leftInt() + "," + this.rightLong() + ">";
    }
}

