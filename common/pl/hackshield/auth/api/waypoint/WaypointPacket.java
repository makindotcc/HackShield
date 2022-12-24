/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.waypoint;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.waypoint.Waypoint;

public final class WaypointPacket
implements HsPacket,
Serializable {
    private final Operation operation;
    private Collection<Waypoint> data;

    public WaypointPacket(Operation operation, Waypoint waypoint) {
        this.operation = operation;
        this.data = Collections.singleton(waypoint);
    }

    public WaypointPacket(Operation operation, Collection<Waypoint> waypoints) {
        this.operation = operation;
        this.data = waypoints;
    }

    public WaypointPacket() {
        this.operation = Operation.REMOVE_ALL;
    }

    public Operation getOperation() {
        return this.operation;
    }

    public Collection<Waypoint> getData() {
        return this.data;
    }

    public static enum Operation implements Serializable
    {
        ADD,
        REMOVE,
        REMOVE_ALL,
        UPDATE_PROPERTIES,
        UPDATE_LOCATION;

    }
}

