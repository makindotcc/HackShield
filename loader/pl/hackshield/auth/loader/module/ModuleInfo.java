/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.loader.module;

public class ModuleInfo {
    private final String fileName;
    private final String className;
    private final String initMethod;
    private final Class<?>[] parameterTypes;
    private final Object[] args;

    public ModuleInfo(String fileName, String className, String initMethod, Class<?>[] parameterTypes, Object[] args2) {
        this.fileName = fileName;
        this.className = className;
        this.initMethod = initMethod;
        this.parameterTypes = parameterTypes;
        this.args = args2;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getClassName() {
        return this.className;
    }

    public String getInitMethod() {
        return this.initMethod;
    }

    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }

    public Object[] getArgs() {
        return this.args;
    }
}

