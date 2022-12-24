/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.util.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import pl.hackshield.auth.common.util.config.Comment;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface Comments {
    public Comment[] value();
}

