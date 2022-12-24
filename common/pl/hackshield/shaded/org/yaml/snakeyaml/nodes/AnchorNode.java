/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.nodes;

import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Node;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.NodeId;

public class AnchorNode
extends Node {
    private Node realNode;

    public AnchorNode(Node realNode) {
        super(realNode.getTag(), realNode.getStartMark(), realNode.getEndMark());
        this.realNode = realNode;
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.anchor;
    }

    public Node getRealNode() {
        return this.realNode;
    }
}

