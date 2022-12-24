/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.shorts.ShortShortImmutablePair;
import it.unimi.dsi.fastutil.shorts.ShortShortSortedPair;
import java.io.Serializable;
import java.util.Objects;

public class ShortShortImmutableSortedPair
extends ShortShortImmutablePair
implements ShortShortSortedPair,
Serializable {
    private static final long serialVersionUID = 0L;

    private ShortShortImmutableSortedPair(short left, short right) {
        super(left, right);
    }

    public static ShortShortImmutableSortedPair of(short left, short right) {
        if (left <= right) {
            return new ShortShortImmutableSortedPair(left, right);
        }
        return new ShortShortImmutableSortedPair(right, left);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ShortShortSortedPair) {
            return this.left == ((ShortShortSortedPair)other).leftShort() && this.right == ((ShortShortSortedPair)other).rightShort();
        }
        if (other instanceof SortedPair) {
            return Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + this.leftShort() + "," + this.rightShort() + "}";
    }
}

