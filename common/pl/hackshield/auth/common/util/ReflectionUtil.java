/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import sun.misc.Unsafe;

public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static void setFinalField(Object instance, Field field, Object value) {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe)unsafeField.get(null);
            if (!Modifier.isStatic(field.getModifiers())) {
                long fieldOffset = unsafe.objectFieldOffset(field);
                unsafe.putObject(instance, fieldOffset, value);
            } else {
                Object fieldBase = unsafe.staticFieldBase(field);
                long fieldOffset = unsafe.staticFieldOffset(field);
                unsafe.putObject(fieldBase, fieldOffset, value);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Class<?> findClass(String name) {
        try {
            return Class.forName(name);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <I, T> FieldAccessor<I, T> findField(Class<?> aClass, String name) {
        try {
            final Field field = aClass.getDeclaredField(name);
            field.setAccessible(true);
            return new FieldAccessor<I, T>(){

                @Override
                public T get(I instance) {
                    try {
                        return field.get(instance);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void set(I instance, T value) {
                    try {
                        field.set(instance, value);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean doesMethodExist(Class<?> aClass, String name, Class<?> ... parameterTypes) {
        for (Method method : aClass.getDeclaredMethods()) {
            if (!method.getName().equals(name) || !Arrays.equals(method.getParameterTypes(), parameterTypes)) continue;
            return true;
        }
        return false;
    }

    public static <I, R> MethodInvoker<I, R> findMethod(Class<?> aClass, String name, Class<?> ... parameterTypes) {
        try {
            Method method = aClass.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return (instance, args) -> {
                try {
                    return method.invoke(instance, args);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            };
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static interface MethodInvoker<I, R> {
        public R invoke(I var1, Object ... var2);
    }

    public static interface FieldAccessor<I, T> {
        public T get(I var1);

        public void set(I var1, T var2);
    }
}

