/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.postprocessor.format;

import eu.okaeri.configs.postprocessor.ConfigSectionWalker;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class YamlSectionWalker
implements ConfigSectionWalker {
    private static final Set<String> MULTILINE_START = new HashSet<String>(Arrays.asList(">", ">-", "|", "|-"));

    @Override
    public boolean isKeyMultilineStart(String line) {
        String trimmed = line.trim().replaceAll("\\s{2,}", " ");
        if (trimmed.isEmpty()) {
            return false;
        }
        int colon = trimmed.indexOf(":");
        int distance = trimmed.length() - colon;
        for (String trigger : MULTILINE_START) {
            if (distance > trigger.length() + 2 || !trimmed.endsWith(trigger)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isKey(String line) {
        String name = this.readName(line);
        return !name.isEmpty() && name.charAt(0) != '-' && name.charAt(0) != '#';
    }

    @Override
    public String readName(String line) {
        return line.split(":", 2)[0].trim();
    }
}

