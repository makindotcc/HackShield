/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSet;
import java.util.Set;

public abstract class AbstractBooleanSet
extends AbstractBooleanCollection
implements Cloneable,
BooleanSet {
    protected AbstractBooleanSet() {
    }

    @Override
    public abstract BooleanIterator iterator();

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        Set s = (Set)o;
        if (s.size() != this.size()) {
            return false;
        }
        if (s instanceof BooleanSet) {
            return this.containsAll((BooleanSet)s);
        }
        return this.containsAll(s);
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        BooleanIterator i = this.iterator();
        while (n-- != 0) {
            boolean k = i.nextBoolean();
            h += k ? 1231 : 1237;
        }
        return h;
    }

    @Override
    public boolean remove(boolean k) {
        return super.rem(k);
    }

    @Override
    @Deprecated
    public boolean rem(boolean k) {
        return this.remove(k);
    }
}

