/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.tokens;

import pl.hackshield.shaded.org.yaml.snakeyaml.error.Mark;
import pl.hackshield.shaded.org.yaml.snakeyaml.tokens.Token;

public final class AnchorToken
extends Token {
    private final String value;

    public AnchorToken(String value, Mark startMark, Mark endMark) {
        super(startMark, endMark);
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Anchor;
    }
}

