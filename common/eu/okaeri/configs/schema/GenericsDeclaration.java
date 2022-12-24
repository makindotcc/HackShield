/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.schema;

import eu.okaeri.configs.OkaeriConfig;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;

public class GenericsDeclaration {
    private static final Map<String, Class<?>> PRIMITIVES = new HashMap();
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap();
    private static final Set<Class<?>> PRIMITIVE_WRAPPERS = new HashSet();
    private Class<?> type;
    private List<GenericsDeclaration> subtype = new ArrayList<GenericsDeclaration>();
    private boolean isEnum;

    private GenericsDeclaration(Class<?> type) {
        this.type = type;
        this.isEnum = type.isEnum();
    }

    public static boolean isUnboxedCompatibleWithBoxed(@NonNull Class<?> unboxedClazz, @NonNull Class<?> boxedClazz) {
        if (unboxedClazz == null) {
            throw new NullPointerException("unboxedClazz is marked non-null but is null");
        }
        if (boxedClazz == null) {
            throw new NullPointerException("boxedClazz is marked non-null but is null");
        }
        Class<?> primitiveWrapper = PRIMITIVE_TO_WRAPPER.get(unboxedClazz);
        return primitiveWrapper == boxedClazz;
    }

    public static boolean doBoxTypesMatch(@NonNull Class<?> clazz1, @NonNull Class<?> clazz2) {
        if (clazz1 == null) {
            throw new NullPointerException("clazz1 is marked non-null but is null");
        }
        if (clazz2 == null) {
            throw new NullPointerException("clazz2 is marked non-null but is null");
        }
        return GenericsDeclaration.isUnboxedCompatibleWithBoxed(clazz1, clazz2) || GenericsDeclaration.isUnboxedCompatibleWithBoxed(clazz2, clazz1);
    }

    public static GenericsDeclaration of(@NonNull Object type, @NonNull List<Object> subtypes) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        if (subtypes == null) {
            throw new NullPointerException("subtypes is marked non-null but is null");
        }
        Class<?> finalType = type instanceof Class ? (Class<?>)type : type.getClass();
        GenericsDeclaration declaration = new GenericsDeclaration(finalType);
        declaration.setSubtype(subtypes.stream().map(GenericsDeclaration::of).collect(Collectors.toList()));
        return declaration;
    }

    public static GenericsDeclaration of(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof GenericsDeclaration) {
            return (GenericsDeclaration)object;
        }
        if (object instanceof Class) {
            return new GenericsDeclaration((Class)object);
        }
        if (object instanceof Type) {
            return GenericsDeclaration.from((Type)object);
        }
        return new GenericsDeclaration(object.getClass());
    }

    private static GenericsDeclaration from(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type rawType = parameterizedType.getRawType();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (rawType instanceof Class) {
                GenericsDeclaration declaration = new GenericsDeclaration((Class)rawType);
                declaration.setSubtype(Arrays.stream(actualTypeArguments).map(GenericsDeclaration::of).filter(Objects::nonNull).collect(Collectors.toList()));
                return declaration;
            }
        }
        throw new IllegalArgumentException("cannot process type: " + type + " [" + type.getClass() + "]");
    }

    public GenericsDeclaration getSubtypeAtOrNull(int index) {
        return this.subtype == null ? null : (index >= this.subtype.size() ? null : this.subtype.get(index));
    }

    public GenericsDeclaration getSubtypeAtOrThrow(int index) {
        GenericsDeclaration subtype = this.getSubtypeAtOrNull(index);
        if (subtype == null) {
            throw new IllegalArgumentException("Cannot resolve subtype with index " + index + " for " + this);
        }
        return subtype;
    }

    public Class<?> wrap() {
        return PRIMITIVE_TO_WRAPPER.get(this.type);
    }

    public Object unwrapValue(Object object) {
        if (object instanceof Boolean) {
            return (boolean)((Boolean)object);
        }
        if (object instanceof Byte) {
            return (byte)((Byte)object);
        }
        if (object instanceof Character) {
            return Character.valueOf(((Character)object).charValue());
        }
        if (object instanceof Double) {
            return (double)((Double)object);
        }
        if (object instanceof Float) {
            return Float.valueOf(((Float)object).floatValue());
        }
        if (object instanceof Integer) {
            return (int)((Integer)object);
        }
        if (object instanceof Long) {
            return (long)((Long)object);
        }
        if (object instanceof Short) {
            return (short)((Short)object);
        }
        return object;
    }

    public boolean isPrimitive() {
        return this.type.isPrimitive();
    }

    public boolean isPrimitiveWrapper() {
        return PRIMITIVE_WRAPPERS.contains(this.type);
    }

    public boolean isConfig() {
        return OkaeriConfig.class.isAssignableFrom(this.type);
    }

    public boolean hasSubtypes() {
        return this.subtype != null && !this.subtype.isEmpty();
    }

    public Class<?> getType() {
        return this.type;
    }

    public List<GenericsDeclaration> getSubtype() {
        return this.subtype;
    }

    public boolean isEnum() {
        return this.isEnum;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public void setSubtype(List<GenericsDeclaration> subtype) {
        this.subtype = subtype;
    }

    public void setEnum(boolean isEnum) {
        this.isEnum = isEnum;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GenericsDeclaration)) {
            return false;
        }
        GenericsDeclaration other = (GenericsDeclaration)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isEnum() != other.isEnum()) {
            return false;
        }
        Class<?> this$type = this.getType();
        Class<?> other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        List<GenericsDeclaration> this$subtype = this.getSubtype();
        List<GenericsDeclaration> other$subtype = other.getSubtype();
        return !(this$subtype == null ? other$subtype != null : !((Object)this$subtype).equals(other$subtype));
    }

    protected boolean canEqual(Object other) {
        return other instanceof GenericsDeclaration;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isEnum() ? 79 : 97);
        Class<?> $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        List<GenericsDeclaration> $subtype = this.getSubtype();
        result = result * 59 + ($subtype == null ? 43 : ((Object)$subtype).hashCode());
        return result;
    }

    public String toString() {
        return "GenericsDeclaration(type=" + this.getType() + ", subtype=" + this.getSubtype() + ", isEnum=" + this.isEnum() + ")";
    }

    public GenericsDeclaration(Class<?> type, List<GenericsDeclaration> subtype, boolean isEnum) {
        this.type = type;
        this.subtype = subtype;
        this.isEnum = isEnum;
    }

    static {
        PRIMITIVES.put(Boolean.TYPE.getName(), Boolean.TYPE);
        PRIMITIVES.put(Byte.TYPE.getName(), Byte.TYPE);
        PRIMITIVES.put(Character.TYPE.getName(), Character.TYPE);
        PRIMITIVES.put(Double.TYPE.getName(), Double.TYPE);
        PRIMITIVES.put(Float.TYPE.getName(), Float.TYPE);
        PRIMITIVES.put(Integer.TYPE.getName(), Integer.TYPE);
        PRIMITIVES.put(Long.TYPE.getName(), Long.TYPE);
        PRIMITIVES.put(Short.TYPE.getName(), Short.TYPE);
        PRIMITIVE_TO_WRAPPER.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(Byte.TYPE, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(Character.TYPE, Character.class);
        PRIMITIVE_TO_WRAPPER.put(Double.TYPE, Double.class);
        PRIMITIVE_TO_WRAPPER.put(Float.TYPE, Float.class);
        PRIMITIVE_TO_WRAPPER.put(Integer.TYPE, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(Long.TYPE, Long.class);
        PRIMITIVE_TO_WRAPPER.put(Short.TYPE, Short.class);
        PRIMITIVE_WRAPPERS.add(Boolean.class);
        PRIMITIVE_WRAPPERS.add(Byte.class);
        PRIMITIVE_WRAPPERS.add(Character.class);
        PRIMITIVE_WRAPPERS.add(Double.class);
        PRIMITIVE_WRAPPERS.add(Float.class);
        PRIMITIVE_WRAPPERS.add(Integer.class);
        PRIMITIVE_WRAPPERS.add(Long.class);
        PRIMITIVE_WRAPPERS.add(Short.class);
    }
}

