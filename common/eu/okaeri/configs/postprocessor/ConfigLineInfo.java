/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.postprocessor;

import lombok.NonNull;

public class ConfigLineInfo {
    private int indent;
    private int change;
    private String name;

    public static ConfigLineInfo of(int indent, int change, @NonNull String name) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        }
        ConfigLineInfo info = new ConfigLineInfo();
        info.indent = indent;
        info.change = change;
        info.name = name;
        return info;
    }

    public int getIndent() {
        return this.indent;
    }

    public int getChange() {
        return this.change;
    }

    public String getName() {
        return this.name;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConfigLineInfo)) {
            return false;
        }
        ConfigLineInfo other = (ConfigLineInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getIndent() != other.getIndent()) {
            return false;
        }
        if (this.getChange() != other.getChange()) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        return !(this$name == null ? other$name != null : !this$name.equals(other$name));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConfigLineInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getIndent();
        result = result * 59 + this.getChange();
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    public String toString() {
        return "ConfigLineInfo(indent=" + this.getIndent() + ", change=" + this.getChange() + ", name=" + this.getName() + ")";
    }
}

