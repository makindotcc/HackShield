/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.annotation;

import eu.okaeri.configs.annotation.VariableMode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Variable {
    public String value();

    public VariableMode mode() default VariableMode.RUNTIME;
}

