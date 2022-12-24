/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;

public interface ObjectSerializer<T> {
    public boolean supports(@NonNull Class<? super T> var1);

    public void serialize(@NonNull T var1, @NonNull SerializationData var2, @NonNull GenericsDeclaration var3);

    public T deserialize(@NonNull DeserializationData var1, @NonNull GenericsDeclaration var2);
}

