/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.OkaeriConfigInitializer;
import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.SerdesContext;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import lombok.NonNull;

public final class ConfigManager {
    public static <T extends OkaeriConfig> T create(@NonNull Class<T> clazz) throws OkaeriException {
        OkaeriConfig config;
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        try {
            config = (OkaeriConfig)clazz.newInstance();
        }
        catch (IllegalAccessException | InstantiationException exception) {
            throw new OkaeriException("cannot create " + clazz.getSimpleName() + " instance: make sure default constructor is available or if subconfig use new instead");
        }
        return (T)ConfigManager.initialize(config);
    }

    public static <T extends OkaeriConfig> T createUnsafe(@NonNull Class<T> clazz) throws OkaeriException {
        OkaeriConfig config;
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        try {
            config = (OkaeriConfig)clazz.newInstance();
        }
        catch (IllegalAccessException | InstantiationException exception) {
            try {
                config = (OkaeriConfig)ConfigManager.allocateInstance(clazz);
            }
            catch (Exception exception1) {
                throw new OkaeriException("failed to create " + clazz + " instance, neither default constructor available, nor unsafe succeeded");
            }
        }
        return (T)ConfigManager.initialize(config);
    }

    public static <T extends OkaeriConfig> T create(@NonNull Class<T> clazz, @NonNull OkaeriConfigInitializer initializer) throws OkaeriException {
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        if (initializer == null) {
            throw new NullPointerException("initializer is marked non-null but is null");
        }
        T config = ConfigManager.create(clazz);
        try {
            initializer.apply((OkaeriConfig)config);
        }
        catch (Exception exception) {
            if (((OkaeriConfig)config).getConfigurer() != null) {
                throw new OkaeriException("failed to initialize " + clazz.getName() + " [" + ((OkaeriConfig)config).getConfigurer().getClass() + "]", exception);
            }
            throw new OkaeriException("failed to initialize " + clazz.getName(), exception);
        }
        return config;
    }

    public static <T extends OkaeriConfig> T transformCopy(@NonNull OkaeriConfig config, @NonNull Class<T> into) throws OkaeriException {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (into == null) {
            throw new NullPointerException("into is marked non-null but is null");
        }
        T copy = ConfigManager.createUnsafe(into);
        Configurer configurer = config.getConfigurer();
        ((OkaeriConfig)copy).withConfigurer(configurer);
        ConfigDeclaration copyDeclaration = ((OkaeriConfig)copy).getDeclaration();
        if (config.getBindFile() != null) {
            ((OkaeriConfig)copy).withBindFile(config.getBindFile());
        }
        configurer.getAllKeys().stream().map(copyDeclaration::getField).filter(Optional::isPresent).map(Optional::get).forEach(field -> {
            Object value = configurer.getValue(field.getName());
            GenericsDeclaration generics = GenericsDeclaration.of(value);
            if (value != null && (field.getType().getType() != value.getClass() || !generics.isPrimitiveWrapper() && !generics.isPrimitive())) {
                value = configurer.resolveType(value, generics, field.getType().getType(), field.getType(), SerdesContext.of(configurer, field));
            }
            field.updateValue(value);
        });
        return copy;
    }

    public static <T extends OkaeriConfig> T deepCopy(@NonNull OkaeriConfig config, @NonNull Configurer newConfigurer, @NonNull Class<T> into) throws OkaeriException {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (newConfigurer == null) {
            throw new NullPointerException("newConfigurer is marked non-null but is null");
        }
        if (into == null) {
            throw new NullPointerException("into is marked non-null but is null");
        }
        T copy = ConfigManager.createUnsafe(into);
        ((OkaeriConfig)copy).withConfigurer(newConfigurer, config.getConfigurer().getRegistry().allSerdes());
        ((OkaeriConfig)copy).withBindFile(config.getBindFile());
        ((OkaeriConfig)copy).load(config.saveToString());
        return copy;
    }

    public static <T extends OkaeriConfig> T initialize(@NonNull T config) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        config.updateDeclaration();
        return config;
    }

    private static Object allocateInstance(Class<?> clazz) throws Exception {
        Class<?> unsafeClazz = Class.forName("sun.misc.Unsafe");
        Field theUnsafeField = unsafeClazz.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        Object unsafeInstance = theUnsafeField.get(null);
        Method allocateInstance = unsafeClazz.getDeclaredMethod("allocateInstance", Class.class);
        return allocateInstance.invoke(unsafeInstance, clazz);
    }

    private ConfigManager() {
    }
}

