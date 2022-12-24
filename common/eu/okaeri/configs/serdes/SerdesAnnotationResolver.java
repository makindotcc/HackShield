/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.serdes;

import eu.okaeri.configs.serdes.SerdesContextAttachment;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;
import lombok.NonNull;

public interface SerdesAnnotationResolver<A extends Annotation, D extends SerdesContextAttachment> {
    public Class<A> getAnnotationType();

    public Optional<D> resolveAttachment(@NonNull Field var1, @NonNull A var2);
}

