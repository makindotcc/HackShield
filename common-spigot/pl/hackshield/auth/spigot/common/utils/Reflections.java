/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package pl.hackshield.auth.spigot.common.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;

public final class Reflections {
    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "").replace(".", "");
    private static final Map<String, MethodInvoker> methodCache = new ConcurrentHashMap<String, MethodInvoker>();

    private Reflections() {
    }

    public static MethodInvoker getMethod(Class<?> aClass, String name, boolean cache, Class<?> ... parameterTypes) {
        try {
            String key = aClass.getName() + "." + name + Arrays.toString(parameterTypes);
            if (cache && methodCache.containsKey(key)) {
                return methodCache.get(key);
            }
            Method method = aClass.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            MethodInvoker methodInvoker = (instance, params) -> {
                try {
                    return method.invoke(instance, params);
                }
                catch (IllegalAccessException | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            };
            if (cache) {
                methodCache.put(key, methodInvoker);
            }
            return methodInvoker;
        }
        catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> FieldAccessor<T> getField(Class<?> aClass, String name) {
        try {
            final Field field = aClass.getDeclaredField(name);
            field.setAccessible(true);
            return new FieldAccessor<T>(){

                @Override
                public T get(Object instance) {
                    try {
                        return field.get(instance);
                    }
                    catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public void set(Object instance, Object value) {
                    try {
                        field.set(instance, value);
                    }
                    catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            };
        }
        catch (NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Field getJavaField(Class<?> aClass, String name) {
        try {
            Field field = aClass.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }
        catch (NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Field getJavaField(String aClass, String name) {
        try {
            Class<?> clazz = Reflections.getClass(aClass);
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }
        catch (NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> ConstructorInvoker<T> getConstructor(Class<?> aClass, Class<?> ... parameterTypes) {
        try {
            Constructor<?> constructor = aClass.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return params -> {
                try {
                    return constructor.newInstance(params);
                }
                catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            };
        }
        catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + VERSION + "." + name);
        }
        catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Class<?> getMinecraftClass_1_17(String classPackage, String name) {
        try {
            return Class.forName("net.minecraft." + classPackage + "." + name);
        }
        catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Class<?> getBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + VERSION + "." + name);
        }
        catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        }
        catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Object newInstance(Constructor<?> constructor, Object ... args) {
        try {
            return constructor.newInstance(args);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void newInstance(Class<?> aClass) {
        try {
            aClass.newInstance();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static interface MethodInvoker {
        public Object invoke(Object var1, Object ... var2);
    }

    public static interface ConstructorInvoker<T> {
        public T newInstance(Object ... var1);
    }

    public static interface FieldAccessor<T> {
        public T get(Object var1);

        public void set(Object var1, Object var2);
    }
}

