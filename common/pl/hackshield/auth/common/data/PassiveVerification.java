/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.data;

import eu.okaeri.configs.OkaeriConfig;
import java.util.Arrays;
import java.util.List;

public class PassiveVerification
extends OkaeriConfig {
    public boolean enabled = false;
    public List<String> servers = Arrays.asList("lobby", "creative");
}

