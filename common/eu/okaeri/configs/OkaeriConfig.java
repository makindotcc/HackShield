/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs;

import eu.okaeri.configs.annotation.Variable;
import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.exception.InitializationException;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.exception.ValidationException;
import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.builtin.NamedMigration;
import eu.okaeri.configs.migrate.view.RawConfigView;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;
import lombok.NonNull;

public abstract class OkaeriConfig {
    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private Path bindFile;
    private Configurer configurer;
    private ConfigDeclaration declaration;
    private boolean removeOrphans = false;

    public OkaeriConfig() {
        this.updateDeclaration();
    }

    public void setConfigurer(@NonNull Configurer configurer) {
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        this.configurer = configurer;
        this.configurer.setParent(this);
    }

    public OkaeriConfig withConfigurer(@NonNull Configurer configurer) {
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        if (this.getConfigurer() != null) {
            configurer.setRegistry(this.getConfigurer().getRegistry());
        }
        this.setConfigurer(configurer);
        return this;
    }

    public OkaeriConfig withConfigurer(@NonNull Configurer configurer, OkaeriSerdesPack ... serdesPack) {
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        if (serdesPack == null) {
            throw new NullPointerException("serdesPack is marked non-null but is null");
        }
        if (this.getConfigurer() != null) {
            configurer.setRegistry(this.getConfigurer().getRegistry());
        }
        this.setConfigurer(configurer);
        Arrays.stream(serdesPack).forEach(this.getConfigurer()::register);
        return this;
    }

    public OkaeriConfig withSerdesPack(@NonNull OkaeriSerdesPack serdesPack) {
        if (serdesPack == null) {
            throw new NullPointerException("serdesPack is marked non-null but is null");
        }
        this.getConfigurer().register(serdesPack);
        return this;
    }

    public OkaeriConfig withBindFile(@NonNull File bindFile) {
        if (bindFile == null) {
            throw new NullPointerException("bindFile is marked non-null but is null");
        }
        this.setBindFile(bindFile.toPath());
        return this;
    }

    public OkaeriConfig withBindFile(@NonNull Path path) {
        if (path == null) {
            throw new NullPointerException("path is marked non-null but is null");
        }
        this.setBindFile(path);
        return this;
    }

    public OkaeriConfig withBindFile(@NonNull String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname is marked non-null but is null");
        }
        this.setBindFile(Paths.get(pathname, new String[0]));
        return this;
    }

    public OkaeriConfig withLogger(@NonNull Logger logger) {
        if (logger == null) {
            throw new NullPointerException("logger is marked non-null but is null");
        }
        this.setLogger(logger);
        return this;
    }

    public OkaeriConfig withRemoveOrphans(boolean removeOrphans) {
        this.setRemoveOrphans(removeOrphans);
        return this;
    }

    public OkaeriConfig saveDefaults() throws OkaeriException {
        if (this.getBindFile() == null) {
            throw new InitializationException("bindFile cannot be null");
        }
        if (Files.exists(this.getBindFile(), new LinkOption[0])) {
            return this;
        }
        return this.save();
    }

    public void set(@NonNull String key, Object value) throws OkaeriException {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (this.getConfigurer() == null) {
            throw new InitializationException("configurer cannot be null");
        }
        FieldDeclaration field = this.getDeclaration().getField(key).orElse(null);
        if (field != null) {
            value = this.getConfigurer().resolveType(value, GenericsDeclaration.of(value), field.getType().getType(), field.getType(), SerdesContext.of(this.configurer, field));
            field.updateValue(value);
        }
        GenericsDeclaration fieldGenerics = field == null ? null : field.getType();
        this.getConfigurer().setValue(key, value, fieldGenerics, field);
    }

    public Object get(@NonNull String key) throws OkaeriException {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (this.getConfigurer() == null) {
            throw new InitializationException("configurer cannot be null");
        }
        FieldDeclaration field = this.getDeclaration().getField(key).orElse(null);
        if (field != null) {
            return field.getValue();
        }
        return this.getConfigurer().getValue(key);
    }

    public <T> T get(@NonNull String key, @NonNull Class<T> clazz) throws OkaeriException {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        return this.get(key, GenericsDeclaration.of(clazz));
    }

    public <T> T get(@NonNull String key, @NonNull GenericsDeclaration generics) throws OkaeriException {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        if (this.getConfigurer() == null) {
            throw new InitializationException("configurer cannot be null");
        }
        FieldDeclaration field = this.getDeclaration().getField(key).orElse(null);
        if (field != null) {
            return (T)this.getConfigurer().resolveType(field.getValue(), field.getType(), generics.getType(), generics, SerdesContext.of(this.configurer, field));
        }
        return (T)this.getConfigurer().getValue(key, generics.getType(), null, SerdesContext.of(this.configurer));
    }

    public OkaeriConfig save() throws OkaeriException {
        return this.save(this.getBindFile());
    }

    public OkaeriConfig save(@NonNull File file) throws OkaeriException {
        if (file == null) {
            throw new NullPointerException("file is marked non-null but is null");
        }
        try {
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            return this.save(new PrintStream((OutputStream)new FileOutputStream(file, false), true, StandardCharsets.UTF_8.name()));
        }
        catch (FileNotFoundException | UnsupportedEncodingException exception) {
            throw new OkaeriException("failed #save using file " + file, exception);
        }
    }

    public OkaeriConfig save(@NonNull Path path) throws OkaeriException {
        if (path == null) {
            throw new NullPointerException("path is marked non-null but is null");
        }
        return this.save(path.toFile());
    }

    public String saveToString() throws OkaeriException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        this.save(outputStream);
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    public OkaeriConfig save(@NonNull OutputStream outputStream) throws OkaeriException {
        if (outputStream == null) {
            throw new NullPointerException("outputStream is marked non-null but is null");
        }
        if (this.getConfigurer() == null) {
            throw new InitializationException("configurer cannot be null");
        }
        for (FieldDeclaration field : this.getDeclaration().getFields()) {
            if (!this.getConfigurer().isValid(field, field.getValue())) {
                throw new ValidationException(this.getConfigurer().getClass() + " marked " + field.getName() + " as invalid without throwing an exception");
            }
            try {
                this.getConfigurer().setValue(field.getName(), field.getValue(), field.getType(), field);
            }
            catch (Exception exception) {
                throw new OkaeriException("failed to #setValue for " + field.getName(), exception);
            }
        }
        try {
            Set<String> orphans = this.getConfigurer().sort(this.getDeclaration());
            if (!orphans.isEmpty() && this.isRemoveOrphans()) {
                this.logger.warning("Removed orphaned (undeclared) keys: " + orphans);
                orphans.forEach(orphan -> this.getConfigurer().remove((String)orphan));
            }
            this.getConfigurer().write(outputStream, this.getDeclaration());
        }
        catch (Exception exception) {
            throw new OkaeriException("failed #write", exception);
        }
        return this;
    }

    public Map<String, Object> asMap(@NonNull Configurer configurer, boolean conservative) throws OkaeriException {
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        for (FieldDeclaration field : this.getDeclaration().getFields()) {
            Object simplified = configurer.simplify(field.getValue(), field.getType(), SerdesContext.of(configurer, field), conservative);
            map.put(field.getName(), simplified);
        }
        if (this.getConfigurer() == null) {
            return map;
        }
        for (String keyName : this.getConfigurer().getAllKeys()) {
            if (map.containsKey(keyName)) continue;
            Object value = this.getConfigurer().getValue(keyName);
            Object simplified = configurer.simplify(value, GenericsDeclaration.of(value), SerdesContext.of(configurer, null), conservative);
            map.put(keyName, simplified);
        }
        return map;
    }

    public OkaeriConfig load(boolean update) throws OkaeriException {
        this.load();
        if (update) {
            this.save();
        }
        return this;
    }

    public OkaeriConfig load(@NonNull String data) throws OkaeriException {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (this.getConfigurer() == null) {
            throw new InitializationException("configurer cannot be null");
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        return this.load(inputStream);
    }

    public OkaeriConfig load(@NonNull InputStream inputStream) throws OkaeriException {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is marked non-null but is null");
        }
        if (this.getConfigurer() == null) {
            throw new InitializationException("configurer cannot be null");
        }
        try {
            this.getConfigurer().load(inputStream, this.getDeclaration());
        }
        catch (Exception exception) {
            throw new OkaeriException("failed #load", exception);
        }
        return this.update();
    }

    public OkaeriConfig load() throws OkaeriException {
        return this.load(this.getBindFile());
    }

    public OkaeriConfig load(@NonNull File file) throws OkaeriException {
        if (file == null) {
            throw new NullPointerException("file is marked non-null but is null");
        }
        try {
            return this.load(new FileInputStream(file));
        }
        catch (FileNotFoundException exception) {
            throw new OkaeriException("failed #load using file " + file, exception);
        }
    }

    public OkaeriConfig load(@NonNull Path path) throws OkaeriException {
        if (path == null) {
            throw new NullPointerException("path is marked non-null but is null");
        }
        return this.load(path.toFile());
    }

    public OkaeriConfig load(@NonNull Map<String, Object> map) throws OkaeriException {
        if (map == null) {
            throw new NullPointerException("map is marked non-null but is null");
        }
        if (this.getConfigurer() == null) {
            throw new InitializationException("configurer cannot be null");
        }
        map.forEach((arg_0, arg_1) -> this.set(arg_0, arg_1));
        return this;
    }

    public OkaeriConfig load(@NonNull OkaeriConfig otherConfig) throws OkaeriException {
        if (otherConfig == null) {
            throw new NullPointerException("otherConfig is marked non-null but is null");
        }
        if (this.getConfigurer() == null) {
            throw new InitializationException("configurer cannot be null");
        }
        return this.load(otherConfig.asMap(this.getConfigurer(), true));
    }

    public OkaeriConfig migrate(ConfigMigration ... migrations) throws OkaeriException {
        if (migrations == null) {
            throw new NullPointerException("migrations is marked non-null but is null");
        }
        return this.migrate((Long performed) -> {
            try {
                this.load(this.saveToString());
            }
            catch (OkaeriException exception) {
                throw new OkaeriException("failed #migrate due to load error after migrations (not saving)", exception);
            }
            this.save();
        }, migrations);
    }

    public OkaeriConfig migrate(@NonNull Consumer<Long> callback, ConfigMigration ... migrations) throws OkaeriException {
        if (callback == null) {
            throw new NullPointerException("callback is marked non-null but is null");
        }
        if (migrations == null) {
            throw new NullPointerException("migrations is marked non-null but is null");
        }
        RawConfigView view = new RawConfigView(this);
        long performed = Arrays.stream(migrations).filter(migration -> {
            try {
                return migration.migrate(this, view);
            }
            catch (Exception exception) {
                throw new OkaeriException("migrate failure in " + migration.getClass().getName(), exception);
            }
        }).peek(migration -> {
            if (migration instanceof NamedMigration) {
                String name = migration.getClass().getSimpleName();
                String description = ((NamedMigration)migration).getDescription();
                this.logger.info(name + ": " + description);
            }
        }).count();
        if (performed > 0L) {
            callback.accept(performed);
        }
        return this;
    }

    public OkaeriConfig update() throws OkaeriException {
        if (this.getDeclaration() == null) {
            throw new InitializationException("declaration cannot be null: config not initialized");
        }
        for (FieldDeclaration field : this.getDeclaration().getFields()) {
            Object value;
            String fieldName = field.getName();
            GenericsDeclaration genericType = field.getType();
            Class<?> type = field.getType().getType();
            Variable variable = field.getVariable();
            boolean updateValue = true;
            if (variable != null) {
                String property;
                String rawProperty = System.getProperty(variable.value());
                String string = property = rawProperty == null ? System.getenv(variable.value()) : rawProperty;
                if (property != null) {
                    Object value2;
                    try {
                        value2 = this.getConfigurer().resolveType(property, GenericsDeclaration.of(property), genericType.getType(), genericType, SerdesContext.of(this.configurer, field));
                    }
                    catch (Exception exception) {
                        throw new OkaeriException("failed to #resolveType for @Variable { " + variable.value() + " }", exception);
                    }
                    if (!this.getConfigurer().isValid(field, value2)) {
                        throw new ValidationException(this.getConfigurer().getClass() + " marked " + field.getName() + " as invalid without throwing an exception");
                    }
                    field.updateValue(value2);
                    field.setVariableHide(true);
                    updateValue = false;
                }
            }
            if (!this.getConfigurer().keyExists(fieldName)) continue;
            try {
                value = this.getConfigurer().getValue(fieldName, type, genericType, SerdesContext.of(this.configurer, field));
            }
            catch (Exception exception) {
                throw new OkaeriException("failed to #getValue for " + fieldName, exception);
            }
            if (updateValue) {
                if (!this.getConfigurer().isValid(field, value)) {
                    throw new ValidationException(this.getConfigurer().getClass() + " marked " + field.getName() + " as invalid without throwing an exception");
                }
                field.updateValue(value);
            }
            field.setStartingValue(value);
        }
        return this;
    }

    public OkaeriConfig updateDeclaration() {
        this.setDeclaration(ConfigDeclaration.of(this));
        return this;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Path getBindFile() {
        return this.bindFile;
    }

    protected void setBindFile(Path bindFile) {
        this.bindFile = bindFile;
    }

    public Configurer getConfigurer() {
        return this.configurer;
    }

    public ConfigDeclaration getDeclaration() {
        return this.declaration;
    }

    protected void setDeclaration(ConfigDeclaration declaration) {
        this.declaration = declaration;
    }

    public boolean isRemoveOrphans() {
        return this.removeOrphans;
    }

    protected void setRemoveOrphans(boolean removeOrphans) {
        this.removeOrphans = removeOrphans;
    }
}

