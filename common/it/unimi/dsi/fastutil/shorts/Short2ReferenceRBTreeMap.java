/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.shorts.Short2ReferenceRBTreeMap$Submap.SubmapIterator
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ReferenceMap;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ReferenceSortedMap;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceMap;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceRBTreeMap;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceSortedMap;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortComparators;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Short2ReferenceRBTreeMap<V>
extends AbstractShort2ReferenceSortedMap<V>
implements Serializable,
Cloneable {
    protected transient Entry<V> tree;
    protected int count;
    protected transient Entry<V> firstEntry;
    protected transient Entry<V> lastEntry;
    protected transient ObjectSortedSet<Short2ReferenceMap.Entry<V>> entries;
    protected transient ShortSortedSet keys;
    protected transient ReferenceCollection<V> values;
    protected transient boolean modified;
    protected Comparator<? super Short> storedComparator;
    protected transient ShortComparator actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private transient boolean[] dirPath;
    private transient Entry<V>[] nodePath;

    public Short2ReferenceRBTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = ShortComparators.asShortComparator(this.storedComparator);
    }

    public Short2ReferenceRBTreeMap(Comparator<? super Short> c) {
        this();
        this.storedComparator = c;
    }

    public Short2ReferenceRBTreeMap(Map<? extends Short, ? extends V> m) {
        this();
        this.putAll((Map<Short, ? extends V>)m);
    }

    public Short2ReferenceRBTreeMap(SortedMap<Short, V> m) {
        this(m.comparator());
        this.putAll((Map<Short, V>)m);
    }

    public Short2ReferenceRBTreeMap(Short2ReferenceMap<? extends V> m) {
        this();
        this.putAll(m);
    }

    public Short2ReferenceRBTreeMap(Short2ReferenceSortedMap<V> m) {
        this(m.comparator());
        this.putAll(m);
    }

    public Short2ReferenceRBTreeMap(short[] k, V[] v, Comparator<? super Short> c) {
        this(c);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Short2ReferenceRBTreeMap(short[] k, V[] v) {
        this(k, v, null);
    }

    final int compare(short k1, short k2) {
        return this.actualComparator == null ? Short.compare(k1, k2) : this.actualComparator.compare(k1, k2);
    }

    final Entry<V> findKey(short k) {
        int cmp;
        Entry<V> e = this.tree;
        while (e != null && (cmp = this.compare(k, e.key)) != 0) {
            e = cmp < 0 ? e.left() : e.right();
        }
        return e;
    }

    final Entry<V> locateKey(short k) {
        Entry<V> e = this.tree;
        Entry<V> last = this.tree;
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
    public V put(short k, V v) {
        Entry<V> e = this.add(k);
        Object oldValue = e.value;
        e.value = v;
        return (V)oldValue;
    }

    private Entry<V> add(short k) {
        Entry<Object> e;
        this.modified = false;
        int maxDepth = 0;
        if (this.tree == null) {
            ++this.count;
            this.firstEntry = new Entry<Object>(k, this.defRetValue);
            this.lastEntry = this.firstEntry;
            this.tree = this.firstEntry;
            e = this.firstEntry;
        } else {
            Entry<Object> p = this.tree;
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
                        e = new Entry<Object>(k, this.defRetValue);
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
                    e = new Entry<Object>(k, this.defRetValue);
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
    public V remove(short k) {
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
                                            return (V)this.defRetValue;
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
                                            return (V)this.defRetValue;
                                        } while ((p = p.left()) != null);
                                        while (i-- != 0) {
                                            this.nodePath[i] = null;
                                        }
                                        return (V)this.defRetValue;
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
        return (V)p.value;
    }

    @Override
    public boolean containsValue(Object v) {
        ValueIterator i = new ValueIterator();
        int j = this.count;
        while (j-- != 0) {
            Object ev = i.next();
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
    public boolean containsKey(short k) {
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
    public V get(short k) {
        Entry<V> e = this.findKey(k);
        return (V)(e == null ? this.defRetValue : e.value);
    }

    @Override
    public short firstShortKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.firstEntry.key;
    }

    @Override
    public short lastShortKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.lastEntry.key;
    }

    @Override
    public ObjectSortedSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Short2ReferenceMap.Entry<V>>(){
                final Comparator<? super Short2ReferenceMap.Entry<V>> comparator;
                {
                    this.comparator = Short2ReferenceRBTreeMap.this.actualComparator == null ? (x, y) -> Short.compare(x.getShortKey(), y.getShortKey()) : (x, y) -> Short2ReferenceRBTreeMap.this.actualComparator.compare(x.getShortKey(), y.getShortKey());
                }

                @Override
                public Comparator<? super Short2ReferenceMap.Entry<V>> comparator() {
                    return this.comparator;
                }

                @Override
                public ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> iterator() {
                    return new EntryIterator();
                }

                @Override
                public ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> iterator(Short2ReferenceMap.Entry<V> from) {
                    return new EntryIterator(from.getShortKey());
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
                    if (!(e.getKey() instanceof Short)) {
                        return false;
                    }
                    Entry f = Short2ReferenceRBTreeMap.this.findKey((Short)e.getKey());
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
                    if (!(e.getKey() instanceof Short)) {
                        return false;
                    }
                    Entry f = Short2ReferenceRBTreeMap.this.findKey((Short)e.getKey());
                    if (f == null || f.getValue() != e.getValue()) {
                        return false;
                    }
                    Short2ReferenceRBTreeMap.this.remove(f.key);
                    return true;
                }

                @Override
                public int size() {
                    return Short2ReferenceRBTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Short2ReferenceRBTreeMap.this.clear();
                }

                @Override
                public Short2ReferenceMap.Entry<V> first() {
                    return Short2ReferenceRBTreeMap.this.firstEntry;
                }

                @Override
                public Short2ReferenceMap.Entry<V> last() {
                    return Short2ReferenceRBTreeMap.this.lastEntry;
                }

                @Override
                public ObjectSortedSet<Short2ReferenceMap.Entry<V>> subSet(Short2ReferenceMap.Entry<V> from, Short2ReferenceMap.Entry<V> to) {
                    return Short2ReferenceRBTreeMap.this.subMap(from.getShortKey(), to.getShortKey()).short2ReferenceEntrySet();
                }

                @Override
                public ObjectSortedSet<Short2ReferenceMap.Entry<V>> headSet(Short2ReferenceMap.Entry<V> to) {
                    return Short2ReferenceRBTreeMap.this.headMap(to.getShortKey()).short2ReferenceEntrySet();
                }

                @Override
                public ObjectSortedSet<Short2ReferenceMap.Entry<V>> tailSet(Short2ReferenceMap.Entry<V> from) {
                    return Short2ReferenceRBTreeMap.this.tailMap(from.getShortKey()).short2ReferenceEntrySet();
                }
            };
        }
        return this.entries;
    }

    @Override
    public ShortSortedSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ReferenceCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractReferenceCollection<V>(){

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public boolean contains(Object k) {
                    return Short2ReferenceRBTreeMap.this.containsValue(k);
                }

                @Override
                public int size() {
                    return Short2ReferenceRBTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Short2ReferenceRBTreeMap.this.clear();
                }
            };
        }
        return this.values;
    }

    @Override
    public ShortComparator comparator() {
        return this.actualComparator;
    }

    @Override
    public Short2ReferenceSortedMap<V> headMap(short to) {
        return new Submap(0, true, to, false);
    }

    @Override
    public Short2ReferenceSortedMap<V> tailMap(short from) {
        return new Submap(from, false, 0, true);
    }

    @Override
    public Short2ReferenceSortedMap<V> subMap(short from, short to) {
        return new Submap(from, false, to, false);
    }

    public Short2ReferenceRBTreeMap<V> clone() {
        Short2ReferenceRBTreeMap c;
        try {
            c = (Short2ReferenceRBTreeMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.allocatePaths();
        if (this.count != 0) {
            Entry<V> rp = new Entry<V>();
            Entry rq = new Entry();
            Entry<V> p = rp;
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
            s.writeShort(e.key);
            s.writeObject(e.value);
        }
    }

    private Entry<V> readTree(ObjectInputStream s, int n, Entry<V> pred, Entry<V> succ) throws IOException, ClassNotFoundException {
        if (n == 1) {
            Entry<Object> top = new Entry<Object>(s.readShort(), s.readObject());
            top.pred(pred);
            top.succ(succ);
            top.black(true);
            return top;
        }
        if (n == 2) {
            Entry<Object> top = new Entry<Object>(s.readShort(), s.readObject());
            top.black(true);
            top.right(new Entry<Object>(s.readShort(), s.readObject()));
            top.right.pred(top);
            top.pred(pred);
            top.right.succ(succ);
            return top;
        }
        int rightN = n / 2;
        int leftN = n - rightN - 1;
        Entry top = new Entry();
        top.left(this.readTree(s, leftN, pred, top));
        top.key = s.readShort();
        top.value = s.readObject();
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
            Entry<V> e = this.tree;
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

    private static final class Entry<V>
    extends AbstractShort2ReferenceMap.BasicEntry<V>
    implements Cloneable {
        private static final int BLACK_MASK = 1;
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 0x40000000;
        Entry<V> left;
        Entry<V> right;
        int info;

        Entry() {
            super((short)0, null);
        }

        Entry(short k, V v) {
            super(k, v);
            this.info = -1073741824;
        }

        Entry<V> left() {
            return (this.info & 0x40000000) != 0 ? null : this.left;
        }

        Entry<V> right() {
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

        void pred(Entry<V> pred) {
            this.info |= 0x40000000;
            this.left = pred;
        }

        void succ(Entry<V> succ) {
            this.info |= Integer.MIN_VALUE;
            this.right = succ;
        }

        void left(Entry<V> left) {
            this.info &= 0xBFFFFFFF;
            this.left = left;
        }

        void right(Entry<V> right) {
            this.info &= Integer.MAX_VALUE;
            this.right = right;
        }

        boolean black() {
            return (this.info & 1) != 0;
        }

        void black(boolean black) {
            this.info = black ? (this.info |= 1) : (this.info &= 0xFFFFFFFE);
        }

        Entry<V> next() {
            Entry<V> next = this.right;
            if ((this.info & Integer.MIN_VALUE) == 0) {
                while ((next.info & 0x40000000) == 0) {
                    next = next.left;
                }
            }
            return next;
        }

        Entry<V> prev() {
            Entry<V> prev = this.left;
            if ((this.info & 0x40000000) == 0) {
                while ((prev.info & Integer.MIN_VALUE) == 0) {
                    prev = prev.right;
                }
            }
            return prev;
        }

        @Override
        public V setValue(V value) {
            Object oldValue = this.value;
            this.value = value;
            return (V)oldValue;
        }

        public Entry<V> clone() {
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
            return this.key == (Short)e.getKey() && this.value == e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
        }

        @Override
        public String toString() {
            return this.key + "=>" + this.value;
        }
    }

    private final class ValueIterator
    extends TreeIterator
    implements ObjectListIterator<V> {
        private ValueIterator() {
        }

        @Override
        public V next() {
            return this.nextEntry().value;
        }

        @Override
        public V previous() {
            return this.previousEntry().value;
        }
    }

    private class KeySet
    extends AbstractShort2ReferenceSortedMap.KeySet {
        private KeySet() {
        }

        @Override
        public ShortBidirectionalIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public ShortBidirectionalIterator iterator(short from) {
            return new KeyIterator(from);
        }
    }

    private final class Submap
    extends AbstractShort2ReferenceSortedMap<V>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        short from;
        short to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Short2ReferenceMap.Entry<V>> entries;
        protected transient ShortSortedSet keys;
        protected transient ReferenceCollection<V> values;

        public Submap(short from, boolean bottom, short to, boolean top) {
            if (!bottom && !top && Short2ReferenceRBTreeMap.this.compare(from, to) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Short2ReferenceRBTreeMap.this.defRetValue;
        }

        @Override
        public void clear() {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                i.nextEntry();
                i.remove();
            }
        }

        final boolean in(short k) {
            return !(!this.bottom && Short2ReferenceRBTreeMap.this.compare(k, this.from) < 0 || !this.top && Short2ReferenceRBTreeMap.this.compare(k, this.to) >= 0);
        }

        @Override
        public ObjectSortedSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Short2ReferenceMap.Entry<V>>(){

                    @Override
                    public ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override
                    public ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> iterator(Short2ReferenceMap.Entry<V> from) {
                        return new SubmapEntryIterator(from.getShortKey());
                    }

                    @Override
                    public Comparator<? super Short2ReferenceMap.Entry<V>> comparator() {
                        return Short2ReferenceRBTreeMap.this.short2ReferenceEntrySet().comparator();
                    }

                    @Override
                    public boolean contains(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                            return false;
                        }
                        Entry f = Short2ReferenceRBTreeMap.this.findKey((Short)e.getKey());
                        return f != null && Submap.this.in(f.key) && e.equals(f);
                    }

                    @Override
                    public boolean remove(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                            return false;
                        }
                        Entry f = Short2ReferenceRBTreeMap.this.findKey((Short)e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                            Submap.this.remove(f.key);
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
                    public Short2ReferenceMap.Entry<V> first() {
                        return Submap.this.firstEntry();
                    }

                    @Override
                    public Short2ReferenceMap.Entry<V> last() {
                        return Submap.this.lastEntry();
                    }

                    @Override
                    public ObjectSortedSet<Short2ReferenceMap.Entry<V>> subSet(Short2ReferenceMap.Entry<V> from, Short2ReferenceMap.Entry<V> to) {
                        return Submap.this.subMap(from.getShortKey(), to.getShortKey()).short2ReferenceEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Short2ReferenceMap.Entry<V>> headSet(Short2ReferenceMap.Entry<V> to) {
                        return Submap.this.headMap(to.getShortKey()).short2ReferenceEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Short2ReferenceMap.Entry<V>> tailSet(Short2ReferenceMap.Entry<V> from) {
                        return Submap.this.tailMap(from.getShortKey()).short2ReferenceEntrySet();
                    }
                };
            }
            return this.entries;
        }

        @Override
        public ShortSortedSet keySet() {
            if (this.keys == null) {
                this.keys = new KeySet();
            }
            return this.keys;
        }

        @Override
        public ReferenceCollection<V> values() {
            if (this.values == null) {
                this.values = new AbstractReferenceCollection<V>(){

                    @Override
                    public ObjectIterator<V> iterator() {
                        return new SubmapValueIterator();
                    }

                    @Override
                    public boolean contains(Object k) {
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
        public boolean containsKey(short k) {
            return this.in(k) && Short2ReferenceRBTreeMap.this.containsKey(k);
        }

        @Override
        public boolean containsValue(Object v) {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                Object ev = i.nextEntry().value;
                if (ev != v) continue;
                return true;
            }
            return false;
        }

        @Override
        public V get(short k) {
            Entry e;
            short kk = k;
            return this.in(kk) && (e = Short2ReferenceRBTreeMap.this.findKey(kk)) != null ? e.value : this.defRetValue;
        }

        @Override
        public V put(short k, V v) {
            Short2ReferenceRBTreeMap.this.modified = false;
            if (!this.in(k)) {
                throw new IllegalArgumentException("Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            Object oldValue = Short2ReferenceRBTreeMap.this.put(k, v);
            return Short2ReferenceRBTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        public V remove(short k) {
            Short2ReferenceRBTreeMap.this.modified = false;
            if (!this.in(k)) {
                return this.defRetValue;
            }
            Object oldValue = Short2ReferenceRBTreeMap.this.remove(k);
            return Short2ReferenceRBTreeMap.this.modified ? oldValue : this.defRetValue;
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
        public ShortComparator comparator() {
            return Short2ReferenceRBTreeMap.this.actualComparator;
        }

        @Override
        public Short2ReferenceSortedMap<V> headMap(short to) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to, false);
            }
            return Short2ReferenceRBTreeMap.this.compare(to, this.to) < 0 ? new Submap(this.from, this.bottom, to, false) : this;
        }

        @Override
        public Short2ReferenceSortedMap<V> tailMap(short from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return Short2ReferenceRBTreeMap.this.compare(from, this.from) > 0 ? new Submap(from, false, this.to, this.top) : this;
        }

        @Override
        public Short2ReferenceSortedMap<V> subMap(short from, short to) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to, false);
            }
            if (!this.top) {
                short s = to = Short2ReferenceRBTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }
            if (!this.bottom) {
                short s = from = Short2ReferenceRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to == this.to) {
                return this;
            }
            return new Submap(from, false, to, false);
        }

        public Entry<V> firstEntry() {
            Entry e;
            if (Short2ReferenceRBTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e = Short2ReferenceRBTreeMap.this.firstEntry;
            } else {
                e = Short2ReferenceRBTreeMap.this.locateKey(this.from);
                if (Short2ReferenceRBTreeMap.this.compare(e.key, this.from) < 0) {
                    e = e.next();
                }
            }
            if (e == null || !this.top && Short2ReferenceRBTreeMap.this.compare(e.key, this.to) >= 0) {
                return null;
            }
            return e;
        }

        public Entry<V> lastEntry() {
            Entry e;
            if (Short2ReferenceRBTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e = Short2ReferenceRBTreeMap.this.lastEntry;
            } else {
                e = Short2ReferenceRBTreeMap.this.locateKey(this.to);
                if (Short2ReferenceRBTreeMap.this.compare(e.key, this.to) >= 0) {
                    e = e.prev();
                }
            }
            if (e == null || !this.bottom && Short2ReferenceRBTreeMap.this.compare(e.key, this.from) < 0) {
                return null;
            }
            return e;
        }

        @Override
        public short firstShortKey() {
            Entry e = this.firstEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        @Override
        public short lastShortKey() {
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
            SubmapIterator(short k) {
                this();
                if (this.next == null) return;
                if (!submap.bottom && submap.Short2ReferenceRBTreeMap.this.compare(k, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!submap.top) {
                    this.prev = submap.lastEntry();
                    if (submap.Short2ReferenceRBTreeMap.this.compare(k, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = submap.Short2ReferenceRBTreeMap.this.locateKey(k);
                if (submap.Short2ReferenceRBTreeMap.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Short2ReferenceRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Short2ReferenceRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

        private class KeySet
        extends AbstractShort2ReferenceSortedMap.KeySet {
            private KeySet() {
            }

            @Override
            public ShortBidirectionalIterator iterator() {
                return new SubmapKeyIterator();
            }

            @Override
            public ShortBidirectionalIterator iterator(short from) {
                return new SubmapKeyIterator(from);
            }
        }

        private final class SubmapValueIterator
        extends it.unimi.dsi.fastutil.shorts.Short2ReferenceRBTreeMap$Submap.SubmapIterator
        implements ObjectListIterator<V> {
            private SubmapValueIterator() {
            }

            @Override
            public V next() {
                return this.nextEntry().value;
            }

            @Override
            public V previous() {
                return this.previousEntry().value;
            }
        }

        private final class SubmapKeyIterator
        extends it.unimi.dsi.fastutil.shorts.Short2ReferenceRBTreeMap$Submap.SubmapIterator
        implements ShortListIterator {
            public SubmapKeyIterator() {
            }

            public SubmapKeyIterator(short from) {
                super(from);
            }

            @Override
            public short nextShort() {
                return this.nextEntry().key;
            }

            @Override
            public short previousShort() {
                return this.previousEntry().key;
            }
        }

        private class SubmapEntryIterator
        extends it.unimi.dsi.fastutil.shorts.Short2ReferenceRBTreeMap$Submap.SubmapIterator
        implements ObjectListIterator<Short2ReferenceMap.Entry<V>> {
            SubmapEntryIterator() {
            }

            SubmapEntryIterator(short k) {
                super(k);
            }

            @Override
            public Short2ReferenceMap.Entry<V> next() {
                return this.nextEntry();
            }

            @Override
            public Short2ReferenceMap.Entry<V> previous() {
                return this.previousEntry();
            }
        }
    }

    private class EntryIterator
    extends TreeIterator
    implements ObjectListIterator<Short2ReferenceMap.Entry<V>> {
        EntryIterator() {
        }

        EntryIterator(short k) {
            super(k);
        }

        @Override
        public Short2ReferenceMap.Entry<V> next() {
            return this.nextEntry();
        }

        @Override
        public Short2ReferenceMap.Entry<V> previous() {
            return this.previousEntry();
        }
    }

    private final class KeyIterator
    extends TreeIterator
    implements ShortListIterator {
        public KeyIterator() {
        }

        public KeyIterator(short k) {
            super(k);
        }

        @Override
        public short nextShort() {
            return this.nextEntry().key;
        }

        @Override
        public short previousShort() {
            return this.previousEntry().key;
        }
    }

    private class TreeIterator {
        Entry<V> prev;
        Entry<V> next;
        Entry<V> curr;
        int index = 0;

        TreeIterator() {
            this.next = Short2ReferenceRBTreeMap.this.firstEntry;
        }

        TreeIterator(short k) {
            this.next = Short2ReferenceRBTreeMap.this.locateKey(k);
            if (this.next != null) {
                if (Short2ReferenceRBTreeMap.this.compare(this.next.key, k) <= 0) {
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

        Entry<V> nextEntry() {
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

        Entry<V> previousEntry() {
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
            Short2ReferenceRBTreeMap.this.remove(this.curr.key);
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

