/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pl.hackshield.shaded.org.yaml.snakeyaml.DumperOptions;
import pl.hackshield.shaded.org.yaml.snakeyaml.emitter.Emitable;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.AliasEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.DocumentEndEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.DocumentStartEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.ImplicitTuple;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.MappingEndEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.MappingStartEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.ScalarEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.SequenceEndEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.SequenceStartEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.StreamEndEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.StreamStartEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.AnchorNode;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.CollectionNode;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.MappingNode;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Node;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.NodeId;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.NodeTuple;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.ScalarNode;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.SequenceNode;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Tag;
import pl.hackshield.shaded.org.yaml.snakeyaml.resolver.Resolver;
import pl.hackshield.shaded.org.yaml.snakeyaml.serializer.AnchorGenerator;
import pl.hackshield.shaded.org.yaml.snakeyaml.serializer.SerializerException;

public final class Serializer {
    private final Emitable emitter;
    private final Resolver resolver;
    private boolean explicitStart;
    private boolean explicitEnd;
    private DumperOptions.Version useVersion;
    private Map<String, String> useTags;
    private Set<Node> serializedNodes;
    private Map<Node, String> anchors;
    private AnchorGenerator anchorGenerator;
    private Boolean closed;
    private Tag explicitRoot;

    public Serializer(Emitable emitter, Resolver resolver, DumperOptions opts, Tag rootTag) {
        this.emitter = emitter;
        this.resolver = resolver;
        this.explicitStart = opts.isExplicitStart();
        this.explicitEnd = opts.isExplicitEnd();
        if (opts.getVersion() != null) {
            this.useVersion = opts.getVersion();
        }
        this.useTags = opts.getTags();
        this.serializedNodes = new HashSet<Node>();
        this.anchors = new HashMap<Node, String>();
        this.anchorGenerator = opts.getAnchorGenerator();
        this.closed = null;
        this.explicitRoot = rootTag;
    }

    public void open() throws IOException {
        if (this.closed != null) {
            if (Boolean.TRUE.equals(this.closed)) {
                throw new SerializerException("serializer is closed");
            }
            throw new SerializerException("serializer is already opened");
        }
        this.emitter.emit(new StreamStartEvent(null, null));
        this.closed = Boolean.FALSE;
    }

    public void close() throws IOException {
        if (this.closed == null) {
            throw new SerializerException("serializer is not opened");
        }
        if (!Boolean.TRUE.equals(this.closed)) {
            this.emitter.emit(new StreamEndEvent(null, null));
            this.closed = Boolean.TRUE;
            this.serializedNodes.clear();
            this.anchors.clear();
        }
    }

    public void serialize(Node node) throws IOException {
        if (this.closed == null) {
            throw new SerializerException("serializer is not opened");
        }
        if (this.closed.booleanValue()) {
            throw new SerializerException("serializer is closed");
        }
        this.emitter.emit(new DocumentStartEvent(null, null, this.explicitStart, this.useVersion, this.useTags));
        this.anchorNode(node);
        if (this.explicitRoot != null) {
            node.setTag(this.explicitRoot);
        }
        this.serializeNode(node, null);
        this.emitter.emit(new DocumentEndEvent(null, null, this.explicitEnd));
        this.serializedNodes.clear();
        this.anchors.clear();
    }

    private void anchorNode(Node node) {
        if (node.getNodeId() == NodeId.anchor) {
            node = ((AnchorNode)node).getRealNode();
        }
        if (this.anchors.containsKey(node)) {
            String anchor = this.anchors.get(node);
            if (null == anchor) {
                anchor = this.anchorGenerator.nextAnchor(node);
                this.anchors.put(node, anchor);
            }
        } else {
            this.anchors.put(node, null);
            switch (node.getNodeId()) {
                case sequence: {
                    SequenceNode seqNode = (SequenceNode)node;
                    List<Node> list = seqNode.getValue();
                    for (Node item : list) {
                        this.anchorNode(item);
                    }
                    break;
                }
                case mapping: {
                    MappingNode mnode = (MappingNode)node;
                    List<NodeTuple> map = mnode.getValue();
                    for (NodeTuple object : map) {
                        Node key = object.getKeyNode();
                        Node value = object.getValueNode();
                        this.anchorNode(key);
                        this.anchorNode(value);
                    }
                    break;
                }
            }
        }
    }

    private void serializeNode(Node node, Node parent) throws IOException {
        if (node.getNodeId() == NodeId.anchor) {
            node = ((AnchorNode)node).getRealNode();
        }
        String tAlias = this.anchors.get(node);
        if (this.serializedNodes.contains(node)) {
            this.emitter.emit(new AliasEvent(tAlias, null, null));
        } else {
            this.serializedNodes.add(node);
            switch (node.getNodeId()) {
                case scalar: {
                    ScalarNode scalarNode = (ScalarNode)node;
                    Tag detectedTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), true);
                    Tag defaultTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), false);
                    ImplicitTuple tuple = new ImplicitTuple(node.getTag().equals(detectedTag), node.getTag().equals(defaultTag));
                    ScalarEvent event = new ScalarEvent(tAlias, node.getTag().getValue(), tuple, scalarNode.getValue(), null, null, scalarNode.getScalarStyle());
                    this.emitter.emit(event);
                    break;
                }
                case sequence: {
                    SequenceNode seqNode = (SequenceNode)node;
                    boolean implicitS = node.getTag().equals(this.resolver.resolve(NodeId.sequence, null, true));
                    this.emitter.emit(new SequenceStartEvent(tAlias, node.getTag().getValue(), implicitS, null, null, seqNode.getFlowStyle()));
                    List<Node> list = seqNode.getValue();
                    for (Node item : list) {
                        this.serializeNode(item, node);
                    }
                    this.emitter.emit(new SequenceEndEvent(null, null));
                    break;
                }
                default: {
                    Tag implicitTag = this.resolver.resolve(NodeId.mapping, null, true);
                    boolean implicitM = node.getTag().equals(implicitTag);
                    this.emitter.emit(new MappingStartEvent(tAlias, node.getTag().getValue(), implicitM, null, null, ((CollectionNode)node).getFlowStyle()));
                    MappingNode mnode = (MappingNode)node;
                    List<NodeTuple> map = mnode.getValue();
                    for (NodeTuple row : map) {
                        Node key = row.getKeyNode();
                        Node value = row.getValueNode();
                        this.serializeNode(key, mnode);
                        this.serializeNode(value, mnode);
                    }
                    this.emitter.emit(new MappingEndEvent(null, null));
                }
            }
        }
    }
}

