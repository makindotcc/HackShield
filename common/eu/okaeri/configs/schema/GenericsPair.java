/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.schema;

import eu.okaeri.configs.schema.GenericsDeclaration;

public class GenericsPair<L, R> {
    private GenericsDeclaration from;
    private GenericsDeclaration to;

    public GenericsPair<R, L> reverse() {
        return new GenericsPair<L, R>(this.to, this.from);
    }

    public GenericsDeclaration getFrom() {
        return this.from;
    }

    public GenericsDeclaration getTo() {
        return this.to;
    }

    public void setFrom(GenericsDeclaration from) {
        this.from = from;
    }

    public void setTo(GenericsDeclaration to) {
        this.to = to;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GenericsPair)) {
            return false;
        }
        GenericsPair other = (GenericsPair)o;
        if (!other.canEqual(this)) {
            return false;
        }
        GenericsDeclaration this$from = this.getFrom();
        GenericsDeclaration other$from = other.getFrom();
        if (this$from == null ? other$from != null : !((Object)this$from).equals(other$from)) {
            return false;
        }
        GenericsDeclaration this$to = this.getTo();
        GenericsDeclaration other$to = other.getTo();
        return !(this$to == null ? other$to != null : !((Object)this$to).equals(other$to));
    }

    protected boolean canEqual(Object other) {
        return other instanceof GenericsPair;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        GenericsDeclaration $from = this.getFrom();
        result = result * 59 + ($from == null ? 43 : ((Object)$from).hashCode());
        GenericsDeclaration $to = this.getTo();
        result = result * 59 + ($to == null ? 43 : ((Object)$to).hashCode());
        return result;
    }

    public String toString() {
        return "GenericsPair(from=" + this.getFrom() + ", to=" + this.getTo() + ")";
    }

    public GenericsPair(GenericsDeclaration from, GenericsDeclaration to) {
        this.from = from;
        this.to = to;
    }
}

