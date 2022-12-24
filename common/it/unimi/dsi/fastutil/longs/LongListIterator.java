/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import java.util.ListIterator;

public interface LongListIterator
extends LongBidirectionalIterator,
ListIterator<Long> {
    @Override
    default public void set(long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public void set(Long k) {
        this.set((long)k);
    }

    @Override
    @Deprecated
    default public void add(Long k) {
        this.add((long)k);
    }

    @Override
    @Deprecated
    default public Long next() {
        return LongBidirectionalIterator.super.next();
    }

    @Override
    @Deprecated
    default public Long previous() {
        return LongBidirectionalIterator.super.previous();
    }
}

