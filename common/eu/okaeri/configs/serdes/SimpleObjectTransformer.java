/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.ObjectTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import eu.okaeri.configs.serdes.SimpleObjectTransformerExecutor;
import lombok.NonNull;

public abstract class SimpleObjectTransformer {
    private SimpleObjectTransformer() {
    }

    public static <S, D> ObjectTransformer<S, D> of(final @NonNull Class<S> from, final @NonNull Class<D> to, final @NonNull SimpleObjectTransformerExecutor<S, D> transformer) {
        if (from == null) {
            throw new NullPointerException("from is marked non-null but is null");
        }
        if (to == null) {
            throw new NullPointerException("to is marked non-null but is null");
        }
        if (transformer == null) {
            throw new NullPointerException("transformer is marked non-null but is null");
        }
        return new ObjectTransformer<S, D>(){

            @Override
            public GenericsPair<S, D> getPair() {
                return this.genericsPair(from, to);
            }

            @Override
            public D transform(@NonNull S data, @NonNull SerdesContext serdesContext) {
                if (data == null) {
                    throw new NullPointerException("data is marked non-null but is null");
                }
                if (serdesContext == null) {
                    throw new NullPointerException("serdesContext is marked non-null but is null");
                }
                return transformer.transform(data);
            }
        };
    }
}

