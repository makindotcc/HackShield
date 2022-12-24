/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.annotation;

import java.util.regex.Pattern;

public final class NameStrategy
extends Enum<NameStrategy> {
    public static final /* enum */ NameStrategy IDENTITY = new NameStrategy("", "");
    public static final /* enum */ NameStrategy SNAKE_CASE = new NameStrategy("$1_$2", "(\\G(?!^)|\\b(?:[A-Z]{2}|[a-zA-Z][a-z]*))(?=[a-zA-Z]{2,}|\\d)([A-Z](?:[A-Z]|[a-z]*)|\\d+)");
    public static final /* enum */ NameStrategy HYPHEN_CASE = new NameStrategy("$1-$2", "(\\G(?!^)|\\b(?:[A-Z]{2}|[a-zA-Z][a-z]*))(?=[a-zA-Z]{2,}|\\d)([A-Z](?:[A-Z]|[a-z]*)|\\d+)");
    private final String replacement;
    private final Pattern regex;
    private static final /* synthetic */ NameStrategy[] $VALUES;

    public static NameStrategy[] values() {
        return (NameStrategy[])$VALUES.clone();
    }

    public static NameStrategy valueOf(String name) {
        return Enum.valueOf(NameStrategy.class, name);
    }

    private NameStrategy(String replacement, String regex) {
        this.replacement = replacement;
        this.regex = Pattern.compile(regex);
    }

    public String getReplacement() {
        return this.replacement;
    }

    public Pattern getRegex() {
        return this.regex;
    }

    private static /* synthetic */ NameStrategy[] $values() {
        return new NameStrategy[]{IDENTITY, SNAKE_CASE, HYPHEN_CASE};
    }

    static {
        $VALUES = NameStrategy.$values();
    }
}

