/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.nodes;

import java.util.List;
import pl.hackshield.shaded.org.yaml.snakeyaml.DumperOptions;
import pl.hackshield.shaded.org.yaml.snakeyaml.error.Mark;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Node;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Tag;

public abstract class CollectionNode<T>
extends Node {
    private DumperOptions.FlowStyle flowStyle;

    public CollectionNode(Tag tag, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
        super(tag, startMark, endMark);
        this.setFlowStyle(flowStyle);
    }

    @Deprecated
    public CollectionNode(Tag tag, Mark startMark, Mark endMark, Boolean flowStyle) {
        this(tag, startMark, endMark, DumperOptions.FlowStyle.fromBoolean(flowStyle));
    }

    public abstract List<T> getValue();

    public DumperOptions.FlowStyle getFlowStyle() {
        return this.flowStyle;
    }

    public void setFlowStyle(DumperOptions.FlowStyle flowStyle) {
        if (flowStyle == null) {
            throw new NullPointerException("Flow style must be provided.");
        }
        this.flowStyle = flowStyle;
    }

    @Deprecated
    public void setFlowStyle(Boolean flowStyle) {
        this.setFlowStyle(DumperOptions.FlowStyle.fromBoolean(flowStyle));
    }

    public void setEndMark(Mark endMark) {
        this.endMark = endMark;
    }
}

