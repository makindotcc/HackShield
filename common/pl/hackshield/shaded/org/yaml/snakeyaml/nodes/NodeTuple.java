/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.nodes;

import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Node;

public final class NodeTuple {
    private Node keyNode;
    private Node valueNode;

    public NodeTuple(Node keyNode, Node valueNode) {
        if (keyNode == null || valueNode == null) {
            throw new NullPointerException("Nodes must be provided.");
        }
        this.keyNode = keyNode;
        this.valueNode = valueNode;
    }

    public Node getKeyNode() {
        return this.keyNode;
    }

    public Node getValueNode() {
        return this.valueNode;
    }

    public String toString() {
        return "<NodeTuple keyNode=" + this.keyNode.toString() + "; valueNode=" + this.valueNode.toString() + ">";
    }
}

