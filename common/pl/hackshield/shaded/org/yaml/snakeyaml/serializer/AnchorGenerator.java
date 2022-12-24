/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.serializer;

import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Node;

public interface AnchorGenerator {
    public String nextAnchor(Node var1);
}

