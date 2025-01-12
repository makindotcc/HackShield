/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.nodes;

import java.util.List;
import pl.hackshield.shaded.org.yaml.snakeyaml.DumperOptions;
import pl.hackshield.shaded.org.yaml.snakeyaml.error.Mark;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.CollectionNode;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Node;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.NodeId;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Tag;

public class SequenceNode
extends CollectionNode<Node> {
    private final List<Node> value;

    public SequenceNode(Tag tag, boolean resolved, List<Node> value, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
        super(tag, startMark, endMark, flowStyle);
        if (value == null) {
            throw new NullPointerException("value in a Node is required.");
        }
        this.value = value;
        this.resolved = resolved;
    }

    public SequenceNode(Tag tag, List<Node> value, DumperOptions.FlowStyle flowStyle) {
        this(tag, true, value, null, null, flowStyle);
    }

    @Deprecated
    public SequenceNode(Tag tag, List<Node> value, Boolean style) {
        this(tag, value, DumperOptions.FlowStyle.fromBoolean(style));
    }

    @Deprecated
    public SequenceNode(Tag tag, boolean resolved, List<Node> value, Mark startMark, Mark endMark, Boolean style) {
        this(tag, resolved, value, startMark, endMark, DumperOptions.FlowStyle.fromBoolean(style));
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.sequence;
    }

    @Override
    public List<Node> getValue() {
        return this.value;
    }

    public void setListType(Class<? extends Object> listType) {
        for (Node node : this.value) {
            node.setType(listType);
        }
    }

    public String toString() {
        return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=" + this.getValue() + ")>";
    }
}

