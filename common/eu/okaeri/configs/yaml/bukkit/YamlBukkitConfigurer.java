/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.configuration.MemorySection
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package eu.okaeri.configs.yaml.bukkit;

import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.exception.OkaeriException;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlBukkitConfigurer
extends Configurer {
    private YamlConfiguration config;
    private String commentPrefix = "# ";
    private String sectionSeparator = "";

    public YamlBukkitConfigurer(@NonNull YamlConfiguration config, @NonNull String commentPrefix, @NonNull String sectionSeparator) {
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

    public YamlBukkitConfigurer(@NonNull String commentPrefix, @NonNull String sectionSeparator) {
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

    public YamlBukkitConfigurer(@NonNull String sectionSeparator) {
        this();
        if (sectionSeparator == null) {
            throw new NullPointerException("sectionSeparator is marked non-null but is null");
        }
        this.sectionSeparator = sectionSeparator;
    }

    public YamlBukkitConfigurer(@NonNull YamlConfiguration config) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        this.config = config;
    }

    public YamlBukkitConfigurer() {
        this(new YamlConfiguration());
    }

    @Override
    public List<String> getExtensions() {
        return Arrays.asList("yml", "yaml");
    }

    @Override
    public Object simplify(Object value, GenericsDeclaration genericType, @NonNull SerdesContext serdesContext, boolean conservative) throws OkaeriException {
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        if (value instanceof MemorySection) {
            return ((MemorySection)value).getValues(false);
        }
        return super.simplify(value, genericType, serdesContext, conservative);
    }

    @Override
    public <T> T resolveType(Object object, GenericsDeclaration genericSource, @NonNull Class<T> targetClazz, GenericsDeclaration genericTarget, @NonNull SerdesContext serdesContext) {
        if (targetClazz == null) {
            throw new NullPointerException("targetClazz is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        if (object instanceof MemorySection) {
            Map values = ((MemorySection)object).getValues(false);
            return super.resolveType(values, GenericsDeclaration.of(values), targetClazz, genericTarget, serdesContext);
        }
        return super.resolveType(object, genericSource, targetClazz, genericTarget, serdesContext);
    }

    @Override
    public void setValue(@NonNull String key, Object value, GenericsDeclaration type, FieldDeclaration field) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        Object simplified = this.simplify(value, type, SerdesContext.of(this, field), true);
        this.config.set(key, simplified);
    }

    @Override
    public void setValueUnsafe(@NonNull String key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        this.config.set(key, value);
    }

    @Override
    public Object getValue(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.config.get(key);
    }

    @Override
    public Object remove(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (!this.keyExists(key)) {
            return null;
        }
        Object old = this.config.get(key);
        this.config.set(key, null);
        return old;
    }

    @Override
    public boolean keyExists(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.config.getKeys(false).contains(key);
    }

    @Override
    public List<String> getAllKeys() {
        return Collections.unmodifiableList(new ArrayList(this.config.getKeys(false)));
    }

    @Override
    public void load(@NonNull InputStream inputStream, @NonNull ConfigDeclaration declaration) throws Exception {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is marked non-null but is null");
        }
        if (declaration == null) {
            throw new NullPointerException("declaration is marked non-null but is null");
        }
        this.config.loadFromString(ConfigPostprocessor.of(inputStream).getContext());
    }

    @Override
    public void write(@NonNull OutputStream outputStream, final @NonNull ConfigDeclaration declaration) throws Exception {
        if (outputStream == null) {
            throw new NullPointerException("outputStream is marked non-null but is null");
        }
        if (declaration == null) {
            throw new NullPointerException("declaration is marked non-null but is null");
        }
        String contents = this.config.saveToString();
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
                String comment = ConfigPostprocessor.createComment(YamlBukkitConfigurer.this.commentPrefix, fieldComment);
                return ConfigPostprocessor.addIndent(comment, lineInfo.getIndent()) + line;
            }
        }).prependContextComment(this.commentPrefix, this.sectionSeparator, declaration.getHeader()).write(outputStream);
    }
}

