/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface TargetType {
    public Class<?> value();
}

