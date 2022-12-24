/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.constructor;

import pl.hackshield.shaded.org.yaml.snakeyaml.constructor.Construct;
import pl.hackshield.shaded.org.yaml.snakeyaml.error.YAMLException;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Node;

public abstract class AbstractConstruct
implements Construct {
    @Override
    public void construct2ndStep(Node node, Object data) {
        if (node.isTwoStepsConstruction()) {
            throw new IllegalStateException("Not Implemented in " + this.getClass().getName());
        }
        throw new YAMLException("Unexpected recursive structure for Node: " + node);
    }
}

