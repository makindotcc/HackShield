/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatSets;
import it.unimi.dsi.fastutil.objects.AbstractReference2FloatMap;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunctions;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public final class Reference2FloatMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Reference2FloatMaps() {
    }

    public static <K> ObjectIterator<Reference2FloatMap.Entry<K>> fastIterator(Reference2FloatMap<K> map) {
        ObjectSet<Reference2FloatMap.Entry<K>> entries = map.reference2FloatEntrySet();
        return entries instanceof Reference2FloatMap.FastEntrySet ? ((Reference2FloatMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K> void fastForEach(Reference2FloatMap<K> map, Consumer<? super Reference2FloatMap.Entry<K>> consumer) {
        ObjectSet<Reference2FloatMap.Entry<K>> entries = map.reference2FloatEntrySet();
        if (entries instanceof Reference2FloatMap.FastEntrySet) {
            ((Reference2FloatMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static <K> ObjectIterable<Reference2FloatMap.Entry<K>> fastIterable(Reference2FloatMap<K> map) {
        final ObjectSet<Reference2FloatMap.Entry<K>> entries = map.reference2FloatEntrySet();
        return entries instanceof Reference2FloatMap.FastEntrySet ? new ObjectIterable<Reference2FloatMap.Entry<K>>(){

            @Override
            public ObjectIterator<Reference2FloatMap.Entry<K>> iterator() {
                return ((Reference2FloatMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public ObjectSpliterator<Reference2FloatMap.Entry<K>> spliterator() {
                return entries.spliterator();
            }

            @Override
            public void forEach(Consumer<? super Reference2FloatMap.Entry<K>> consumer) {
                ((Reference2FloatMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static <K> Reference2FloatMap<K> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K> Reference2FloatMap<K> singleton(K key, float value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2FloatMap<K> singleton(K key, Float value) {
        return new Singleton<K>(key, value.floatValue());
    }

    public static <K> Reference2FloatMap<K> synchronize(Reference2FloatMap<K> m) {
        return new SynchronizedMap<K>(m);
    }

    public static <K> Reference2FloatMap<K> synchronize(Reference2FloatMap<K> m, Object sync) {
        return new SynchronizedMap<K>(m, sync);
    }

    public static <K> Reference2FloatMap<K> unmodifiable(Reference2FloatMap<? extends K> m) {
        return new UnmodifiableMap<K>(m);
    }

    public static class EmptyMap<K>
    extends Reference2FloatFunctions.EmptyFunction<K>
    implements Reference2FloatMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(float v) {
            return false;
        }

        @Override
        @Deprecated
        public Float getOrDefault(Object key, Float defaultValue) {
            return defaultValue;
        }

        @Override
        public float getOrDefault(Object key, float defaultValue) {
            return defaultValue;
        }

        @Override
        @Deprecated
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends K, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Reference2FloatMap.Entry<K>> reference2FloatEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ReferenceSet<K> keySet() {
            return ReferenceSets.EMPTY_SET;
        }

        @Override
        public FloatCollection values() {
            return FloatSets.EMPTY_SET;
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super Float> consumer) {
        }

        @Override
        public Object clone() {
            return EMPTY_MAP;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map)) {
                return false;
            }
            return ((Map)o).isEmpty();
        }

        @Override
        public String toString() {
            return "{}";
        }
    }

    public static class Singleton<K>
    extends Reference2FloatFunctions.Singleton<K>
    implements Reference2FloatMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Reference2FloatMap.Entry<K>> entries;
        protected transient ReferenceSet<K> keys;
        protected transient FloatCollection values;

        protected Singleton(K key, float value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(float v) {
            return Float.floatToIntBits(this.value) == Float.floatToIntBits(v);
        }

        @Override
        @Deprecated
        public boolean containsValue(Object ov) {
            return Float.floatToIntBits(((Float)ov).floatValue()) == Float.floatToIntBits(this.value);
        }

        @Override
        public void putAll(Map<? extends K, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Reference2FloatMap.Entry<K>> reference2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractReference2FloatMap.BasicEntry<Object>(this.key, this.value));
            }
            return this.entries;
        }

        @Override
        @Deprecated
        public ObjectSet<Map.Entry<K, Float>> entrySet() {
            return this.reference2FloatEntrySet();
        }

        @Override
        public ReferenceSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public FloatCollection values() {
            if (this.values == null) {
                this.values = FloatSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.key) ^ HashCommon.float2int(this.value);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Map)) {
                return false;
            }
            Map m = (Map)o;
            if (m.size() != 1) {
                return false;
            }
            return m.entrySet().iterator().next().equals(this.entrySet().iterator().next());
        }

        public String toString() {
            return "{" + this.key + "=>" + this.value + "}";
        }
    }

    public static class SynchronizedMap<K>
    extends Reference2FloatFunctions.SynchronizedFunction<K>
    implements Reference2FloatMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2FloatMap<K> map;
        protected transient ObjectSet<Reference2FloatMap.Entry<K>> entries;
        protected transient ReferenceSet<K> keys;
        protected transient FloatCollection values;

        protected SynchronizedMap(Reference2FloatMap<K> m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Reference2FloatMap<K> m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(float v) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsValue(v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public boolean containsValue(Object ov) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsValue(ov);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putAll(Map<? extends K, ? extends Float> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Reference2FloatMap.Entry<K>> reference2FloatEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.reference2FloatEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Override
        @Deprecated
        public ObjectSet<Map.Entry<K, Float>> entrySet() {
            return this.reference2FloatEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ReferenceSet<K> keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = ReferenceSets.synchronize(this.map.keySet(), this.sync);
                }
                return this.keys;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FloatCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    this.values = FloatCollections.synchronize(this.map.values(), this.sync);
                }
                return this.values;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.sync;
            synchronized (object) {
                return this.map.equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void writeObject(ObjectOutputStream s) throws IOException {
            Object object = this.sync;
            synchronized (object) {
                s.defaultWriteObject();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float getOrDefault(Object key, float defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super K, ? super Float> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach((BiConsumer<? super K, Float>)action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super K, ? super Float, ? extends Float> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float putIfAbsent(K key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float replace(K key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(K key, float oldValue, float newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsent(K key, Reference2FloatFunction<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeFloatIfPresent(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeFloatIfPresent((K)key, (BiFunction<? super K, Float, Float>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeFloat(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeFloat((K)key, (BiFunction<? super K, Float, Float>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float merge(K key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, (BiFunction<Float, Float, Float>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Float getOrDefault(Object key, Float defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public boolean remove(Object key, Object value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Float replace(K key, Float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public boolean replace(K key, Float oldValue, Float newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Float putIfAbsent(K key, Float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Float computeIfAbsent(K key, Function<? super K, ? extends Float> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Float computeIfPresent(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent((K)key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Float compute(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute((K)key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Float merge(K key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, (BiFunction<Float, Float, Float>)remappingFunction);
            }
        }
    }

    public static class UnmodifiableMap<K>
    extends Reference2FloatFunctions.UnmodifiableFunction<K>
    implements Reference2FloatMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2FloatMap<? extends K> map;
        protected transient ObjectSet<Reference2FloatMap.Entry<K>> entries;
        protected transient ReferenceSet<K> keys;
        protected transient FloatCollection values;

        protected UnmodifiableMap(Reference2FloatMap<? extends K> m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(float v) {
            return this.map.containsValue(v);
        }

        @Override
        @Deprecated
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends K, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Reference2FloatMap.Entry<K>> reference2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.reference2FloatEntrySet());
            }
            return this.entries;
        }

        @Override
        @Deprecated
        public ObjectSet<Map.Entry<K, Float>> entrySet() {
            return this.reference2FloatEntrySet();
        }

        @Override
        public ReferenceSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public FloatCollection values() {
            if (this.values == null) {
                this.values = FloatCollections.unmodifiable(this.map.values());
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        @Override
        public int hashCode() {
            return this.map.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return this.map.equals(o);
        }

        @Override
        public float getOrDefault(Object key, float defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super Float> action) {
            this.map.forEach((BiConsumer<? super K, Float>)action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super Float, ? extends Float> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float putIfAbsent(K key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float replace(K key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, float oldValue, float newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsent(K key, Reference2FloatFunction<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeFloatIfPresent(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeFloat(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float merge(K key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Float getOrDefault(Object key, Float defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        @Deprecated
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Float replace(K key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean replace(K key, Float oldValue, Float newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Float putIfAbsent(K key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Float computeIfAbsent(K key, Function<? super K, ? extends Float> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Float computeIfPresent(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Float compute(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Float merge(K key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }
}

