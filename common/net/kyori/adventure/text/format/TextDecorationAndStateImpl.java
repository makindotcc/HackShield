/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.format;

import java.util.Objects;
import net.kyori.adventure.internal.Internals;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecorationAndState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TextDecorationAndStateImpl
implements TextDecorationAndState {
    private final TextDecoration decoration;
    private final TextDecoration.State state;

    TextDecorationAndStateImpl(TextDecoration decoration, TextDecoration.State state) {
        this.decoration = decoration;
        this.state = Objects.requireNonNull(state, "state");
    }

    @Override
    @NotNull
    public TextDecoration decoration() {
        return this.decoration;
    }

    @Override
    public @NotNull TextDecoration.State state() {
        return this.state;
    }

    public String toString() {
        return Internals.toString(this);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        TextDecorationAndStateImpl that = (TextDecorationAndStateImpl)other;
        return this.decoration == that.decoration && this.state == that.state;
    }

    public int hashCode() {
        int result = this.decoration.hashCode();
        result = 31 * result + this.state.hashCode();
        return result;
    }
}

