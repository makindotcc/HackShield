/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.annotation;

import eu.okaeri.configs.annotation.Headers;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=Headers.class)
public @interface Header {
    public String[] value();

    public boolean raw() default false;
}

