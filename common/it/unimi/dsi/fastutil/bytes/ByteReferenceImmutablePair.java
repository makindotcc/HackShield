/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteReferencePair;
import java.io.Serializable;
import java.util.Objects;

public class ByteReferenceImmutablePair<V>
implements ByteReferencePair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final byte left;
    protected final V right;

    public ByteReferenceImmutablePair(byte left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> ByteReferenceImmutablePair<V> of(byte left, V right) {
        return new ByteReferenceImmutablePair<V>(left, right);
    }

    @Override
    public byte leftByte() {
        return this.left;
    }

    @Override
    public V right() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ByteReferencePair) {
            return this.left == ((ByteReferencePair)other).leftByte() && this.right == ((ByteReferencePair)other).right();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
    }

    public String toString() {
        return "<" + this.leftByte() + "," + this.right() + ">";
    }
}

