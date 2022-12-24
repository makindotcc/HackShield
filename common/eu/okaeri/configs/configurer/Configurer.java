/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.configurer;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.TargetType;
import eu.okaeri.configs.configurer.InMemoryWrappedConfigurer;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.ObjectTransformer;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesContext;
import eu.okaeri.configs.serdes.SerdesRegistry;
import eu.okaeri.configs.serdes.SerializationData;
import eu.okaeri.configs.serdes.standard.StandardSerdes;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;

public abstract class Configurer {
    private OkaeriConfig parent;
    @NonNull
    private SerdesRegistry registry = new SerdesRegistry();

    public Configurer() {
        this.registry.register(new StandardSerdes());
    }

    public void register(@NonNull OkaeriSerdesPack pack) {
        if (pack == null) {
            throw new NullPointerException("pack is marked non-null but is null");
        }
        this.registry.register(pack);
    }

    public List<String> getExtensions() {
        return Collections.emptyList();
    }

    public abstract void setValue(@NonNull String var1, Object var2, GenericsDeclaration var3, FieldDeclaration var4);

    public abstract void setValueUnsafe(@NonNull String var1, Object var2);

    public abstract Object getValue(@NonNull String var1);

    public Object getValueUnsafe(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.getValue(key);
    }

    public abstract Object remove(@NonNull String var1);

    public boolean isToStringObject(@NonNull Object object, GenericsDeclaration genericType) {
        if (object == null) {
            throw new NullPointerException("object is marked non-null but is null");
        }
        if (object instanceof Class) {
            Class clazzObject = (Class)object;
            return clazzObject.isEnum() || genericType != null && this.registry.canTransform(genericType, GenericsDeclaration.of(String.class));
        }
        return object.getClass().isEnum() || this.isToStringObject(object.getClass(), genericType);
    }

    public Object simplifyCollection(@NonNull Collection<?> value, GenericsDeclaration genericType, @NonNull SerdesContext serdesContext, boolean conservative) throws OkaeriException {
        if (value == null) {
            throw new NullPointerException("value is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        ArrayList<Object> collection = new ArrayList<Object>();
        GenericsDeclaration collectionSubtype = genericType == null ? null : genericType.getSubtypeAtOrNull(0);
        for (Object collectionElement : value) {
            collection.add(this.simplify(collectionElement, collectionSubtype, serdesContext, conservative));
        }
        return collection;
    }

    public Object simplifyMap(@NonNull Map<Object, Object> value, GenericsDeclaration genericType, @NonNull SerdesContext serdesContext, boolean conservative) throws OkaeriException {
        if (value == null) {
            throw new NullPointerException("value is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
        GenericsDeclaration keyDeclaration = genericType == null ? null : genericType.getSubtypeAtOrNull(0);
        GenericsDeclaration valueDeclaration = genericType == null ? null : genericType.getSubtypeAtOrNull(1);
        for (Map.Entry<Object, Object> entry : value.entrySet()) {
            Object key = this.simplify(entry.getKey(), keyDeclaration, serdesContext, conservative);
            Object kValue = this.simplify(entry.getValue(), valueDeclaration, serdesContext, conservative);
            map.put(key, kValue);
        }
        return map;
    }

    public Object simplify(Object value, GenericsDeclaration genericType, @NonNull SerdesContext serdesContext, boolean conservative) throws OkaeriException {
        Class<?> serializerType;
        ObjectSerializer serializer;
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        if (value == null) {
            return null;
        }
        if (genericType != null && genericType.getType() == Object.class && !genericType.hasSubtypes()) {
            genericType = GenericsDeclaration.of(value);
        }
        if ((serializer = this.registry.getSerializer(serializerType = genericType != null ? genericType.getType() : value.getClass())) == null) {
            GenericsDeclaration valueDeclaration;
            if (OkaeriConfig.class.isAssignableFrom(value.getClass())) {
                OkaeriConfig config = (OkaeriConfig)value;
                return config.asMap(this, conservative);
            }
            if (conservative && (serializerType.isPrimitive() || GenericsDeclaration.of(serializerType).isPrimitiveWrapper())) {
                return value;
            }
            if (serializerType.isPrimitive()) {
                Class<?> wrappedPrimitive = GenericsDeclaration.of(serializerType).wrap();
                return this.simplify(wrappedPrimitive.cast(value), GenericsDeclaration.of(wrappedPrimitive), serdesContext, conservative);
            }
            if (genericType == null && this.isToStringObject(serializerType, valueDeclaration = GenericsDeclaration.of(value))) {
                return this.resolveType(value, genericType, String.class, null, serdesContext);
            }
            if (this.isToStringObject(serializerType, genericType)) {
                return this.resolveType(value, genericType, String.class, null, serdesContext);
            }
            if (value instanceof Collection) {
                return this.simplifyCollection((Collection)value, genericType, serdesContext, conservative);
            }
            if (value instanceof Map) {
                return this.simplifyMap((Map)value, genericType, serdesContext, conservative);
            }
            throw new OkaeriException("cannot simplify type " + serializerType + " (" + genericType + "): '" + value + "' [" + value.getClass() + "]");
        }
        Configurer configurer = this.getParent() == null ? this : this.getParent().getConfigurer();
        SerializationData serializationData = new SerializationData(configurer, serdesContext);
        serializer.serialize(value, serializationData, genericType == null ? GenericsDeclaration.of(value) : genericType);
        LinkedHashMap<Object, Object> serializationMap = new LinkedHashMap<Object, Object>(serializationData.asMap());
        return this.simplifyMap(serializationMap, GenericsDeclaration.of(Map.class, Collections.singletonList(String.class)), serdesContext, conservative);
    }

    public <T> T getValue(@NonNull String key, @NonNull Class<T> clazz, GenericsDeclaration genericType, @NonNull SerdesContext serdesContext) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        Object value = this.getValue(key);
        if (value == null) {
            return null;
        }
        return this.resolveType(value, GenericsDeclaration.of(value), clazz, genericType, serdesContext);
    }

    public <T> T resolveType(Object object, GenericsDeclaration genericSource, @NonNull Class<T> targetClazz, GenericsDeclaration genericTarget, @NonNull SerdesContext serdesContext) throws OkaeriException {
        ObjectTransformer transformer;
        GenericsDeclaration target;
        if (targetClazz == null) {
            throw new NullPointerException("targetClazz is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        if (object == null) {
            return null;
        }
        GenericsDeclaration source = genericSource == null ? GenericsDeclaration.of(object) : genericSource;
        GenericsDeclaration genericsDeclaration = target = genericTarget == null ? GenericsDeclaration.of(targetClazz) : genericTarget;
        if (target.isPrimitive()) {
            target = GenericsDeclaration.of(target.wrap());
        }
        ObjectSerializer objectSerializer = this.registry.getSerializer(targetClazz);
        if (object instanceof Map && objectSerializer != null) {
            Configurer configurer = this.getParent() == null ? this : this.getParent().getConfigurer();
            DeserializationData deserializationData = new DeserializationData((Map)object, configurer, serdesContext);
            Object deserialized = objectSerializer.deserialize(deserializationData, target);
            return targetClazz.cast(deserialized);
        }
        if (OkaeriConfig.class.isAssignableFrom(targetClazz)) {
            T config = ConfigManager.createUnsafe(targetClazz);
            Map configMap = this.resolveType(object, source, Map.class, GenericsDeclaration.of(Map.class, Arrays.asList(String.class, Object.class)), serdesContext);
            ((OkaeriConfig)config).setConfigurer(new InMemoryWrappedConfigurer(this, configMap));
            return (T)((OkaeriConfig)config).update();
        }
        if (genericTarget != null) {
            Class<?> localTargetClazz = this.resolveTargetBaseType(serdesContext, target, source);
            if (object instanceof Collection && Collection.class.isAssignableFrom(localTargetClazz)) {
                Collection sourceList = (Collection)object;
                Collection targetList = (Collection)this.createInstance(localTargetClazz);
                GenericsDeclaration listDeclaration = genericTarget.getSubtypeAtOrNull(0);
                for (Object item2 : sourceList) {
                    Object converted = this.resolveType(item2, GenericsDeclaration.of(item2), listDeclaration.getType(), listDeclaration, serdesContext);
                    targetList.add(converted);
                }
                return (T)localTargetClazz.cast(targetList);
            }
            if (object instanceof Map && Map.class.isAssignableFrom(localTargetClazz)) {
                Map values = (Map)object;
                GenericsDeclaration keyDeclaration = genericTarget.getSubtypeAtOrNull(0);
                GenericsDeclaration valueDeclaration = genericTarget.getSubtypeAtOrNull(1);
                Map map = (Map)this.createInstance(localTargetClazz);
                for (Map.Entry entry : values.entrySet()) {
                    Object key = this.resolveType(entry.getKey(), GenericsDeclaration.of(entry.getKey()), keyDeclaration.getType(), keyDeclaration, serdesContext);
                    Object value = this.resolveType(entry.getValue(), GenericsDeclaration.of(entry.getValue()), valueDeclaration.getType(), valueDeclaration, serdesContext);
                    map.put(key, value);
                }
                return (T)localTargetClazz.cast(map);
            }
        }
        if ((transformer = this.registry.getTransformer(source, target)) == null) {
            Class<?> objectClazz = object.getClass();
            try {
                if (object instanceof String && target.isEnum()) {
                    String strObject = (String)object;
                    try {
                        Method enumMethod = targetClazz.getMethod("valueOf", String.class);
                        Object enumValue = enumMethod.invoke(null, strObject);
                        if (enumValue != null) {
                            return targetClazz.cast(enumValue);
                        }
                    }
                    catch (InvocationTargetException ignored) {
                        Enum[] enumValues;
                        for (Enum value : enumValues = (Enum[])targetClazz.getEnumConstants()) {
                            if (!strObject.equalsIgnoreCase(value.name())) continue;
                            return targetClazz.cast(value);
                        }
                    }
                    String enumValuesStr = Arrays.stream(targetClazz.getEnumConstants()).map(item -> ((Enum)item).name()).collect(Collectors.joining(", "));
                    throw new IllegalArgumentException("no enum value for name " + strObject + " (available: " + enumValuesStr + ")");
                }
                if (source.isEnum() && targetClazz == String.class) {
                    Method enumMethod = objectClazz.getMethod("name", new Class[0]);
                    return targetClazz.cast(enumMethod.invoke(object, new Object[0]));
                }
            }
            catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
                throw new OkaeriException("failed to resolve enum " + object.getClass() + " <> " + targetClazz, exception);
            }
            if (targetClazz.isPrimitive() && GenericsDeclaration.doBoxTypesMatch(targetClazz, objectClazz)) {
                GenericsDeclaration primitiveDeclaration = GenericsDeclaration.of(object);
                return (T)primitiveDeclaration.unwrapValue(object);
            }
            if (targetClazz.isPrimitive() || GenericsDeclaration.of(targetClazz).isPrimitiveWrapper()) {
                Object simplified = this.simplify(object, GenericsDeclaration.of(objectClazz), serdesContext, false);
                return this.resolveType(simplified, GenericsDeclaration.of(simplified), targetClazz, GenericsDeclaration.of(targetClazz), serdesContext);
            }
            List<ObjectTransformer> transformersFrom = this.getRegistry().getTransformersFrom(source);
            for (ObjectTransformer stepOneTransformer : transformersFrom) {
                GenericsDeclaration stepOneTarget = stepOneTransformer.getPair().getTo();
                ObjectTransformer stepTwoTransformer = this.getRegistry().getTransformer(stepOneTarget, target);
                if (stepTwoTransformer == null) continue;
                Object transformed = stepOneTransformer.transform(object, serdesContext);
                Object doubleTransformed = stepTwoTransformer.transform(transformed, serdesContext);
                return targetClazz.cast(doubleTransformed);
            }
            try {
                return targetClazz.cast(object);
            }
            catch (ClassCastException exception) {
                throw new OkaeriException("cannot resolve " + object.getClass() + " to " + targetClazz + " (" + source + " => " + target + "): " + object, exception);
            }
        }
        if (targetClazz.isPrimitive()) {
            Object transformed = transformer.transform(object, serdesContext);
            return (T)GenericsDeclaration.of(targetClazz).unwrapValue(transformed);
        }
        return targetClazz.cast(transformer.transform(object, serdesContext));
    }

    public Class<?> resolveTargetBaseType(@NonNull SerdesContext serdesContext, @NonNull GenericsDeclaration target, @NonNull GenericsDeclaration source) {
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        if (target == null) {
            throw new NullPointerException("target is marked non-null but is null");
        }
        if (source == null) {
            throw new NullPointerException("source is marked non-null but is null");
        }
        FieldDeclaration serdesContextField = serdesContext.getField();
        Class<?> targetType = target.getType();
        if (serdesContextField != null && !serdesContextField.getType().equals(target)) {
            return targetType;
        }
        if (serdesContextField == null) {
            return targetType;
        }
        Optional<TargetType> targetTypeAnnotation = serdesContextField.getAnnotation(TargetType.class);
        if (targetTypeAnnotation.isPresent()) {
            return targetTypeAnnotation.get().value();
        }
        return targetType;
    }

    public Object createInstance(@NonNull Class<?> clazz) throws OkaeriException {
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        try {
            if (Collection.class.isAssignableFrom(clazz)) {
                if (clazz == Set.class) {
                    return new LinkedHashSet();
                }
                if (clazz == List.class) {
                    return new ArrayList();
                }
                try {
                    return clazz.newInstance();
                }
                catch (InstantiationException exception) {
                    throw new OkaeriException("cannot create instance of " + clazz + " (tip: provide implementation (e.g. ArrayList) for types with no default constructor using @TargetType annotation)", exception);
                }
            }
            if (Map.class.isAssignableFrom(clazz)) {
                if (clazz == Map.class) {
                    return new LinkedHashMap();
                }
                try {
                    return clazz.newInstance();
                }
                catch (InstantiationException exception) {
                    throw new OkaeriException("cannot create instance of " + clazz + " (tip: provide implementation (e.g. LinkedHashMap) for types with no default constructor using @TargetType annotation)", exception);
                }
            }
            throw new OkaeriException("cannot create instance of " + clazz);
        }
        catch (Exception exception) {
            throw new OkaeriException("failed to create instance of " + clazz, exception);
        }
    }

    public boolean keyExists(@NonNull String key) {
        if (key == null) {
            throw new NullPointerException("key is marked non-null but is null");
        }
        return this.getValue(key) != null;
    }

    public boolean isValid(@NonNull FieldDeclaration declaration, Object value) {
        if (declaration == null) {
            throw new NullPointerException("declaration is marked non-null but is null");
        }
        return true;
    }

    public List<String> getAllKeys() {
        return this.getParent().getDeclaration().getFields().stream().map(FieldDeclaration::getName).collect(Collectors.toList());
    }

    public Set<String> sort(@NonNull ConfigDeclaration declaration) {
        if (declaration == null) {
            throw new NullPointerException("declaration is marked non-null but is null");
        }
        Map reordered = declaration.getFields().stream().collect(LinkedHashMap::new, (map, field) -> {
            Object oldValue = this.getValueUnsafe(field.getName());
            this.remove(field.getName());
            map.put(field.getName(), oldValue);
        }, HashMap::putAll);
        LinkedHashSet<String> orphans = new LinkedHashSet<String>(this.getAllKeys());
        reordered.forEach((arg_0, arg_1) -> this.setValueUnsafe(arg_0, arg_1));
        return orphans;
    }

    public abstract void write(@NonNull OutputStream var1, @NonNull ConfigDeclaration var2) throws Exception;

    public abstract void load(@NonNull InputStream var1, @NonNull ConfigDeclaration var2) throws Exception;

    public OkaeriConfig getParent() {
        return this.parent;
    }

    public void setParent(OkaeriConfig parent) {
        this.parent = parent;
    }

    public void setRegistry(@NonNull SerdesRegistry registry) {
        if (registry == null) {
            throw new NullPointerException("registry is marked non-null but is null");
        }
        this.registry = registry;
    }

    @NonNull
    public SerdesRegistry getRegistry() {
        return this.registry;
    }
}

