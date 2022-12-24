/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.internal.Internals;
import net.kyori.adventure.text.AbstractComponent;
import net.kyori.adventure.text.AbstractComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TranslatableComponentImpl
extends AbstractComponent
implements TranslatableComponent {
    private final String key;
    private final List<Component> args;

    static TranslatableComponent create(@NotNull List<Component> children, @NotNull Style style, @NotNull String key, @NotNull @NotNull ComponentLike @NotNull [] args) {
        Objects.requireNonNull(args, "args");
        return TranslatableComponentImpl.create(children, style, key, Arrays.asList(args));
    }

    static TranslatableComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String key, @NotNull List<? extends ComponentLike> args) {
        return new TranslatableComponentImpl(ComponentLike.asComponents(children, IS_NOT_EMPTY), Objects.requireNonNull(style, "style"), Objects.requireNonNull(key, "key"), ComponentLike.asComponents(args));
    }

    TranslatableComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String key, @NotNull List<Component> args) {
        super(children, style);
        this.key = key;
        this.args = args;
    }

    @Override
    @NotNull
    public String key() {
        return this.key;
    }

    @Override
    @NotNull
    public TranslatableComponent key(@NotNull String key) {
        if (Objects.equals(this.key, key)) {
            return this;
        }
        return TranslatableComponentImpl.create(this.children, this.style, key, this.args);
    }

    @Override
    @NotNull
    public List<Component> args() {
        return this.args;
    }

    @Override
    @NotNull
    public TranslatableComponent args(ComponentLike ... args) {
        return TranslatableComponentImpl.create(this.children, this.style, this.key, args);
    }

    @Override
    @NotNull
    public TranslatableComponent args(@NotNull List<? extends ComponentLike> args) {
        return TranslatableComponentImpl.create(this.children, this.style, this.key, args);
    }

    @Override
    @NotNull
    public TranslatableComponent children(@NotNull List<? extends ComponentLike> children) {
        return TranslatableComponentImpl.create(children, this.style, this.key, this.args);
    }

    @Override
    @NotNull
    public TranslatableComponent style(@NotNull Style style) {
        return TranslatableComponentImpl.create(this.children, style, this.key, this.args);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TranslatableComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        TranslatableComponent that = (TranslatableComponent)other;
        return Objects.equals(this.key, that.key()) && Objects.equals(this.args, that.args());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.key.hashCode();
        result = 31 * result + this.args.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Internals.toString(this);
    }

    @Override
    @NotNull
    public TranslatableComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl
    extends AbstractComponentBuilder<TranslatableComponent, TranslatableComponent.Builder>
    implements TranslatableComponent.Builder {
        @Nullable
        private String key;
        private List<? extends Component> args = Collections.emptyList();

        BuilderImpl() {
        }

        BuilderImpl(@NotNull TranslatableComponent component) {
            super(component);
            this.key = component.key();
            this.args = component.args();
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder key(@NotNull String key) {
            this.key = key;
            return this;
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder args(@NotNull ComponentBuilder<?, ?> arg) {
            return this.args(Collections.singletonList(Objects.requireNonNull(arg, "arg").build()));
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder args(ComponentBuilder<?, ?> ... args) {
            Objects.requireNonNull(args, "args");
            if (args.length == 0) {
                return this.args(Collections.emptyList());
            }
            return this.args(Stream.of(args).map(ComponentBuilder::build).collect(Collectors.toList()));
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder args(@NotNull Component arg) {
            return this.args(Collections.singletonList(Objects.requireNonNull(arg, "arg")));
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder args(ComponentLike ... args) {
            Objects.requireNonNull(args, "args");
            if (args.length == 0) {
                return this.args(Collections.emptyList());
            }
            return this.args(Arrays.asList(args));
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder args(@NotNull List<? extends ComponentLike> args) {
            this.args = ComponentLike.asComponents(Objects.requireNonNull(args, "args"));
            return this;
        }

        @Override
        @NotNull
        public TranslatableComponent build() {
            if (this.key == null) {
                throw new IllegalStateException("key must be set");
            }
            return TranslatableComponentImpl.create(this.children, this.buildStyle(), this.key, this.args);
        }
    }
}

