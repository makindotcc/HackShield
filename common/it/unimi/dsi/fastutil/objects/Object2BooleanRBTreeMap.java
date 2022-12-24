/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2BooleanRBTreeMap$Submap.SubmapIterator
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2BooleanMap;
import it.unimi.dsi.fastutil.objects.AbstractObject2BooleanSortedMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanRBTreeMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanSortedMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedMap;

public class Object2BooleanRBTreeMap<K>
extends AbstractObject2BooleanSortedMap<K>
implements Serializable,
Cloneable {
    protected transient Entry<K> tree;
    protected int count;
    protected transient Entry<K> firstEntry;
    protected transient Entry<K> lastEntry;
    protected transient ObjectSortedSet<Object2BooleanMap.Entry<K>> entries;
    protected transient ObjectSortedSet<K> keys;
    protected transient BooleanCollection values;
    protected transient boolean modified;
    protected Comparator<? super K> storedComparator;
    protected transient Comparator<? super K> actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private transient boolean[] dirPath;
    private transient Entry<K>[] nodePath;

    public Object2BooleanRBTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = this.storedComparator;
    }

    public Object2BooleanRBTreeMap(Comparator<? super K> c) {
        this();
        this.storedComparator = c;
    }

    public Object2BooleanRBTreeMap(Map<? extends K, ? extends Boolean> m) {
        this();
        this.putAll((Map<? extends K, Boolean>)m);
    }

    public Object2BooleanRBTreeMap(SortedMap<K, Boolean> m) {
        this(m.comparator());
        this.putAll((Map<K, Boolean>)m);
    }

    public Object2BooleanRBTreeMap(Object2BooleanMap<? extends K> m) {
        this();
        this.putAll(m);
    }

    public Object2BooleanRBTreeMap(Object2BooleanSortedMap<K> m) {
        this(m.comparator());
        this.putAll(m);
    }

    public Object2BooleanRBTreeMap(K[] k, boolean[] v, Comparator<? super K> c) {
        this(c);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Object2BooleanRBTreeMap(K[] k, boolean[] v) {
        this(k, v, null);
    }

    final int compare(K k1, K k2) {
        return this.actualComparator == null ? ((Comparable)k1).compareTo(k2) : this.actualComparator.compare(k1, k2);
    }

    final Entry<K> findKey(K k) {
        int cmp;
        Entry<K> e = this.tree;
        while (e != null && (cmp = this.compare(k, e.key)) != 0) {
            e = cmp < 0 ? e.left() : e.right();
        }
        return e;
    }

    final Entry<K> locateKey(K k) {
        Entry<K> e = this.tree;
        Entry<K> last = this.tree;
        int cmp = 0;
        while (e != null && (cmp = this.compare(k, e.key)) != 0) {
            last = e;
            e = cmp < 0 ? e.left() : e.right();
        }
        return cmp == 0 ? e : last;
    }

    private void allocatePaths() {
        this.dirPath = new boolean[64];
        this.nodePath = new Entry[64];
    }

    @Override
    public boolean put(K k, boolean v) {
        Entry<K> e = this.add(k);
        boolean oldValue = e.value;
        e.value = v;
        return oldValue;
    }

    private Entry<K> add(K k) {
        Entry<K> e;
        Objects.requireNonNull(k);
        this.modified = false;
        int maxDepth = 0;
        if (this.tree == null) {
            ++this.count;
            this.firstEntry = new Entry<K>(k, this.defRetValue);
            this.lastEntry = this.firstEntry;
            this.tree = this.firstEntry;
            e = this.firstEntry;
        } else {
            Entry<K> p = this.tree;
            int i = 0;
            while (true) {
                int cmp;
                if ((cmp = this.compare(k, p.key)) == 0) {
                    while (i-- != 0) {
                        this.nodePath[i] = null;
                    }
                    return p;
                }
                this.nodePath[i] = p;
                this.dirPath[i++] = cmp > 0;
                if (this.dirPath[i++]) {
                    if (p.succ()) {
                        ++this.count;
                        e = new Entry<K>(k, this.defRetValue);
                        if (p.right == null) {
                            this.lastEntry = e;
                        }
                        e.left = p;
                        e.right = p.right;
                        p.right(e);
                        break;
                    }
                    p = p.right;
                    continue;
                }
                if (p.pred()) {
                    ++this.count;
                    e = new Entry<K>(k, this.defRetValue);
                    if (p.left == null) {
                        this.firstEntry = e;
                    }
                    e.right = p;
                    e.left = p.left;
                    p.left(e);
                    break;
                }
                p = p.left;
            }
            this.modified = true;
            maxDepth = i--;
            while (i > 0 && !this.nodePath[i].black()) {
                Entry x;
                Entry y;
                if (!this.dirPath[i - 1]) {
                    y = this.nodePath[i - 1].right;
                    if (!this.nodePath[i - 1].succ() && !y.black()) {
                        this.nodePath[i].black(true);
                        y.black(true);
                        this.nodePath[i - 1].black(false);
                        i -= 2;
                        continue;
                    }
                    if (!this.dirPath[i]) {
                        y = this.nodePath[i];
                    } else {
                        x = this.nodePath[i];
                        y = x.right;
                        x.right = y.left;
                        y.left = x;
                        this.nodePath[i - 1].left = y;
                        if (y.pred()) {
                            y.pred(false);
                            x.succ(y);
                        }
                    }
                    x = this.nodePath[i - 1];
                    x.black(false);
                    y.black(true);
                    x.left = y.right;
                    y.right = x;
                    if (i < 2) {
                        this.tree = y;
                    } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = y;
                    } else {
                        this.nodePath[i - 2].left = y;
                    }
                    if (!y.succ()) break;
                    y.succ(false);
                    x.pred(y);
                    break;
                }
                y = this.nodePath[i - 1].left;
                if (!this.nodePath[i - 1].pred() && !y.black()) {
                    this.nodePath[i].black(true);
                    y.black(true);
                    this.nodePath[i - 1].black(false);
                    i -= 2;
                    continue;
                }
                if (this.dirPath[i]) {
                    y = this.nodePath[i];
                } else {
                    x = this.nodePath[i];
                    y = x.left;
                    x.left = y.right;
                    y.right = x;
                    this.nodePath[i - 1].right = y;
                    if (y.succ()) {
                        y.succ(false);
                        x.pred(y);
                    }
                }
                x = this.nodePath[i - 1];
                x.black(false);
                y.black(true);
                x.right = y.left;
                y.left = x;
                if (i < 2) {
                    this.tree = y;
                } else if (this.dirPath[i - 2]) {
                    this.nodePath[i - 2].right = y;
                } else {
                    this.nodePath[i - 2].left = y;
                }
                if (!y.pred()) break;
                y.pred(false);
                x.succ(y);
                break;
            }
        }
        this.tree.black(true);
        while (maxDepth-- != 0) {
            this.nodePath[maxDepth] = null;
        }
        return e;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    @Override
    public boolean removeBoolean(Object k) {
        block69: {
            block66: {
                block68: {
                    block64: {
                        block67: {
                            block65: {
                                block62: {
                                    block63: {
                                        this.modified = false;
                                        if (this.tree == null) {
                                            return this.defRetValue;
                                        }
                                        p = this.tree;
                                        i = 0;
                                        kk = k;
                                        do lbl-1000:
                                        // 3 sources

                                        {
                                            if ((cmp = this.compare(kk, p.key)) == 0) {
                                                if (p.left != null) break block62;
                                                break block63;
                                            }
                                            this.dirPath[i] = cmp > 0;
                                            this.nodePath[i] = p;
                                            if (!this.dirPath[i++]) continue;
                                            if ((p = p.right()) != null) ** GOTO lbl-1000
                                            while (i-- != 0) {
                                                this.nodePath[i] = null;
                                            }
                                            return this.defRetValue;
                                        } while ((p = p.left()) != null);
                                        while (i-- != 0) {
                                            this.nodePath[i] = null;
                                        }
                                        return this.defRetValue;
                                    }
                                    this.firstEntry = p.next();
                                }
                                if (p.right == null) {
                                    this.lastEntry = p.prev();
                                }
                                if (!p.succ()) break block65;
                                if (p.pred()) {
                                    if (i == 0) {
                                        this.tree = p.left;
                                    } else if (this.dirPath[i - 1]) {
                                        this.nodePath[i - 1].succ(p.right);
                                    } else {
                                        this.nodePath[i - 1].pred(p.left);
                                    }
                                } else {
                                    p.prev().right = p.right;
                                    if (i == 0) {
                                        this.tree = p.left;
                                    } else if (this.dirPath[i - 1]) {
                                        this.nodePath[i - 1].right = p.left;
                                    } else {
                                        this.nodePath[i - 1].left = p.left;
                                    }
                                }
                                break block66;
                            }
                            r = p.right;
                            if (!r.pred()) break block67;
                            r.left = p.left;
                            r.pred(p.pred());
                            if (!r.pred()) {
                                r.prev().right = r;
                            }
                            if (i == 0) {
                                this.tree = r;
                            } else if (this.dirPath[i - 1]) {
                                this.nodePath[i - 1].right = r;
                            } else {
                                this.nodePath[i - 1].left = r;
                            }
                            color = r.black();
                            r.black(p.black());
                            p.black(color);
                            this.dirPath[i] = true;
                            this.nodePath[i++] = r;
                            break block66;
                        }
                        j = i++;
                        while (true) {
                            this.dirPath[i] = false;
                            this.nodePath[i++] = r;
                            s = r.left;
                            if (s.pred()) {
                                this.dirPath[j] = true;
                                this.nodePath[j] = s;
                                if (s.succ()) {
                                    break;
                                }
                                break block64;
                            }
                            r = s;
                        }
                        r.pred(s);
                        break block68;
                    }
                    r.left = s.right;
                }
                s.left = p.left;
                if (!p.pred()) {
                    p.prev().right = s;
                    s.pred(false);
                }
                s.right(p.right);
                color = s.black();
                s.black(p.black());
                p.black(color);
                if (j == 0) {
                    this.tree = s;
                } else if (this.dirPath[j - 1]) {
                    this.nodePath[j - 1].right = s;
                } else {
                    this.nodePath[j - 1].left = s;
                }
            }
            maxDepth = i;
            if (!p.black()) break block69;
            while (i > 0) {
                if (this.dirPath[i - 1] && !this.nodePath[i - 1].succ() || !this.dirPath[i - 1] && !this.nodePath[i - 1].pred()) {
                    v0 = x = this.dirPath[i - 1] != false ? this.nodePath[i - 1].right : this.nodePath[i - 1].left;
                    if (!x.black()) {
                        x.black(true);
                        break;
                    }
                }
                if (this.dirPath[i - 1]) ** GOTO lbl161
                w = this.nodePath[i - 1].right;
                if (!w.black()) {
                    w.black(true);
                    this.nodePath[i - 1].black(false);
                    this.nodePath[i - 1].right = w.left;
                    w.left = this.nodePath[i - 1];
                    if (i < 2) {
                        this.tree = w;
                    } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = w;
                    } else {
                        this.nodePath[i - 2].left = w;
                    }
                    this.nodePath[i] = this.nodePath[i - 1];
                    this.dirPath[i] = false;
                    this.nodePath[i - 1] = w;
                    if (maxDepth == i++) {
                        ++maxDepth;
                    }
                    w = this.nodePath[i - 1].right;
                }
                if ((w.pred() || w.left.black()) && (w.succ() || w.right.black())) {
                    w.black(false);
                } else {
                    if (w.succ() || w.right.black()) {
                        y = w.left;
                        y.black(true);
                        w.black(false);
                        w.left = y.right;
                        y.right = w;
                        this.nodePath[i - 1].right = y;
                        w = this.nodePath[i - 1].right;
                        if (w.succ()) {
                            w.succ(false);
                            w.right.pred(w);
                        }
                    }
                    w.black(this.nodePath[i - 1].black());
                    this.nodePath[i - 1].black(true);
                    w.right.black(true);
                    this.nodePath[i - 1].right = w.left;
                    w.left = this.nodePath[i - 1];
                    if (i < 2) {
                        this.tree = w;
                    } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = w;
                    } else {
                        this.nodePath[i - 2].left = w;
                    }
                    if (!w.pred()) break;
                    w.pred(false);
                    this.nodePath[i - 1].succ(w);
                    break;
lbl161:
                    // 1 sources

                    w = this.nodePath[i - 1].left;
                    if (!w.black()) {
                        w.black(true);
                        this.nodePath[i - 1].black(false);
                        this.nodePath[i - 1].left = w.right;
                        w.right = this.nodePath[i - 1];
                        if (i < 2) {
                            this.tree = w;
                        } else if (this.dirPath[i - 2]) {
                            this.nodePath[i - 2].right = w;
                        } else {
                            this.nodePath[i - 2].left = w;
                        }
                        this.nodePath[i] = this.nodePath[i - 1];
                        this.dirPath[i] = true;
                        this.nodePath[i - 1] = w;
                        if (maxDepth == i++) {
                            ++maxDepth;
                        }
                        w = this.nodePath[i - 1].left;
                    }
                    if ((w.pred() || w.left.black()) && (w.succ() || w.right.black())) {
                        w.black(false);
                    } else {
                        if (w.pred() || w.left.black()) {
                            y = w.right;
                            y.black(true);
                            w.black(false);
                            w.right = y.left;
                            y.left = w;
                            this.nodePath[i - 1].left = y;
                            w = this.nodePath[i - 1].left;
                            if (w.pred()) {
                                w.pred(false);
                                w.left.succ(w);
                            }
                        }
                        w.black(this.nodePath[i - 1].black());
                        this.nodePath[i - 1].black(true);
                        w.left.black(true);
                        this.nodePath[i - 1].left = w.right;
                        w.right = this.nodePath[i - 1];
                        if (i < 2) {
                            this.tree = w;
                        } else if (this.dirPath[i - 2]) {
                            this.nodePath[i - 2].right = w;
                        } else {
                            this.nodePath[i - 2].left = w;
                        }
                        if (!w.succ()) break;
                        w.succ(false);
                        this.nodePath[i - 1].pred(w);
                        break;
                    }
                }
                --i;
            }
            if (this.tree != null) {
                this.tree.black(true);
            }
        }
        this.modified = true;
        --this.count;
        while (maxDepth-- != 0) {
            this.nodePath[maxDepth] = null;
        }
        return p.value;
    }

    @Override
    public boolean containsValue(boolean v) {
        ValueIterator i = new ValueIterator();
        int j = this.count;
        while (j-- != 0) {
            boolean ev = i.nextBoolean();
            if (ev != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        this.count = 0;
        this.tree = null;
        this.entries = null;
        this.values = null;
        this.keys = null;
        this.lastEntry = null;
        this.firstEntry = null;
    }

    @Override
    public boolean containsKey(Object k) {
        if (k == null) {
            return false;
        }
        return this.findKey(k) != null;
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }

    @Override
    public boolean getBoolean(Object k) {
        Entry<Object> e = this.findKey(k);
        return e == null ? this.defRetValue : e.value;
    }

    @Override
    public K firstKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return (K)this.firstEntry.key;
    }

    @Override
    public K lastKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return (K)this.lastEntry.key;
    }

    @Override
    public ObjectSortedSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Object2BooleanMap.Entry<K>>(){
                final Comparator<? super Object2BooleanMap.Entry<K>> comparator;
                {
                    this.comparator = Object2BooleanRBTreeMap.this.actualComparator == null ? (x, y) -> ((Comparable)x.getKey()).compareTo(y.getKey()) : (x, y) -> Object2BooleanRBTreeMap.this.actualComparator.compare(x.getKey(), y.getKey());
                }

                @Override
                public Comparator<? super Object2BooleanMap.Entry<K>> comparator() {
                    return this.comparator;
                }

                @Override
                public ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> iterator() {
                    return new EntryIterator();
                }

                @Override
                public ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> iterator(Object2BooleanMap.Entry<K> from) {
                    return new EntryIterator(from.getKey());
                }

                @Override
                public boolean contains(Object o) {
                    if (o == null || !(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry e = (Map.Entry)o;
                    if (e.getKey() == null) {
                        return false;
                    }
                    if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                        return false;
                    }
                    Entry f = Object2BooleanRBTreeMap.this.findKey(e.getKey());
                    return e.equals(f);
                }

                @Override
                public boolean remove(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry e = (Map.Entry)o;
                    if (e.getKey() == null) {
                        return false;
                    }
                    if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                        return false;
                    }
                    Entry f = Object2BooleanRBTreeMap.this.findKey(e.getKey());
                    if (f == null || f.getBooleanValue() != ((Boolean)e.getValue()).booleanValue()) {
                        return false;
                    }
                    Object2BooleanRBTreeMap.this.removeBoolean(f.key);
                    return true;
                }

                @Override
                public int size() {
                    return Object2BooleanRBTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Object2BooleanRBTreeMap.this.clear();
                }

                @Override
                public Object2BooleanMap.Entry<K> first() {
                    return Object2BooleanRBTreeMap.this.firstEntry;
                }

                @Override
                public Object2BooleanMap.Entry<K> last() {
                    return Object2BooleanRBTreeMap.this.lastEntry;
                }

                @Override
                public ObjectSortedSet<Object2BooleanMap.Entry<K>> subSet(Object2BooleanMap.Entry<K> from, Object2BooleanMap.Entry<K> to) {
                    return Object2BooleanRBTreeMap.this.subMap(from.getKey(), to.getKey()).object2BooleanEntrySet();
                }

                @Override
                public ObjectSortedSet<Object2BooleanMap.Entry<K>> headSet(Object2BooleanMap.Entry<K> to) {
                    return Object2BooleanRBTreeMap.this.headMap(to.getKey()).object2BooleanEntrySet();
                }

                @Override
                public ObjectSortedSet<Object2BooleanMap.Entry<K>> tailSet(Object2BooleanMap.Entry<K> from) {
                    return Object2BooleanRBTreeMap.this.tailMap(from.getKey()).object2BooleanEntrySet();
                }
            };
        }
        return this.entries;
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public BooleanCollection values() {
        if (this.values == null) {
            this.values = new AbstractBooleanCollection(){

                @Override
                public BooleanIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public boolean contains(boolean k) {
                    return Object2BooleanRBTreeMap.this.containsValue(k);
                }

                @Override
                public int size() {
                    return Object2BooleanRBTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Object2BooleanRBTreeMap.this.clear();
                }
            };
        }
        return this.values;
    }

    @Override
    public Comparator<? super K> comparator() {
        return this.actualComparator;
    }

    @Override
    public Object2BooleanSortedMap<K> headMap(K to) {
        return new Submap(null, true, to, false);
    }

    @Override
    public Object2BooleanSortedMap<K> tailMap(K from) {
        return new Submap(from, false, null, true);
    }

    @Override
    public Object2BooleanSortedMap<K> subMap(K from, K to) {
        return new Submap(from, false, to, false);
    }

    public Object2BooleanRBTreeMap<K> clone() {
        Object2BooleanRBTreeMap c;
        try {
            c = (Object2BooleanRBTreeMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.allocatePaths();
        if (this.count != 0) {
            Entry<K> rp = new Entry<K>();
            Entry rq = new Entry();
            Entry<K> p = rp;
            rp.left(this.tree);
            Entry q = rq;
            rq.pred(null);
            while (true) {
                Object e;
                if (!p.pred()) {
                    e = p.left.clone();
                    ((Entry)e).pred(q.left);
                    ((Entry)e).succ(q);
                    q.left(e);
                    p = p.left;
                    q = q.left;
                } else {
                    while (p.succ()) {
                        p = p.right;
                        if (p == null) {
                            q.right = null;
                            c.tree = rq.left;
                            c.firstEntry = c.tree;
                            while (c.firstEntry.left != null) {
                                c.firstEntry = c.firstEntry.left;
                            }
                            c.lastEntry = c.tree;
                            while (c.lastEntry.right != null) {
                                c.lastEntry = c.lastEntry.right;
                            }
                            return c;
                        }
                        q = q.right;
                    }
                    p = p.right;
                    q = q.right;
                }
                if (p.succ()) continue;
                e = p.right.clone();
                ((Entry)e).succ(q.right);
                ((Entry)e).pred(q);
                q.right(e);
            }
        }
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        int n = this.count;
        EntryIterator i = new EntryIterator();
        s.defaultWriteObject();
        while (n-- != 0) {
            Entry e = i.nextEntry();
            s.writeObject(e.key);
            s.writeBoolean(e.value);
        }
    }

    private Entry<K> readTree(ObjectInputStream s, int n, Entry<K> pred, Entry<K> succ) throws IOException, ClassNotFoundException {
        if (n == 1) {
            Entry<Object> top = new Entry<Object>(s.readObject(), s.readBoolean());
            top.pred(pred);
            top.succ(succ);
            top.black(true);
            return top;
        }
        if (n == 2) {
            Entry<Object> top = new Entry<Object>(s.readObject(), s.readBoolean());
            top.black(true);
            top.right(new Entry<Object>(s.readObject(), s.readBoolean()));
            top.right.pred(top);
            top.pred(pred);
            top.right.succ(succ);
            return top;
        }
        int rightN = n / 2;
        int leftN = n - rightN - 1;
        Entry top = new Entry();
        top.left(this.readTree(s, leftN, pred, top));
        top.key = s.readObject();
        top.value = s.readBoolean();
        top.black(true);
        top.right(this.readTree(s, rightN, top, succ));
        if (n + 2 == (n + 2 & -(n + 2))) {
            top.right.black(false);
        }
        return top;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.setActualComparator();
        this.allocatePaths();
        if (this.count != 0) {
            this.tree = this.readTree(s, this.count, null, null);
            Entry<K> e = this.tree;
            while (e.left() != null) {
                e = e.left();
            }
            this.firstEntry = e;
            e = this.tree;
            while (e.right() != null) {
                e = e.right();
            }
            this.lastEntry = e;
        }
    }

    private static final class Entry<K>
    extends AbstractObject2BooleanMap.BasicEntry<K>
    implements Cloneable {
        private static final int BLACK_MASK = 1;
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 0x40000000;
        Entry<K> left;
        Entry<K> right;
        int info;

        Entry() {
            super(null, false);
        }

        Entry(K k, boolean v) {
            super(k, v);
            this.info = -1073741824;
        }

        Entry<K> left() {
            return (this.info & 0x40000000) != 0 ? null : this.left;
        }

        Entry<K> right() {
            return (this.info & Integer.MIN_VALUE) != 0 ? null : this.right;
        }

        boolean pred() {
            return (this.info & 0x40000000) != 0;
        }

        boolean succ() {
            return (this.info & Integer.MIN_VALUE) != 0;
        }

        void pred(boolean pred) {
            this.info = pred ? (this.info |= 0x40000000) : (this.info &= 0xBFFFFFFF);
        }

        void succ(boolean succ) {
            this.info = succ ? (this.info |= Integer.MIN_VALUE) : (this.info &= Integer.MAX_VALUE);
        }

        void pred(Entry<K> pred) {
            this.info |= 0x40000000;
            this.left = pred;
        }

        void succ(Entry<K> succ) {
            this.info |= Integer.MIN_VALUE;
            this.right = succ;
        }

        void left(Entry<K> left) {
            this.info &= 0xBFFFFFFF;
            this.left = left;
        }

        void right(Entry<K> right) {
            this.info &= Integer.MAX_VALUE;
            this.right = right;
        }

        boolean black() {
            return (this.info & 1) != 0;
        }

        void black(boolean black) {
            this.info = black ? (this.info |= 1) : (this.info &= 0xFFFFFFFE);
        }

        Entry<K> next() {
            Entry<K> next = this.right;
            if ((this.info & Integer.MIN_VALUE) == 0) {
                while ((next.info & 0x40000000) == 0) {
                    next = next.left;
                }
            }
            return next;
        }

        Entry<K> prev() {
            Entry<K> prev = this.left;
            if ((this.info & 0x40000000) == 0) {
                while ((prev.info & Integer.MIN_VALUE) == 0) {
                    prev = prev.right;
                }
            }
            return prev;
        }

        @Override
        public boolean setValue(boolean value) {
            boolean oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public Entry<K> clone() {
            Entry c;
            try {
                c = (Entry)super.clone();
            }
            catch (CloneNotSupportedException cantHappen) {
                throw new InternalError();
            }
            c.key = this.key;
            c.value = this.value;
            c.info = this.info;
            return c;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Objects.equals(this.key, e.getKey()) && this.value == (Boolean)e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key.hashCode() ^ (this.value ? 1231 : 1237);
        }

        @Override
        public String toString() {
            return this.key + "=>" + this.value;
        }
    }

    private final class ValueIterator
    extends TreeIterator
    implements BooleanListIterator {
        private ValueIterator() {
        }

        @Override
        public boolean nextBoolean() {
            return this.nextEntry().value;
        }

        @Override
        public boolean previousBoolean() {
            return this.previousEntry().value;
        }
    }

    private class KeySet
    extends AbstractObject2BooleanSortedMap.KeySet {
        private KeySet() {
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeyIterator(from);
        }
    }

    private final class Submap
    extends AbstractObject2BooleanSortedMap<K>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        K from;
        K to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Object2BooleanMap.Entry<K>> entries;
        protected transient ObjectSortedSet<K> keys;
        protected transient BooleanCollection values;

        public Submap(K from, boolean bottom, K to, boolean top) {
            if (!bottom && !top && Object2BooleanRBTreeMap.this.compare(from, to) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Object2BooleanRBTreeMap.this.defRetValue;
        }

        @Override
        public void clear() {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                i.nextEntry();
                i.remove();
            }
        }

        final boolean in(K k) {
            return !(!this.bottom && Object2BooleanRBTreeMap.this.compare(k, this.from) < 0 || !this.top && Object2BooleanRBTreeMap.this.compare(k, this.to) >= 0);
        }

        @Override
        public ObjectSortedSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Object2BooleanMap.Entry<K>>(){

                    @Override
                    public ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override
                    public ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> iterator(Object2BooleanMap.Entry<K> from) {
                        return new SubmapEntryIterator(from.getKey());
                    }

                    @Override
                    public Comparator<? super Object2BooleanMap.Entry<K>> comparator() {
                        return Object2BooleanRBTreeMap.this.object2BooleanEntrySet().comparator();
                    }

                    @Override
                    public boolean contains(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                            return false;
                        }
                        Entry f = Object2BooleanRBTreeMap.this.findKey(e.getKey());
                        return f != null && Submap.this.in(f.key) && e.equals(f);
                    }

                    @Override
                    public boolean remove(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                            return false;
                        }
                        Entry f = Object2BooleanRBTreeMap.this.findKey(e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                            Submap.this.removeBoolean(f.key);
                        }
                        return f != null;
                    }

                    @Override
                    public int size() {
                        int c = 0;
                        ObjectIterator i = this.iterator();
                        while (i.hasNext()) {
                            ++c;
                            i.next();
                        }
                        return c;
                    }

                    @Override
                    public boolean isEmpty() {
                        return !new SubmapIterator().hasNext();
                    }

                    @Override
                    public void clear() {
                        Submap.this.clear();
                    }

                    @Override
                    public Object2BooleanMap.Entry<K> first() {
                        return Submap.this.firstEntry();
                    }

                    @Override
                    public Object2BooleanMap.Entry<K> last() {
                        return Submap.this.lastEntry();
                    }

                    @Override
                    public ObjectSortedSet<Object2BooleanMap.Entry<K>> subSet(Object2BooleanMap.Entry<K> from, Object2BooleanMap.Entry<K> to) {
                        return Submap.this.subMap(from.getKey(), to.getKey()).object2BooleanEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Object2BooleanMap.Entry<K>> headSet(Object2BooleanMap.Entry<K> to) {
                        return Submap.this.headMap(to.getKey()).object2BooleanEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Object2BooleanMap.Entry<K>> tailSet(Object2BooleanMap.Entry<K> from) {
                        return Submap.this.tailMap(from.getKey()).object2BooleanEntrySet();
                    }
                };
            }
            return this.entries;
        }

        @Override
        public ObjectSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = new KeySet();
            }
            return this.keys;
        }

        @Override
        public BooleanCollection values() {
            if (this.values == null) {
                this.values = new AbstractBooleanCollection(){

                    @Override
                    public BooleanIterator iterator() {
                        return new SubmapValueIterator();
                    }

                    @Override
                    public boolean contains(boolean k) {
                        return Submap.this.containsValue(k);
                    }

                    @Override
                    public int size() {
                        return Submap.this.size();
                    }

                    @Override
                    public void clear() {
                        Submap.this.clear();
                    }
                };
            }
            return this.values;
        }

        @Override
        public boolean containsKey(Object k) {
            if (k == null) {
                return false;
            }
            return this.in(k) && Object2BooleanRBTreeMap.this.containsKey(k);
        }

        @Override
        public boolean containsValue(boolean v) {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                boolean ev = i.nextEntry().value;
                if (ev != v) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean getBoolean(Object k) {
            Entry<Object> e;
            Object kk = k;
            return this.in(kk) && (e = Object2BooleanRBTreeMap.this.findKey(kk)) != null ? e.value : this.defRetValue;
        }

        @Override
        public boolean put(K k, boolean v) {
            Object2BooleanRBTreeMap.this.modified = false;
            if (!this.in(k)) {
                throw new IllegalArgumentException("Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            boolean oldValue = Object2BooleanRBTreeMap.this.put(k, v);
            return Object2BooleanRBTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        public boolean removeBoolean(Object k) {
            Object2BooleanRBTreeMap.this.modified = false;
            if (!this.in(k)) {
                return this.defRetValue;
            }
            boolean oldValue = Object2BooleanRBTreeMap.this.removeBoolean(k);
            return Object2BooleanRBTreeMap.this.modified ? oldValue : this.defRetValue;
        }

        @Override
        public int size() {
            SubmapIterator i = new SubmapIterator();
            int n = 0;
            while (i.hasNext()) {
                ++n;
                i.nextEntry();
            }
            return n;
        }

        @Override
        public boolean isEmpty() {
            return !new SubmapIterator().hasNext();
        }

        @Override
        public Comparator<? super K> comparator() {
            return Object2BooleanRBTreeMap.this.actualComparator;
        }

        @Override
        public Object2BooleanSortedMap<K> headMap(K to) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to, false);
            }
            return Object2BooleanRBTreeMap.this.compare(to, this.to) < 0 ? new Submap(this.from, this.bottom, to, false) : this;
        }

        @Override
        public Object2BooleanSortedMap<K> tailMap(K from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return Object2BooleanRBTreeMap.this.compare(from, this.from) > 0 ? new Submap(from, false, this.to, this.top) : this;
        }

        @Override
        public Object2BooleanSortedMap<K> subMap(K from, K to) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to, false);
            }
            if (!this.top) {
                Object k = to = Object2BooleanRBTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }
            if (!this.bottom) {
                Object k = from = Object2BooleanRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to == this.to) {
                return this;
            }
            return new Submap(from, false, to, false);
        }

        public Entry<K> firstEntry() {
            Entry e;
            if (Object2BooleanRBTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e = Object2BooleanRBTreeMap.this.firstEntry;
            } else {
                e = Object2BooleanRBTreeMap.this.locateKey(this.from);
                if (Object2BooleanRBTreeMap.this.compare(e.key, this.from) < 0) {
                    e = e.next();
                }
            }
            if (e == null || !this.top && Object2BooleanRBTreeMap.this.compare(e.key, this.to) >= 0) {
                return null;
            }
            return e;
        }

        public Entry<K> lastEntry() {
            Entry e;
            if (Object2BooleanRBTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e = Object2BooleanRBTreeMap.this.lastEntry;
            } else {
                e = Object2BooleanRBTreeMap.this.locateKey(this.to);
                if (Object2BooleanRBTreeMap.this.compare(e.key, this.to) >= 0) {
                    e = e.prev();
                }
            }
            if (e == null || !this.bottom && Object2BooleanRBTreeMap.this.compare(e.key, this.from) < 0) {
                return null;
            }
            return e;
        }

        @Override
        public K firstKey() {
            Entry e = this.firstEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        @Override
        public K lastKey() {
            Entry e = this.lastEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        private class SubmapIterator
        extends TreeIterator {
            SubmapIterator() {
                this.next = Submap.this.firstEntry();
            }

            /*
             * Enabled aggressive block sorting
             */
            SubmapIterator(K k) {
                this();
                if (this.next == null) return;
                if (!submap.bottom && submap.Object2BooleanRBTreeMap.this.compare(k, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!submap.top) {
                    this.prev = submap.lastEntry();
                    if (submap.Object2BooleanRBTreeMap.this.compare(k, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = submap.Object2BooleanRBTreeMap.this.locateKey(k);
                if (submap.Object2BooleanRBTreeMap.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Object2BooleanRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Object2BooleanRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

        private class KeySet
        extends AbstractObject2BooleanSortedMap.KeySet {
            private KeySet() {
            }

            @Override
            public ObjectBidirectionalIterator<K> iterator() {
                return new SubmapKeyIterator();
            }

            @Override
            public ObjectBidirectionalIterator<K> iterator(K from) {
                return new SubmapKeyIterator(from);
            }
        }

        private final class SubmapValueIterator
        extends it.unimi.dsi.fastutil.objects.Object2BooleanRBTreeMap$Submap.SubmapIterator
        implements BooleanListIterator {
            private SubmapValueIterator() {
            }

            @Override
            public boolean nextBoolean() {
                return this.nextEntry().value;
            }

            @Override
            public boolean previousBoolean() {
                return this.previousEntry().value;
            }
        }

        private final class SubmapKeyIterator
        extends it.unimi.dsi.fastutil.objects.Object2BooleanRBTreeMap$Submap.SubmapIterator
        implements ObjectListIterator<K> {
            public SubmapKeyIterator() {
            }

            public SubmapKeyIterator(K from) {
                super(from);
            }

            @Override
            public K next() {
                return this.nextEntry().key;
            }

            @Override
            public K previous() {
                return this.previousEntry().key;
            }
        }

        private class SubmapEntryIterator
        extends it.unimi.dsi.fastutil.objects.Object2BooleanRBTreeMap$Submap.SubmapIterator
        implements ObjectListIterator<Object2BooleanMap.Entry<K>> {
            SubmapEntryIterator() {
            }

            SubmapEntryIterator(K k) {
                super(k);
            }

            @Override
            public Object2BooleanMap.Entry<K> next() {
                return this.nextEntry();
            }

            @Override
            public Object2BooleanMap.Entry<K> previous() {
                return this.previousEntry();
            }
        }
    }

    private class EntryIterator
    extends TreeIterator
    implements ObjectListIterator<Object2BooleanMap.Entry<K>> {
        EntryIterator() {
        }

        EntryIterator(K k) {
            super(k);
        }

        @Override
        public Object2BooleanMap.Entry<K> next() {
            return this.nextEntry();
        }

        @Override
        public Object2BooleanMap.Entry<K> previous() {
            return this.previousEntry();
        }
    }

    private final class KeyIterator
    extends TreeIterator
    implements ObjectListIterator<K> {
        public KeyIterator() {
        }

        public KeyIterator(K k) {
            super(k);
        }

        @Override
        public K next() {
            return this.nextEntry().key;
        }

        @Override
        public K previous() {
            return this.previousEntry().key;
        }
    }

    private class TreeIterator {
        Entry<K> prev;
        Entry<K> next;
        Entry<K> curr;
        int index = 0;

        TreeIterator() {
            this.next = Object2BooleanRBTreeMap.this.firstEntry;
        }

        TreeIterator(K k) {
            this.next = Object2BooleanRBTreeMap.this.locateKey(k);
            if (this.next != null) {
                if (Object2BooleanRBTreeMap.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                } else {
                    this.prev = this.next.prev();
                }
            }
        }

        public boolean hasNext() {
            return this.next != null;
        }

        public boolean hasPrevious() {
            return this.prev != null;
        }

        void updateNext() {
            this.next = this.next.next();
        }

        Entry<K> nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.prev = this.next;
            this.curr = this.prev;
            ++this.index;
            this.updateNext();
            return this.curr;
        }

        void updatePrevious() {
            this.prev = this.prev.prev();
        }

        Entry<K> previousEntry() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.next = this.prev;
            this.curr = this.next;
            --this.index;
            this.updatePrevious();
            return this.curr;
        }

        public int nextIndex() {
            return this.index;
        }

        public int previousIndex() {
            return this.index - 1;
        }

        public void remove() {
            if (this.curr == null) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
            }
            this.prev = this.curr;
            this.next = this.prev;
            this.updatePrevious();
            this.updateNext();
            Object2BooleanRBTreeMap.this.removeBoolean(this.curr.key);
            this.curr = null;
        }

        public int skip(int n) {
            int i = n;
            while (i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n - i - 1;
        }

        public int back(int n) {
            int i = n;
            while (i-- != 0 && this.hasPrevious()) {
                this.previousEntry();
            }
            return n - i - 1;
        }
    }
}

