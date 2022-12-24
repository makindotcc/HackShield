/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.tokens;

import pl.hackshield.shaded.org.yaml.snakeyaml.error.Mark;
import pl.hackshield.shaded.org.yaml.snakeyaml.tokens.Token;

public final class DocumentEndToken
extends Token {
    public DocumentEndToken(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.DocumentEnd;
    }
}

