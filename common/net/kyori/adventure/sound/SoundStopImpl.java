/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.sound;

import java.util.Objects;
import java.util.stream.Stream;
import net.kyori.adventure.internal.Internals;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class SoundStopImpl
implements SoundStop {
    static final SoundStop ALL = new SoundStopImpl(null){

        @Override
        @Nullable
        public Key sound() {
            return null;
        }
    };
    private final @Nullable Sound.Source source;

    SoundStopImpl(@Nullable Sound.Source source) {
        this.source = source;
    }

    @Override
    public @Nullable Sound.Source source() {
        return this.source;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SoundStopImpl)) {
            return false;
        }
        SoundStopImpl that = (SoundStopImpl)other;
        return Objects.equals(this.sound(), that.sound()) && Objects.equals((Object)this.source, (Object)that.source);
    }

    public int hashCode() {
        int result = Objects.hashCode(this.sound());
        result = 31 * result + Objects.hashCode((Object)this.source);
        return result;
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("name", this.sound()), ExaminableProperty.of("source", (Object)this.source));
    }

    public String toString() {
        return Internals.toString(this);
    }
}

