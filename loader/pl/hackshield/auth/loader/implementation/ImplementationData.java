/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.loader.implementation;

import pl.hackshield.auth.loader.implementation.ImplementationType;

public class ImplementationData {
    private final ImplementationType type;
    private final String fileName;
    private final String packageName;

    public ImplementationData(ImplementationType type, String fileName, String packageName) {
        this.type = type;
        this.fileName = fileName;
        this.packageName = packageName;
    }

    public ImplementationType getType() {
        return this.type;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getPackageName() {
        return this.packageName;
    }
}

