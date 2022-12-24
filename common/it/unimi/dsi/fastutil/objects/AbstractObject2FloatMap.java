/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObject2FloatFunction;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractObject2FloatMap<K>
extends AbstractObject2FloatFunction<K>
implements Object2FloatMap<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractObject2FloatMap() {
    }

    @Override
    public boolean containsKey(Object k) {
        Iterator i = this.object2FloatEntrySet().iterator();
        while (i.hasNext()) {
            if (((Object2FloatMap.Entry)i.next()).getKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(float v) {
        Iterator i = this.object2FloatEntrySet().iterator();
        while (i.hasNext()) {
            if (((Object2FloatMap.Entry)i.next()).getFloatValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ObjectSet<K> keySet() {
        return new AbstractObjectSet<K>(){

            @Override
            public boolean contains(Object k) {
                return AbstractObject2FloatMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractObject2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2FloatMap.this.clear();
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    private final ObjectIterator<Object2FloatMap.Entry<K>> i;
                    {
                        this.i = Object2FloatMaps.fastIterator(AbstractObject2FloatMap.this);
                    }

                    @Override
                    public K next() {
                        return ((Object2FloatMap.Entry)this.i.next()).getKey();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }

                    @Override
                    public void remove() {
                        this.i.remove();
                    }

                    @Override
                    public void forEachRemaining(Consumer<? super K> action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept((Object)entry.getKey()));
                    }
                };
            }

            @Override
            public ObjectSpliterator<K> spliterator() {
                return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2FloatMap.this), 65);
            }
        };
    }

    @Override
    public FloatCollection values() {
        return new AbstractFloatCollection(){

            @Override
            public boolean contains(float k) {
                return AbstractObject2FloatMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractObject2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2FloatMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Object2FloatMap.Entry<K>> i;
                    {
                        this.i = Object2FloatMaps.fastIterator(AbstractObject2FloatMap.this);
                    }

                    @Override
                    public float nextFloat() {
                        return ((Object2FloatMap.Entry)this.i.next()).getFloatValue();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }

                    @Override
                    public void remove() {
                        this.i.remove();
                    }

                    @Override
                    public void forEachRemaining(FloatConsumer action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getFloatValue()));
                    }
                };
            }

            @Override
            public FloatSpliterator spliterator() {
                return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2FloatMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends K, ? extends Float> m) {
        if (m instanceof Object2FloatMap) {
            ObjectIterator i = Object2FloatMaps.fastIterator((Object2FloatMap)m);
            while (i.hasNext()) {
                Object2FloatMap.Entry e = (Object2FloatMap.Entry)i.next();
                this.put(e.getKey(), e.getFloatValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<K, Float>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, Float> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Object2FloatMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Object2FloatMap.Entry)i.next()).hashCode();
        }
        return h;
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
        if (m.size() != this.size()) {
            return false;
        }
        return this.object2FloatEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Object2FloatMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Object2FloatMap.Entry e = (Object2FloatMap.Entry)i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
            s.append("=>");
            s.append(String.valueOf(e.getFloatValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet<K>
    extends AbstractObjectSet<Object2FloatMap.Entry<K>> {
        protected final Object2FloatMap<K> map;

        public BasicEntrySet(Object2FloatMap<K> map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Object2FloatMap.Entry) {
                Object2FloatMap.Entry e = (Object2FloatMap.Entry)o;
                Object k = e.getKey();
                return this.map.containsKey(k) && Float.floatToIntBits(this.map.getFloat(k)) == Float.floatToIntBits(e.getFloatValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Float)) {
                return false;
            }
            return this.map.containsKey(k) && Float.floatToIntBits(this.map.getFloat(k)) == Float.floatToIntBits(((Float)value).floatValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Object2FloatMap.Entry) {
                Object2FloatMap.Entry e = (Object2FloatMap.Entry)o;
                return this.map.remove(e.getKey(), e.getFloatValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Float)) {
                return false;
            }
            float v = ((Float)value).floatValue();
            return this.map.remove(k, v);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public ObjectSpliterator<Object2FloatMap.Entry<K>> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry<K>
    implements Object2FloatMap.Entry<K> {
        protected K key;
        protected float value;

        public BasicEntry() {
        }

        public BasicEntry(K key, Float value) {
            this.key = key;
            this.value = value.floatValue();
        }

        public BasicEntry(K key, float value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public float getFloatValue() {
            return this.value;
        }

        @Override
        public float setValue(float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Object2FloatMap.Entry) {
                Object2FloatMap.Entry e = (Object2FloatMap.Entry)o;
                return Objects.equals(this.key, e.getKey()) && Float.floatToIntBits(this.value) == Float.floatToIntBits(e.getFloatValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Float)) {
                return false;
            }
            return Objects.equals(this.key, key) && Float.floatToIntBits(this.value) == Float.floatToIntBits(((Float)value).floatValue());
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ HashCommon.float2int(this.value);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

