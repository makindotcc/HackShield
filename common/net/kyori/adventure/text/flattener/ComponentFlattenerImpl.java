/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.flattener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ComponentFlattenerImpl
implements ComponentFlattener {
    static final ComponentFlattener BASIC = (ComponentFlattener)new BuilderImpl().mapper(KeybindComponent.class, component -> component.keybind()).mapper(ScoreComponent.class, ScoreComponent::value).mapper(SelectorComponent.class, SelectorComponent::pattern).mapper(TextComponent.class, TextComponent::content).mapper(TranslatableComponent.class, TranslatableComponent::key).build();
    static final ComponentFlattener TEXT_ONLY = (ComponentFlattener)new BuilderImpl().mapper(TextComponent.class, TextComponent::content).build();
    private static final int MAX_DEPTH = 512;
    private final Map<Class<?>, Function<?, String>> flatteners;
    private final Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners;
    private final ConcurrentMap<Class<?>, Handler> propagatedFlatteners = new ConcurrentHashMap();
    private final Function<Component, String> unknownHandler;

    ComponentFlattenerImpl(Map<Class<?>, Function<?, String>> flatteners, Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners, @Nullable Function<Component, String> unknownHandler) {
        this.flatteners = Collections.unmodifiableMap(new HashMap(flatteners));
        this.complexFlatteners = Collections.unmodifiableMap(new HashMap(complexFlatteners));
        this.unknownHandler = unknownHandler;
    }

    @Override
    public void flatten(@NotNull Component input, @NotNull FlattenerListener listener) {
        this.flatten0(input, listener, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void flatten0(@NotNull Component input, @NotNull FlattenerListener listener, int depth) {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(listener, "listener");
        if (input == Component.empty()) {
            return;
        }
        if (depth > 512) {
            throw new IllegalStateException("Exceeded maximum depth of 512 while attempting to flatten components!");
        }
        @Nullable Handler flattener = this.flattener(input);
        Style inputStyle = input.style();
        listener.pushStyle(inputStyle);
        try {
            if (flattener != null) {
                flattener.handle(input, listener, depth + 1);
            }
            if (!input.children().isEmpty()) {
                for (Component child : input.children()) {
                    this.flatten0(child, listener, depth + 1);
                }
            }
        }
        finally {
            listener.popStyle(inputStyle);
        }
    }

    @Nullable
    private <T extends Component> Handler flattener(T test) {
        Handler flattener = this.propagatedFlatteners.computeIfAbsent(test.getClass(), key -> {
            @Nullable Function<?, String> value = this.flatteners.get(key);
            if (value != null) {
                return (component, listener, depth) -> listener.component((String)value.apply(component));
            }
            for (Map.Entry<Class<?>, Function<?, String>> entry : this.flatteners.entrySet()) {
                if (!entry.getKey().isAssignableFrom((Class<?>)key)) continue;
                return (component, listener, depth) -> listener.component((String)((Function)entry.getValue()).apply(component));
            }
            @Nullable BiConsumer<?, Consumer<Component>> complexValue = this.complexFlatteners.get(key);
            if (complexValue != null) {
                return (component, listener, depth) -> complexValue.accept(component, c -> this.flatten0((Component)c, listener, depth));
            }
            for (Map.Entry<Class<?>, BiConsumer<?, Consumer<Component>>> entry : this.complexFlatteners.entrySet()) {
                if (!entry.getKey().isAssignableFrom((Class<?>)key)) continue;
                return (component, listener, depth) -> ((BiConsumer)entry.getValue()).accept(component, c -> this.flatten0((Component)c, listener, depth));
            }
            return Handler.NONE;
        });
        if (flattener == Handler.NONE) {
            return this.unknownHandler == null ? null : (component, listener, depth) -> listener.component(this.unknownHandler.apply(component));
        }
        return flattener;
    }

    @Override
    public @NotNull ComponentFlattener.Builder toBuilder() {
        return new BuilderImpl(this.flatteners, this.complexFlatteners, this.unknownHandler);
    }

    static final class BuilderImpl
    implements ComponentFlattener.Builder {
        private final Map<Class<?>, Function<?, String>> flatteners;
        private final Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners;
        @Nullable
        private Function<Component, String> unknownHandler;

        BuilderImpl() {
            this.flatteners = new HashMap();
            this.complexFlatteners = new HashMap();
        }

        BuilderImpl(Map<Class<?>, Function<?, String>> flatteners, Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners, @Nullable Function<Component, String> unknownHandler) {
            this.flatteners = new HashMap(flatteners);
            this.complexFlatteners = new HashMap(complexFlatteners);
            this.unknownHandler = unknownHandler;
        }

        @Override
        @NotNull
        public ComponentFlattener build() {
            return new ComponentFlattenerImpl(this.flatteners, this.complexFlatteners, this.unknownHandler);
        }

        @Override
        public <T extends Component> @NotNull ComponentFlattener.Builder mapper(@NotNull Class<T> type, @NotNull Function<T, String> converter) {
            this.validateNoneInHierarchy(Objects.requireNonNull(type, "type"));
            this.flatteners.put(type, Objects.requireNonNull(converter, "converter"));
            this.complexFlatteners.remove(type);
            return this;
        }

        @Override
        public <T extends Component> @NotNull ComponentFlattener.Builder complexMapper(@NotNull Class<T> type, @NotNull BiConsumer<T, Consumer<Component>> converter) {
            this.validateNoneInHierarchy(Objects.requireNonNull(type, "type"));
            this.complexFlatteners.put(type, Objects.requireNonNull(converter, "converter"));
            this.flatteners.remove(type);
            return this;
        }

        private void validateNoneInHierarchy(Class<? extends Component> beingRegistered) {
            for (Class<?> clazz : this.flatteners.keySet()) {
                BuilderImpl.testHierarchy(clazz, beingRegistered);
            }
            for (Class<?> clazz : this.complexFlatteners.keySet()) {
                BuilderImpl.testHierarchy(clazz, beingRegistered);
            }
        }

        private static void testHierarchy(Class<?> existing, Class<?> beingRegistered) {
            if (!existing.equals(beingRegistered) && (existing.isAssignableFrom(beingRegistered) || beingRegistered.isAssignableFrom(existing))) {
                throw new IllegalArgumentException("Conflict detected between already registered type " + existing + " and newly registered type " + beingRegistered + "! Types in a component flattener must not share a common hierarchy!");
            }
        }

        @Override
        public @NotNull ComponentFlattener.Builder unknownMapper(@Nullable Function<Component, String> converter) {
            this.unknownHandler = converter;
            return this;
        }
    }

    @FunctionalInterface
    static interface Handler {
        public static final Handler NONE = (input, listener, depth) -> {};

        public void handle(Component var1, FlattenerListener var2, int var3);
    }
}

