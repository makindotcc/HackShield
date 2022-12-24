/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.yaml.snakeyaml;

import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.postprocessor.ConfigLineInfo;
import eu.okaeri.configs.postprocessor.ConfigPostprocessor;
import eu.okaeri.configs.postprocessor.format.YamlSectionWalker;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.SerdesContext;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.NonNull;
import pl.hackshield.shaded.org.yaml.snakeyaml.DumperOptions;
import pl.hackshield.shaded.org.yaml.snakeyaml.LoaderOptions;
import pl.hackshield.shaded.org.yaml.snakeyaml.Yaml;
import pl.hackshield.shaded.org.yaml.snakeyaml.constructor.Constructor;
import pl.hackshield.shaded.org.yaml.snakeyaml.representer.Representer;
import pl.hackshield.shaded.org.yaml.snakeyaml.resolver.Resolver;

public class YamlSnakeYamlConfigurer
extends Configurer {
    private Yaml config;
    private Map<String, Object> map = new LinkedHashMap<String, Object>();
    private String commentPrefix = "# ";
    private String sectionSeparator = "";

    public YamlSnakeYamlConfigurer(@NonNull Yaml config, @NonNull Map<String, Object> map, @NonNull String commentPrefix, @NonNull String sectionSeparator) {
        this(config, commentPrefix, sectionSeparator);
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (map == null) {
            throw new NullPointerException("map is marked non-null but is null");
        }
        if (commentPrefix == null) {
            throw new NullPointerException("commentPrefix is marked non-null but is null");
        }
        if (sectionSeparator == null) {
            throw new NullPointerException("sectionSeparator is marked non-null but is null");
        }
        this.map = map;
    }

    public YamlSnakeYamlConfigurer(@NonNull Yaml config, @NonNull String commentPrefix, @NonNull String sectionSeparator) {
        this(commentPrefix, sectionSeparator);
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (commentPrefix == null) {
            throw new NullPointerException("commentPrefix is marked non-null but is null");
        }
        if (sectionSeparator == null) {
            throw new NullPointerException("sectionSeparator is marked non-null but is null");
        }
        this.config = config;
    }

    public YamlSnakeYamlConfigurer(@NonNull String commentPrefix, @NonNull String sectionSeparator) {
        this();
        if (commentPrefix == null) {
            throw new NullPointerException("commentPrefix is marked non-null but is null");
        }
        if (sectionSeparator == null) {
            throw new NullPointerException("sectionSeparator is marked non-null but is null");
        }
        this.commentPrefix = commentPrefix;
        this.sectionSeparator = sectionSeparator;
    }

    public YamlSnakeYamlConfigurer(@NonNull String sectionSeparator) {
        this();
        if (sectionSeparator == null) {
            throw new NullPointerException("sectionSeparator is marked non-null but is null");
        }
        this.sectionSeparator = sectionSeparator;
    }

    public YamlSnakeYamlConfigurer(@NonNull Yaml config, @NonNull Map<String, Object> map) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (map == null) {
            throw new NullPointerException("map is marked non-null but is null");
        }
        this.config = config;
        this.map = map;
    }

    public YamlSnakeYamlConfigurer(@NonNull Yaml config) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        this.config = config;
    }

    public YamlSnakeYamlConfigurer() {
        this(new Yaml(new Constructor(), YamlSnakeYamlConfigurer.apply(new Representer(), representer -> representer.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)), YamlSnakeYamlConfigurer.apply(new DumperOptions(), dumperOptions -> dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)), new LoaderOptions(), new Resolver()));
    }

    private static <T> T apply(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }

    @Override
    public List<String> getExtensions() {
        return Arrays.asList("yml", "yaml");
    }

    @Override
    public void setValue(@NonNull String key, Object value, GenericsDeclaration type, FieldDeclaration field) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        Object simplified = this.simplify(value, type, SerdesContext.of(this, field), true);
        this.map.put(key, simplified);
    }

    @Override
    public void setValueUnsafe(@NonNull String key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        this.map.put(key, value);
    }

    @Override
    public Object getValue(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.map.get(key);
    }

    @Override
    public Object remove(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.map.remove(key);
    }

    @Override
    public boolean keyExists(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.map.containsKey(key);
    }

    @Override
    public List<String> getAllKeys() {
        return Collections.unmodifiableList(new ArrayList<String>(this.map.keySet()));
    }

    @Override
    public void load(@NonNull InputStream inputStream, @NonNull ConfigDeclaration declaration) throws Exception {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is marked non-null but is null");
        }
        if (declaration == null) {
            throw new NullPointerException("declaration is marked non-null but is null");
        }
        this.map = (Map)this.config.load(inputStream);
        if (this.map == null) {
            this.map = new LinkedHashMap<String, Object>();
        }
    }

    @Override
    public void write(@NonNull OutputStream outputStream, final @NonNull ConfigDeclaration declaration) throws Exception {
        if (outputStream == null) {
            throw new NullPointerException("outputStream is marked non-null but is null");
        }
        if (declaration == null) {
            throw new NullPointerException("declaration is marked non-null but is null");
        }
        String contents = this.config.dump(this.map);
        ConfigPostprocessor.of(contents).removeLines(line -> line.startsWith(this.commentPrefix.trim())).updateLinesKeys(new YamlSectionWalker(){

            @Override
            public String update(String line, ConfigLineInfo lineInfo, List<ConfigLineInfo> path) {
                ConfigDeclaration currentDeclaration = declaration;
                for (int i = 0; i < path.size() - 1; ++i) {
                    ConfigLineInfo pathElement = path.get(i);
                    Optional<FieldDeclaration> field = currentDeclaration.getField(pathElement.getName());
                    if (!field.isPresent()) {
                        return line;
                    }
                    GenericsDeclaration fieldType = field.get().getType();
                    if (!fieldType.isConfig()) {
                        return line;
                    }
                    currentDeclaration = ConfigDeclaration.of(fieldType.getType());
                }
                Optional<FieldDeclaration> lineDeclaration = currentDeclaration.getField(lineInfo.getName());
                if (!lineDeclaration.isPresent()) {
                    return line;
                }
                String[] fieldComment = lineDeclaration.get().getComment();
                if (fieldComment == null) {
                    return line;
                }
                String comment = ConfigPostprocessor.createComment(YamlSnakeYamlConfigurer.this.commentPrefix, fieldComment);
                return ConfigPostprocessor.addIndent(comment, lineInfo.getIndent()) + line;
            }
        }).prependContextComment(this.commentPrefix, this.sectionSeparator, declaration.getHeader()).write(outputStream);
    }
}

