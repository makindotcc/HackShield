/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ComponentLike {
    @NotNull
    public static List<Component> asComponents(@NotNull List<? extends ComponentLike> likes) {
        return ComponentLike.asComponents(likes, null);
    }

    @NotNull
    public static List<Component> asComponents(@NotNull List<? extends ComponentLike> likes, @Nullable Predicate<? super Component> filter) {
        Objects.requireNonNull(likes, "likes");
        int size = likes.size();
        if (size == 0) {
            return Collections.emptyList();
        }
        @Nullable ArrayList<Component> components = null;
        for (int i = 0; i < size; ++i) {
            @Nullable ComponentLike like = likes.get(i);
            if (like == null) {
                throw new NullPointerException("likes[" + i + "]");
            }
            Component component = like.asComponent();
            if (filter != null && !filter.test(component)) continue;
            if (components == null) {
                components = new ArrayList<Component>(size);
            }
            components.add(component);
        }
        if (components == null) {
            return Collections.emptyList();
        }
        components.trimToSize();
        return Collections.unmodifiableList(components);
    }

    @Nullable
    public static Component unbox(@Nullable ComponentLike like) {
        return like != null ? like.asComponent() : null;
    }

    @Contract(pure=true)
    @NotNull
    public Component asComponent();
}

