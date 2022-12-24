/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;

public interface DoublePriorityQueue
extends PriorityQueue<Double> {
    @Override
    public void enqueue(double var1);

    public double dequeueDouble();

    public double firstDouble();

    default public double lastDouble() {
        throw new UnsupportedOperationException();
    }

    public DoubleComparator comparator();

    @Override
    @Deprecated
    default public void enqueue(Double x) {
        this.enqueue((double)x);
    }

    @Override
    @Deprecated
    default public Double dequeue() {
        return this.dequeueDouble();
    }

    @Override
    @Deprecated
    default public Double first() {
        return this.firstDouble();
    }

    @Override
    @Deprecated
    default public Double last() {
        return this.lastDouble();
    }
}

