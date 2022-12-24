/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.annotation;

import eu.okaeri.configs.annotation.Comments;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=Comments.class)
public @interface Comment {
    public String[] value() default {""};
}

