/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectLongPair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectLongImmutablePair<K>
implements ObjectLongPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final long right;

    public ObjectLongImmutablePair(K left, long right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ObjectLongImmutablePair<K> of(K left, long right) {
        return new ObjectLongImmutablePair<K>(left, right);
    }

    @Override
    public K left() {
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
        if (other instanceof ObjectLongPair) {
            return Objects.equals(this.left, ((ObjectLongPair)other).left()) && this.right == ((ObjectLongPair)other).rightLong();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left == null ? 0 : this.left.hashCode()) * 19 + HashCommon.long2int(this.right);
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightLong() + ">";
    }
}

