/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.postprocessor;

import eu.okaeri.configs.postprocessor.ConfigContextManipulator;
import eu.okaeri.configs.postprocessor.ConfigLineFilter;
import eu.okaeri.configs.postprocessor.ConfigLineInfo;
import eu.okaeri.configs.postprocessor.ConfigSectionWalker;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;

public class ConfigPostprocessor {
    private String context;

    public static ConfigPostprocessor of(@NonNull InputStream inputStream) {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is marked non-null but is null");
        }
        ConfigPostprocessor postprocessor = new ConfigPostprocessor();
        postprocessor.setContext(ConfigPostprocessor.readInput(inputStream));
        return postprocessor;
    }

    public static ConfigPostprocessor of(@NonNull String context) {
        if (context == null) {
            throw new NullPointerException("context is marked non-null but is null");
        }
        ConfigPostprocessor postprocessor = new ConfigPostprocessor();
        postprocessor.setContext(context);
        return postprocessor;
    }

    public static int countIndent(@NonNull String line) {
        if (line == null) {
            throw new NullPointerException("line is marked non-null but is null");
        }
        int whitespaces = 0;
        for (char c : line.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                return whitespaces;
            }
            ++whitespaces;
        }
        return whitespaces;
    }

    public static String addIndent(@NonNull String line, int size) {
        if (line == null) {
            throw new NullPointerException("line is marked non-null but is null");
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            buf.append(" ");
        }
        String indent = buf.toString();
        return Arrays.stream(line.split("\n")).map(part -> indent + part).collect(Collectors.joining("\n")) + "\n";
    }

    public static String createCommentOrEmpty(String commentPrefix, String[] strings) {
        return strings == null ? "" : ConfigPostprocessor.createComment(commentPrefix, strings);
    }

    public static String createComment(String commentPrefix, String[] strings) {
        if (strings == null) {
            return null;
        }
        if (commentPrefix == null) {
            commentPrefix = "";
        }
        ArrayList<String> lines = new ArrayList<String>();
        for (String line : strings) {
            String[] parts = line.split("\n");
            String prefix = line.startsWith(commentPrefix.trim()) ? "" : commentPrefix;
            lines.add((line.isEmpty() ? "" : prefix) + line);
        }
        return String.join((CharSequence)"\n", lines) + "\n";
    }

    private static String readInput(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
    }

    private static void writeOutput(OutputStream outputStream, String text) {
        PrintStream out = new PrintStream(outputStream, true, StandardCharsets.UTF_8.name());
        try {
            out.print(text);
        }
        finally {
            if (Collections.singletonList(out).get(0) != null) {
                out.close();
            }
        }
    }

    public ConfigPostprocessor write(@NonNull OutputStream outputStream) {
        if (outputStream == null) {
            throw new NullPointerException("outputStream is marked non-null but is null");
        }
        ConfigPostprocessor.writeOutput(outputStream, this.context);
        return this;
    }

    public ConfigPostprocessor removeLines(@NonNull ConfigLineFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter is marked non-null but is null");
        }
        String[] lines = this.context.split("\n");
        StringBuilder buf = new StringBuilder();
        for (String line : lines) {
            if (filter.remove(line)) continue;
            buf.append(line).append("\n");
        }
        this.context = buf.toString();
        return this;
    }

    public ConfigPostprocessor updateLines(@NonNull ConfigContextManipulator manipulator) {
        if (manipulator == null) {
            throw new NullPointerException("manipulator is marked non-null but is null");
        }
        String[] lines = this.context.split("\n");
        StringBuilder buf = new StringBuilder();
        for (String line : lines) {
            buf.append(manipulator.convert(line)).append("\n");
        }
        this.context = buf.toString();
        return this;
    }

    public ConfigPostprocessor updateLinesKeys(@NonNull ConfigSectionWalker walker) {
        if (walker == null) {
            throw new NullPointerException("walker is marked non-null but is null");
        }
        String[] lines = this.context.split("\n");
        List<ConfigLineInfo> currentPath = new ArrayList<ConfigLineInfo>();
        int lastIndent = 0;
        int level = 0;
        StringBuilder newContext = new StringBuilder();
        boolean multilineSkip = false;
        for (String line : lines) {
            int indent = ConfigPostprocessor.countIndent(line);
            int change = indent - lastIndent;
            String key = walker.readName(line);
            if (!walker.isKey(line)) {
                newContext.append(line).append("\n");
                multilineSkip = false;
                continue;
            }
            if (currentPath.isEmpty()) {
                currentPath.add(ConfigLineInfo.of(indent, change, key));
            }
            if (change > 0) {
                if (!multilineSkip) {
                    ++level;
                    currentPath.add(ConfigLineInfo.of(indent, change, key));
                }
            } else {
                if (change != 0) {
                    ConfigLineInfo lastLineInfo = (ConfigLineInfo)currentPath.get(currentPath.size() - 1);
                    int step = lastLineInfo.getIndent() / level;
                    currentPath = currentPath.subList(0, (level -= change * -1 / step) + 1);
                    multilineSkip = false;
                }
                if (!multilineSkip) {
                    currentPath.set(currentPath.size() - 1, ConfigLineInfo.of(indent, change, key));
                }
            }
            if (multilineSkip) {
                newContext.append(line).append("\n");
                continue;
            }
            if (walker.isKeyMultilineStart(line)) {
                multilineSkip = true;
            }
            lastIndent = indent;
            String updatedLine = walker.update(line, (ConfigLineInfo)currentPath.get(currentPath.size() - 1), currentPath);
            newContext.append(updatedLine).append("\n");
        }
        this.context = newContext.toString();
        return this;
    }

    public ConfigPostprocessor updateContext(@NonNull ConfigContextManipulator manipulator) {
        if (manipulator == null) {
            throw new NullPointerException("manipulator is marked non-null but is null");
        }
        this.context = manipulator.convert(this.context);
        return this;
    }

    public ConfigPostprocessor prependContextComment(String prefix, String[] strings) {
        return this.prependContextComment(prefix, "", strings);
    }

    public ConfigPostprocessor prependContextComment(String prefix, String separator, String[] strings) {
        if (strings != null) {
            this.context = ConfigPostprocessor.createComment(prefix, strings) + separator + this.context;
        }
        return this;
    }

    public ConfigPostprocessor appendContextComment(String prefix, String[] strings) {
        return this.appendContextComment(prefix, "", strings);
    }

    public ConfigPostprocessor appendContextComment(String prefix, String separator, String[] strings) {
        if (strings != null) {
            this.context = this.context + separator + ConfigPostprocessor.createComment(prefix, strings);
        }
        return this;
    }

    public String getContext() {
        return this.context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConfigPostprocessor)) {
            return false;
        }
        ConfigPostprocessor other = (ConfigPostprocessor)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$context = this.getContext();
        String other$context = other.getContext();
        return !(this$context == null ? other$context != null : !this$context.equals(other$context));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConfigPostprocessor;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $context = this.getContext();
        result = result * 59 + ($context == null ? 43 : $context.hashCode());
        return result;
    }

    public String toString() {
        return "ConfigPostprocessor(context=" + this.getContext() + ")";
    }
}

