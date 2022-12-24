/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.schema;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Headers;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.NonNull;

public class ConfigDeclaration {
    private static final Map<Class<?>, ConfigDeclaration> DECLARATION_CACHE = new ConcurrentHashMap();
    private Names nameStrategy;
    private String[] header;
    private Map<String, FieldDeclaration> fieldMap;
    private boolean real;
    private Class<?> type;

    public static ConfigDeclaration of(@NonNull Class<?> clazz, OkaeriConfig config) {
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        ConfigDeclaration template = DECLARATION_CACHE.computeIfAbsent(clazz, klass -> {
            ConfigDeclaration declaration = new ConfigDeclaration();
            declaration.setNameStrategy(ConfigDeclaration.readNames(klass));
            declaration.setHeader(ConfigDeclaration.readHeader(klass));
            declaration.setReal(OkaeriConfig.class.isAssignableFrom((Class<?>)klass));
            declaration.setType((Class<?>)klass);
            return declaration;
        });
        ConfigDeclaration declaration = new ConfigDeclaration();
        declaration.setNameStrategy(template.getNameStrategy());
        declaration.setHeader(template.getHeader());
        declaration.setReal(template.isReal());
        declaration.setType(template.getType());
        declaration.setFieldMap(Arrays.stream(clazz.getDeclaredFields()).filter(field -> !field.getName().startsWith("this$")).map(field -> FieldDeclaration.of(declaration, field, config)).filter(Objects::nonNull).collect(Collectors.toMap(FieldDeclaration::getName, field -> field, (u, v) -> {
            throw new IllegalStateException("Duplicate key/field (by name)!\nLeft: " + u + "\nRight: " + v);
        }, LinkedHashMap::new)));
        return declaration;
    }

    public static ConfigDeclaration of(@NonNull OkaeriConfig config) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        return ConfigDeclaration.of(config.getClass(), config);
    }

    public static ConfigDeclaration of(@NonNull Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        return ConfigDeclaration.of(clazz, null);
    }

    private static String[] readHeader(@NonNull Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        Headers headers = clazz.getAnnotation(Headers.class);
        if (headers != null) {
            ArrayList<String> headerList = new ArrayList<String>();
            for (Header header : headers.value()) {
                headerList.addAll(Arrays.asList(header.value()));
            }
            return headerList.toArray(new String[0]);
        }
        Header header = clazz.getAnnotation(Header.class);
        if (header != null) {
            return header.value();
        }
        return null;
    }

    private static Names readNames(@NonNull Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        Names names = clazz.getAnnotation(Names.class);
        while (names == null) {
            if ((clazz = clazz.getEnclosingClass()) == null) {
                return null;
            }
            names = clazz.getAnnotation(Names.class);
        }
        return names;
    }

    public Optional<FieldDeclaration> getField(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return Optional.ofNullable(this.fieldMap.get(key));
    }

    public GenericsDeclaration getGenericsOrNull(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.getField(key).map(FieldDeclaration::getType).orElse(null);
    }

    public Collection<FieldDeclaration> getFields() {
        return this.fieldMap.values();
    }

    public Names getNameStrategy() {
        return this.nameStrategy;
    }

    public String[] getHeader() {
        return this.header;
    }

    public Map<String, FieldDeclaration> getFieldMap() {
        return this.fieldMap;
    }

    public boolean isReal() {
        return this.real;
    }

    public Class<?> getType() {
        return this.type;
    }

    public void setNameStrategy(Names nameStrategy) {
        this.nameStrategy = nameStrategy;
    }

    public void setHeader(String[] header) {
        this.header = header;
    }

    public void setFieldMap(Map<String, FieldDeclaration> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public void setReal(boolean real) {
        this.real = real;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConfigDeclaration)) {
            return false;
        }
        ConfigDeclaration other = (ConfigDeclaration)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isReal() != other.isReal()) {
            return false;
        }
        Names this$nameStrategy = this.getNameStrategy();
        Names other$nameStrategy = other.getNameStrategy();
        if (this$nameStrategy == null ? other$nameStrategy != null : !this$nameStrategy.equals(other$nameStrategy)) {
            return false;
        }
        if (!Arrays.deepEquals(this.getHeader(), other.getHeader())) {
            return false;
        }
        Map<String, FieldDeclaration> this$fieldMap = this.getFieldMap();
        Map<String, FieldDeclaration> other$fieldMap = other.getFieldMap();
        if (this$fieldMap == null ? other$fieldMap != null : !((Object)this$fieldMap).equals(other$fieldMap)) {
            return false;
        }
        Class<?> this$type = this.getType();
        Class<?> other$type = other.getType();
        return !(this$type == null ? other$type != null : !this$type.equals(other$type));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConfigDeclaration;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isReal() ? 79 : 97);
        Names $nameStrategy = this.getNameStrategy();
        result = result * 59 + ($nameStrategy == null ? 43 : $nameStrategy.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getHeader());
        Map<String, FieldDeclaration> $fieldMap = this.getFieldMap();
        result = result * 59 + ($fieldMap == null ? 43 : ((Object)$fieldMap).hashCode());
        Class<?> $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        return result;
    }

    public String toString() {
        return "ConfigDeclaration(nameStrategy=" + this.getNameStrategy() + ", header=" + Arrays.deepToString(this.getHeader()) + ", fieldMap=" + this.getFieldMap() + ", real=" + this.isReal() + ", type=" + this.getType() + ")";
    }
}

