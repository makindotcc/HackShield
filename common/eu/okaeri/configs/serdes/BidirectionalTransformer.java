/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.SerdesContext;
import lombok.NonNull;

public abstract class BidirectionalTransformer<L, R> {
    public abstract GenericsPair<L, R> getPair();

    public abstract R leftToRight(@NonNull L var1, @NonNull SerdesContext var2);

    public abstract L rightToLeft(@NonNull R var1, @NonNull SerdesContext var2);

    protected GenericsPair<L, R> generics(@NonNull GenericsDeclaration from, @NonNull GenericsDeclaration to) {
        if (from == null) {
            throw new NullPointerException("from is marked non-null but is null");
        }
        if (to == null) {
            throw new NullPointerException("to is marked non-null but is null");
        }
        return new GenericsPair(from, to);
    }

    protected GenericsPair<L, R> genericsPair(@NonNull Class<L> from, @NonNull Class<R> to) {
        if (from == null) {
            throw new NullPointerException("from is marked non-null but is null");
        }
        if (to == null) {
            throw new NullPointerException("to is marked non-null but is null");
        }
        return new GenericsPair(GenericsDeclaration.of(from), GenericsDeclaration.of(to));
    }
}

