/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;
import it.unimi.dsi.fastutil.floats.FloatComparator;

public interface FloatIndirectPriorityQueue
extends IndirectPriorityQueue<Float> {
    public FloatComparator comparator();
}

