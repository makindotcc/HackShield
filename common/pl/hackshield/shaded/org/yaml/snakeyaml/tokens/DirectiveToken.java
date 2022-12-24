/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.tokens;

import java.util.List;
import pl.hackshield.shaded.org.yaml.snakeyaml.error.Mark;
import pl.hackshield.shaded.org.yaml.snakeyaml.error.YAMLException;
import pl.hackshield.shaded.org.yaml.snakeyaml.tokens.Token;

public final class DirectiveToken<T>
extends Token {
    private final String name;
    private final List<T> value;

    public DirectiveToken(String name, List<T> value, Mark startMark, Mark endMark) {
        super(startMark, endMark);
        this.name = name;
        if (value != null && value.size() != 2) {
            throw new YAMLException("Two strings must be provided instead of " + String.valueOf(value.size()));
        }
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public List<T> getValue() {
        return this.value;
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Directive;
    }
}

