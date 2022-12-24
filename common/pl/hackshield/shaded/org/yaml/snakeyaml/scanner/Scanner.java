/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.scanner;

import pl.hackshield.shaded.org.yaml.snakeyaml.tokens.Token;

public interface Scanner {
    public boolean checkToken(Token.ID ... var1);

    public Token peekToken();

    public Token getToken();
}

