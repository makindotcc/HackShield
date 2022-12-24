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

public abstract class ObjectTransformer<S, D> {
    public abstract GenericsPair<S, D> getPair();

    public abstract D transform(S var1, SerdesContext var2);

    protected GenericsPair<S, D> genericsPair(@NonNull Class<S> from, @NonNull Class<D> to) {
        if (from == null) {
            throw new NullPointerException("from is marked non-null but is null");
        }
        if (to == null) {
            throw new NullPointerException("to is marked non-null but is null");
        }
        return new GenericsPair(GenericsDeclaration.of(from), GenericsDeclaration.of(to));
    }
}

