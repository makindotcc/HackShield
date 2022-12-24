/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  kotlin.text.Charsets
 */
package pl.hackshield.auth.common.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kotlin.text.Charsets;
import pl.hackshield.auth.common.data.Config;
import pl.hackshield.auth.common.util.config.Comment;

public class ConfigManager {
    private final File pluginDirectory;
    private final File configFile;
    private final ObjectMapper om;
    private Config config;

    public ConfigManager(File pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
        this.configFile = new File(this.pluginDirectory, "config.yml");
        this.om = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
    }

    public Config loadConfig() {
        if (!this.configFile.exists()) {
            this.config = new Config();
            this.saveConfig();
        } else {
            try {
                this.config = this.om.readValue(this.configFile, Config.class);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.config;
    }

    public void saveConfig() {
        this.pluginDirectory.mkdirs();
        try {
            String prefix = "# HackShield Plugin - Config file\n";
            String content = prefix + this.om.writeValueAsString(this.config);
            List<String> contentLines = Arrays.asList(content.split("\n"));
            ArrayList<String> finalContent = new ArrayList<String>(contentLines);
            HashMap commentsMap = new HashMap();
            int cursor = 0;
            for (Field field : Config.class.getDeclaredFields()) {
                Comment[] annotations = (Comment[])field.getAnnotationsByType(Comment.class);
                if (annotations.length == 0) continue;
                commentsMap.put(field.getName(), Arrays.stream(annotations).map(comment -> "# " + comment.value()).collect(Collectors.toList()));
            }
            for (String line : contentLines) {
                String sectionName;
                List comments;
                if (!line.startsWith(" ") && line.contains(":") && Objects.nonNull(comments = (List)commentsMap.get(sectionName = this.getSectionName(line)))) {
                    finalContent.add(cursor++, "");
                    finalContent.addAll(cursor, comments);
                    cursor += comments.size();
                }
                ++cursor;
            }
            Files.write(this.configFile.toPath(), finalContent, Charsets.UTF_8, new OpenOption[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSectionName(String line) {
        return line.split(":")[0];
    }

    public Config getConfig() {
        return this.config;
    }
}

