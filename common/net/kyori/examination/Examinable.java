/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.examination;

import java.util.stream.Stream;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.Examiner;
import org.jetbrains.annotations.NotNull;

public interface Examinable {
    @NotNull
    default public String examinableName() {
        return this.getClass().getSimpleName();
    }

    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.empty();
    }

    @NotNull
    default public <R> R examine(@NotNull Examiner<R> examiner) {
        return examiner.examine(this);
    }
}

