/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.postprocessor;

import eu.okaeri.configs.postprocessor.ConfigLineInfo;
import java.util.List;

public interface ConfigSectionWalker {
    public boolean isKey(String var1);

    public boolean isKeyMultilineStart(String var1);

    public String readName(String var1);

    public String update(String var1, ConfigLineInfo var2, List<ConfigLineInfo> var3);
}

