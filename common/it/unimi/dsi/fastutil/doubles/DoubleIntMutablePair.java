/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleIntPair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleIntMutablePair
implements DoubleIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected double left;
    protected int right;

    public DoubleIntMutablePair(double left, int right) {
        this.left = left;
        this.right = right;
    }

    public static DoubleIntMutablePair of(double left, int right) {
        return new DoubleIntMutablePair(left, right);
    }

    @Override
    public double leftDouble() {
        return this.left;
    }

    @Override
    public DoubleIntMutablePair left(double l) {
        this.left = l;
        return this;
    }

    @Override
    public int rightInt() {
        return this.right;
    }

    @Override
    public DoubleIntMutablePair right(int r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof DoubleIntPair) {
            return this.left == ((DoubleIntPair)other).leftDouble() && this.right == ((DoubleIntPair)other).rightInt();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.double2int(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftDouble() + "," + this.rightInt() + ">";
    }
}

