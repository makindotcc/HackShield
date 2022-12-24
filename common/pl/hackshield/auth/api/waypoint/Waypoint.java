/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.waypoint;

import java.awt.Color;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import pl.hackshield.auth.api.waypoint.WaypointOptions;

public class Waypoint
implements Serializable {
    private final UUID uniqueId;
    private UUID iconId;
    private int color;
    private Component name;
    private double x;
    private double y;
    private double z;
    private WaypointOptions options;

    public Waypoint(Component name, Color color, double x, double y, double z) {
        this(name, color.getRGB(), x, y, z);
    }

    public Waypoint(Component name, int color, double x, double y, double z) {
        this(UUID.randomUUID(), null, name, color, x, y, z);
    }

    public Waypoint(UUID iconId, Component name, int color, double x, double y, double z) {
        this(UUID.randomUUID(), iconId, name, color, x, y, z);
    }

    public Waypoint(UUID uniqueId, UUID iconId, Component name, int color, double x, double y, double z) {
        this(uniqueId, iconId, name, color, x, y, z, new WaypointOptions());
    }

    public Waypoint(UUID uniqueId, UUID iconId, Component name, int color, double x, double y, double z, WaypointOptions options) {
        this.uniqueId = uniqueId;
        this.iconId = iconId;
        this.name = name;
        this.color = color;
        this.x = x;
        this.y = y;
        this.z = z;
        this.options = options;
    }

    public Waypoint setOptions(WaypointOptions options) {
        this.options = options;
        return this;
    }

    public Waypoint setIconId(UUID iconId) {
        this.iconId = iconId;
        return this;
    }

    public boolean hasDisplayName() {
        return Objects.nonNull(this.name);
    }

    public Waypoint setColor(int color) {
        this.color = color;
        return this;
    }

    public Waypoint setColor(Color color) {
        this.color = color.getRGB();
        return this;
    }

    public Waypoint setName(Component name) {
        this.name = name;
        return this;
    }

    public Waypoint setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public UUID getIconId() {
        return this.iconId;
    }

    public int getColor() {
        return this.color;
    }

    public Component getName() {
        return this.name;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public WaypointOptions getOptions() {
        return this.options;
    }
}

