/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.audience;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;

final class Audiences {
    static final Collector<? super Audience, ?, ForwardingAudience> COLLECTOR = Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), audiences -> Audience.audience(Collections.unmodifiableCollection(audiences)));

    private Audiences() {
    }
}

