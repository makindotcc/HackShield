/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractChar2DoubleMap;
import it.unimi.dsi.fastutil.chars.AbstractChar2DoubleSortedMap;
import it.unimi.dsi.fastutil.chars.Char2DoubleMap;
import it.unimi.dsi.fastutil.chars.Char2DoubleSortedMap;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharComparators;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
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
import java.util.SortedMap;

public class Char2DoubleAVLTreeMap
extends AbstractChar2DoubleSortedMap
implements Serializable,
Cloneable {
    protected transient Entry tree;
    protected int count;
    protected transient Entry firstEntry;
    protected transient Entry lastEntry;
    protected transient ObjectSortedSet<Char2DoubleMap.Entry> entries;
    protected transient CharSortedSet keys;
    protected transient DoubleCollection values;
    protected transient boolean modified;
    protected Comparator<? super Character> storedComparator;
    protected transient CharComparator actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private transient boolean[] dirPath;

    public Char2DoubleAVLTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = CharComparators.asCharComparator(this.storedComparator);
    }

    public Char2DoubleAVLTreeMap(Comparator<? super Character> c) {
        this();
        this.storedComparator = c;
        this.setActualComparator();
    }

    public Char2DoubleAVLTreeMap(Map<? extends Character, ? extends Double> m) {
        this();
        this.putAll(m);
    }

    public Char2DoubleAVLTreeMap(SortedMap<Character, Double> m) {
        this(m.comparator());
        this.putAll((Map<? extends Character, ? extends Double>)m);
    }

    public Char2DoubleAVLTreeMap(Char2DoubleMap m) {
        this();
        this.putAll(m);
    }

    public Char2DoubleAVLTreeMap(Char2DoubleSortedMap m) {
        this(m.comparator());
        this.putAll(m);
    }

    public Char2DoubleAVLTreeMap(char[] k, double[] v, Comparator<? super Character> c) {
        this(c);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Char2DoubleAVLTreeMap(char[] k, double[] v) {
        this(k, v, null);
    }

    final int compare(char k1, char k2) {
        return this.actualComparator == null ? Character.compare(k1, k2) : this.actualComparator.compare(k1, k2);
    }

    final Entry findKey(char k) {
        int cmp;
        Entry e = this.tree;
        while (e != null && (cmp = this.compare(k, e.key)) != 0) {
            e = cmp < 0 ? e.left() : e.right();
        }
        return e;
    }

    final Entry locateKey(char k) {
        Entry e = this.tree;
        Entry last = this.tree;
        int cmp = 0;
        while (e != null && (cmp = this.compare(k, e.key)) != 0) {
            last = e;
            e = cmp < 0 ? e.left() : e.right();
        }
        return cmp == 0 ? e : last;
    }

    private void allocatePaths() {
        this.dirPath = new boolean[48];
    }

    public double addTo(char k, double incr) {
        Entry e = this.add(k);
        double oldValue = e.value;
        e.value += incr;
        return oldValue;
    }

    @Override
    public double put(char k, double v) {
        Entry e = this.add(k);
        double oldValue = e.value;
        e.value = v;
        return oldValue;
    }

    private Entry add(char k) {
        this.modified = false;
        Entry e = null;
        if (this.tree == null) {
            ++this.count;
            this.lastEntry = this.firstEntry = new Entry(k, this.defRetValue);
            this.tree = this.firstEntry;
            e = this.firstEntry;
            this.modified = true;
        } else {
            Entry p = this.tree;
            Entry q = null;
            Entry y = this.tree;
            Entry z = null;
            Entry w = null;
            int i = 0;
            while (true) {
                int cmp;
                if ((cmp = this.compare(k, p.key)) == 0) {
                    return p;
                }
                if (p.balance() != 0) {
                    i = 0;
                    z = q;
                    y = p;
                }
                if (this.dirPath[i++] = cmp > 0) {
                    if (p.succ()) {
                        ++this.count;
                        e = new Entry(k, this.defRetValue);
                        this.modified = true;
                        if (p.right == null) {
                            this.lastEntry = e;
                        }
                        e.left = p;
                        e.right = p.right;
                        p.right(e);
                        break;
                    }
                    q = p;
                    p = p.right;
                    continue;
                }
                if (p.pred()) {
                    ++this.count;
                    e = new Entry(k, this.defRetValue);
                    this.modified = true;
                    if (p.left == null) {
                        this.firstEntry = e;
                    }
                    e.right = p;
                    e.left = p.left;
                    p.left(e);
                    break;
                }
                q = p;
                p = p.left;
            }
            p = y;
            i = 0;
            while (p != e) {
                if (this.dirPath[i]) {
                    p.incBalance();
                } else {
                    p.decBalance();
                }
                p = this.dirPath[i++] ? p.right : p.left;
            }
            if (y.balance() == -2) {
                Entry x = y.left;
                if (x.balance() == -1) {
                    w = x;
                    if (x.succ()) {
                        x.succ(false);
                        y.pred(x);
                    } else {
                        y.left = x.right;
                    }
                    x.right = y;
                    x.balance(0);
                    y.balance(0);
                } else {
                    assert (x.balance() == 1);
                    w = x.right;
                    x.right = w.left;
                    w.left = x;
                    y.left = w.right;
                    w.right = y;
                    if (w.balance() == -1) {
                        x.balance(0);
                        y.balance(1);
                    } else if (w.balance() == 0) {
                        x.balance(0);
                        y.balance(0);
                    } else {
                        x.balance(-1);
                        y.balance(0);
                    }
                    w.balance(0);
                    if (w.pred()) {
                        x.succ(w);
                        w.pred(false);
                    }
                    if (w.succ()) {
                        y.pred(w);
                        w.succ(false);
                    }
                }
            } else if (y.balance() == 2) {
                Entry x = y.right;
                if (x.balance() == 1) {
                    w = x;
                    if (x.pred()) {
                        x.pred(false);
                        y.succ(x);
                    } else {
                        y.right = x.left;
                    }
                    x.left = y;
                    x.balance(0);
                    y.balance(0);
                } else {
                    assert (x.balance() == -1);
                    w = x.left;
                    x.left = w.right;
                    w.right = x;
                    y.right = w.left;
                    w.left = y;
                    if (w.balance() == 1) {
                        x.balance(0);
                        y.balance(-1);
                    } else if (w.balance() == 0) {
                        x.balance(0);
                        y.balance(0);
                    } else {
                        x.balance(1);
                        y.balance(0);
                    }
                    w.balance(0);
                    if (w.pred()) {
                        y.succ(w);
                        w.pred(false);
                    }
                    if (w.succ()) {
                        x.pred(w);
                        w.succ(false);
                    }
                }
            } else {
                return e;
            }
            if (z == null) {
                this.tree = w;
            } else if (z.left == y) {
                z.left = w;
            } else {
                z.right = w;
            }
        }
        return e;
    }

    private Entry parent(Entry e) {
        Entry y;
        if (e == this.tree) {
            return null;
        }
        Entry x = y = e;
        while (true) {
            if (y.succ()) {
                Entry p = y.right;
                if (p == null || p.left != e) {
                    while (!x.pred()) {
                        x = x.left;
                    }
                    p = x.left;
                }
                return p;
            }
            if (x.pred()) {
                Entry p = x.left;
                if (p == null || p.right != e) {
                    while (!y.succ()) {
                        y = y.right;
                    }
                    p = y.right;
                }
                return p;
            }
            x = x.left;
            y = y.right;
        }
    }

    @Override
    public double remove(char k) {
        int cmp;
        this.modified = false;
        if (this.tree == null) {
            return this.defRetValue;
        }
        Entry p = this.tree;
        Entry q = null;
        boolean dir = false;
        char kk = k;
        while ((cmp = this.compare(kk, p.key)) != 0) {
            dir = cmp > 0;
            if (dir) {
                q = p;
                if ((p = p.right()) != null) continue;
                return this.defRetValue;
            }
            q = p;
            if ((p = p.left()) != null) continue;
            return this.defRetValue;
        }
        if (p.left == null) {
            this.firstEntry = p.next();
        }
        if (p.right == null) {
            this.lastEntry = p.prev();
        }
        if (p.succ()) {
            if (p.pred()) {
                if (q != null) {
                    if (dir) {
                        q.succ(p.right);
                    } else {
                        q.pred(p.left);
                    }
                } else {
                    this.tree = dir ? p.right : p.left;
                }
            } else {
                p.prev().right = p.right;
                if (q != null) {
                    if (dir) {
                        q.right = p.left;
                    } else {
                        q.left = p.left;
                    }
                } else {
                    this.tree = p.left;
                }
            }
        } else {
            Entry r = p.right;
            if (r.pred()) {
                r.left = p.left;
                r.pred(p.pred());
                if (!r.pred()) {
                    r.prev().right = r;
                }
                if (q != null) {
                    if (dir) {
                        q.right = r;
                    } else {
                        q.left = r;
                    }
                } else {
                    this.tree = r;
                }
                r.balance(p.balance());
                q = r;
                dir = true;
            } else {
                Entry s;
                while (!(s = r.left).pred()) {
                    r = s;
                }
                if (s.succ()) {
                    r.pred(s);
                } else {
                    r.left = s.right;
                }
                s.left = p.left;
                if (!p.pred()) {
                    p.prev().right = s;
                    s.pred(false);
                }
                s.right = p.right;
                s.succ(false);
                if (q != null) {
                    if (dir) {
                        q.right = s;
                    } else {
                        q.left = s;
                    }
                } else {
                    this.tree = s;
                }
                s.balance(p.balance());
                q = r;
                dir = false;
            }
        }
        while (q != null) {
            Entry w;
            Entry x;
            Entry y = q;
            q = this.parent(y);
            if (!dir) {
                dir = q != null && q.left != y;
                y.incBalance();
                if (y.balance() == 1) break;
                if (y.balance() != 2) continue;
                x = y.right;
                assert (x != null);
                if (x.balance() == -1) {
                    assert (x.balance() == -1);
                    w = x.left;
                    x.left = w.right;
                    w.right = x;
                    y.right = w.left;
                    w.left = y;
                    if (w.balance() == 1) {
                        x.balance(0);
                        y.balance(-1);
                    } else if (w.balance() == 0) {
                        x.balance(0);
                        y.balance(0);
                    } else {
                        assert (w.balance() == -1);
                        x.balance(1);
                        y.balance(0);
                    }
                    w.balance(0);
                    if (w.pred()) {
                        y.succ(w);
                        w.pred(false);
                    }
                    if (w.succ()) {
                        x.pred(w);
                        w.succ(false);
                    }
                    if (q != null) {
                        if (dir) {
                            q.right = w;
                            continue;
                        }
                        q.left = w;
                        continue;
                    }
                    this.tree = w;
                    continue;
                }
                if (q != null) {
                    if (dir) {
                        q.right = x;
                    } else {
                        q.left = x;
                    }
                } else {
                    this.tree = x;
                }
                if (x.balance() == 0) {
                    y.right = x.left;
                    x.left = y;
                    x.balance(-1);
                    y.balance(1);
                    break;
                }
                assert (x.balance() == 1);
                if (x.pred()) {
                    y.succ(true);
                    x.pred(false);
                } else {
                    y.right = x.left;
                }
                x.left = y;
                y.balance(0);
                x.balance(0);
                continue;
            }
            dir = q != null && q.left != y;
            y.decBalance();
            if (y.balance() == -1) break;
            if (y.balance() != -2) continue;
            x = y.left;
            assert (x != null);
            if (x.balance() == 1) {
                assert (x.balance() == 1);
                w = x.right;
                x.right = w.left;
                w.left = x;
                y.left = w.right;
                w.right = y;
                if (w.balance() == -1) {
                    x.balance(0);
                    y.balance(1);
                } else if (w.balance() == 0) {
                    x.balance(0);
                    y.balance(0);
                } else {
                    assert (w.balance() == 1);
                    x.balance(-1);
                    y.balance(0);
                }
                w.balance(0);
                if (w.pred()) {
                    x.succ(w);
                    w.pred(false);
                }
                if (w.succ()) {
                    y.pred(w);
                    w.succ(false);
                }
                if (q != null) {
                    if (dir) {
                        q.right = w;
                        continue;
                    }
                    q.left = w;
                    continue;
                }
                this.tree = w;
                continue;
            }
            if (q != null) {
                if (dir) {
                    q.right = x;
                } else {
                    q.left = x;
                }
            } else {
                this.tree = x;
            }
            if (x.balance() == 0) {
                y.left = x.right;
                x.right = y;
                x.balance(1);
                y.balance(-1);
                break;
            }
            assert (x.balance() == -1);
            if (x.succ()) {
                y.pred(true);
                x.succ(false);
            } else {
                y.left = x.right;
            }
            x.right = y;
            y.balance(0);
            x.balance(0);
        }
        this.modified = true;
        --this.count;
        return p.value;
    }

    @Override
    public boolean containsValue(double v) {
        ValueIterator i = new ValueIterator();
        int j = this.count;
        while (j-- != 0) {
            double ev = i.nextDouble();
            if (Double.doubleToLongBits(ev) != Double.doubleToLongBits(v)) continue;
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
    public boolean containsKey(char k) {
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
    public double get(char k) {
        Entry e = this.findKey(k);
        return e == null ? this.defRetValue : e.value;
    }

    @Override
    public char firstCharKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.firstEntry.key;
    }

    @Override
    public char lastCharKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.lastEntry.key;
    }

    @Override
    public ObjectSortedSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Char2DoubleMap.Entry>(){
                final Comparator<? super Char2DoubleMap.Entry> comparator;
                {
                    this.comparator = Char2DoubleAVLTreeMap.this.actualComparator == null ? (x, y) -> Character.compare(x.getCharKey(), y.getCharKey()) : (x, y) -> Char2DoubleAVLTreeMap.this.actualComparator.compare(x.getCharKey(), y.getCharKey());
                }

                @Override
                public Comparator<? super Char2DoubleMap.Entry> comparator() {
                    return this.comparator;
                }

                @Override
                public ObjectBidirectionalIterator<Char2DoubleMap.Entry> iterator() {
                    return new EntryIterator();
                }

                @Override
                public ObjectBidirectionalIterator<Char2DoubleMap.Entry> iterator(Char2DoubleMap.Entry from) {
                    return new EntryIterator(from.getCharKey());
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
                    if (!(e.getKey() instanceof Character)) {
                        return false;
                    }
                    if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                        return false;
                    }
                    Entry f = Char2DoubleAVLTreeMap.this.findKey(((Character)e.getKey()).charValue());
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
                    if (e.getKey() == null || !(e.getKey() instanceof Character)) {
                        return false;
                    }
                    if (!(e.getValue() instanceof Double)) {
                        return false;
                    }
                    Entry f = Char2DoubleAVLTreeMap.this.findKey(((Character)e.getKey()).charValue());
                    if (f == null || Double.doubleToLongBits(f.getDoubleValue()) != Double.doubleToLongBits((Double)e.getValue())) {
                        return false;
                    }
                    Char2DoubleAVLTreeMap.this.remove(f.key);
                    return true;
                }

                @Override
                public int size() {
                    return Char2DoubleAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Char2DoubleAVLTreeMap.this.clear();
                }

                @Override
                public Char2DoubleMap.Entry first() {
                    return Char2DoubleAVLTreeMap.this.firstEntry;
                }

                @Override
                public Char2DoubleMap.Entry last() {
                    return Char2DoubleAVLTreeMap.this.lastEntry;
                }

                @Override
                public ObjectSortedSet<Char2DoubleMap.Entry> subSet(Char2DoubleMap.Entry from, Char2DoubleMap.Entry to) {
                    return Char2DoubleAVLTreeMap.this.subMap(from.getCharKey(), to.getCharKey()).char2DoubleEntrySet();
                }

                @Override
                public ObjectSortedSet<Char2DoubleMap.Entry> headSet(Char2DoubleMap.Entry to) {
                    return Char2DoubleAVLTreeMap.this.headMap(to.getCharKey()).char2DoubleEntrySet();
                }

                @Override
                public ObjectSortedSet<Char2DoubleMap.Entry> tailSet(Char2DoubleMap.Entry from) {
                    return Char2DoubleAVLTreeMap.this.tailMap(from.getCharKey()).char2DoubleEntrySet();
                }
            };
        }
        return this.entries;
    }

    @Override
    public CharSortedSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public DoubleCollection values() {
        if (this.values == null) {
            this.values = new AbstractDoubleCollection(){

                @Override
                public DoubleIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public boolean contains(double k) {
                    return Char2DoubleAVLTreeMap.this.containsValue(k);
                }

                @Override
                public int size() {
                    return Char2DoubleAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Char2DoubleAVLTreeMap.this.clear();
                }
            };
        }
        return this.values;
    }

    @Override
    public CharComparator comparator() {
        return this.actualComparator;
    }

    @Override
    public Char2DoubleSortedMap headMap(char to) {
        return new Submap('\u0000', true, to, false);
    }

    @Override
    public Char2DoubleSortedMap tailMap(char from) {
        return new Submap(from, false, '\u0000', true);
    }

    @Override
    public Char2DoubleSortedMap subMap(char from, char to) {
        return new Submap(from, false, to, false);
    }

    public Char2DoubleAVLTreeMap clone() {
        Char2DoubleAVLTreeMap c;
        try {
            c = (Char2DoubleAVLTreeMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.allocatePaths();
        if (this.count != 0) {
            Entry rp = new Entry();
            Entry rq = new Entry();
            Entry p = rp;
            rp.left(this.tree);
            Entry q = rq;
            rq.pred(null);
            while (true) {
                Entry e;
                if (!p.pred()) {
                    e = p.left.clone();
                    e.pred(q.left);
                    e.succ(q);
                    q.left(e);
                    p = p.left;
                    q = q.left;
                } else {
                    while (p.succ()) {
                        p = p.right;
                        if (p == null) {
                            q.right = null;
                            c.firstEntry = c.tree = rq.left;
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
                e.succ(q.right);
                e.pred(q);
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
            s.writeChar(e.key);
            s.writeDouble(e.value);
        }
    }

    private Entry readTree(ObjectInputStream s, int n, Entry pred, Entry succ) throws IOException, ClassNotFoundException {
        if (n == 1) {
            Entry top = new Entry(s.readChar(), s.readDouble());
            top.pred(pred);
            top.succ(succ);
            return top;
        }
        if (n == 2) {
            Entry top = new Entry(s.readChar(), s.readDouble());
            top.right(new Entry(s.readChar(), s.readDouble()));
            top.right.pred(top);
            top.balance(1);
            top.pred(pred);
            top.right.succ(succ);
            return top;
        }
        int rightN = n / 2;
        int leftN = n - rightN - 1;
        Entry top = new Entry();
        top.left(this.readTree(s, leftN, pred, top));
        top.key = s.readChar();
        top.value = s.readDouble();
        top.right(this.readTree(s, rightN, top, succ));
        if (n == (n & -n)) {
            top.balance(1);
        }
        return top;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.setActualComparator();
        this.allocatePaths();
        if (this.count != 0) {
            Entry e = this.tree = this.readTree(s, this.count, null, null);
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

    private static final class Entry
    extends AbstractChar2DoubleMap.BasicEntry
    implements Cloneable {
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 0x40000000;
        private static final int BALANCE_MASK = 255;
        Entry left;
        Entry right;
        int info;

        Entry() {
            super('\u0000', 0.0);
        }

        Entry(char k, double v) {
            super(k, v);
            this.info = -1073741824;
        }

        Entry left() {
            return (this.info & 0x40000000) != 0 ? null : this.left;
        }

        Entry right() {
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

        void pred(Entry pred) {
            this.info |= 0x40000000;
            this.left = pred;
        }

        void succ(Entry succ) {
            this.info |= Integer.MIN_VALUE;
            this.right = succ;
        }

        void left(Entry left) {
            this.info &= 0xBFFFFFFF;
            this.left = left;
        }

        void right(Entry right) {
            this.info &= Integer.MAX_VALUE;
            this.right = right;
        }

        int balance() {
            return (byte)this.info;
        }

        void balance(int level) {
            this.info &= 0xFFFFFF00;
            this.info |= level & 0xFF;
        }

        void incBalance() {
            this.info = this.info & 0xFFFFFF00 | (byte)this.info + 1 & 0xFF;
        }

        protected void decBalance() {
            this.info = this.info & 0xFFFFFF00 | (byte)this.info - 1 & 0xFF;
        }

        Entry next() {
            Entry next = this.right;
            if ((this.info & Integer.MIN_VALUE) == 0) {
                while ((next.info & 0x40000000) == 0) {
                    next = next.left;
                }
            }
            return next;
        }

        Entry prev() {
            Entry prev = this.left;
            if ((this.info & 0x40000000) == 0) {
                while ((prev.info & Integer.MIN_VALUE) == 0) {
                    prev = prev.right;
                }
            }
            return prev;
        }

        @Override
        public double setValue(double value) {
            double oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public Entry clone() {
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
            return this.key == ((Character)e.getKey()).charValue() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.double2int(this.value);
        }

        @Override
        public String toString() {
            return this.key + "=>" + this.value;
        }
    }

    private final class ValueIterator
    extends TreeIterator
    implements DoubleListIterator {
        private ValueIterator() {
        }

        @Override
        public double nextDouble() {
            return this.nextEntry().value;
        }

        @Override
        public double previousDouble() {
            return this.previousEntry().value;
        }
    }

    private class KeySet
    extends AbstractChar2DoubleSortedMap.KeySet {
        private KeySet() {
        }

        @Override
        public CharBidirectionalIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public CharBidirectionalIterator iterator(char from) {
            return new KeyIterator(from);
        }
    }

    private final class Submap
    extends AbstractChar2DoubleSortedMap
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        char from;
        char to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Char2DoubleMap.Entry> entries;
        protected transient CharSortedSet keys;
        protected transient DoubleCollection values;

        public Submap(char from, boolean bottom, char to, boolean top) {
            if (!bottom && !top && Char2DoubleAVLTreeMap.this.compare(from, to) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Char2DoubleAVLTreeMap.this.defRetValue;
        }

        @Override
        public void clear() {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                i.nextEntry();
                i.remove();
            }
        }

        final boolean in(char k) {
            return !(!this.bottom && Char2DoubleAVLTreeMap.this.compare(k, this.from) < 0 || !this.top && Char2DoubleAVLTreeMap.this.compare(k, this.to) >= 0);
        }

        @Override
        public ObjectSortedSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Char2DoubleMap.Entry>(){

                    @Override
                    public ObjectBidirectionalIterator<Char2DoubleMap.Entry> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override
                    public ObjectBidirectionalIterator<Char2DoubleMap.Entry> iterator(Char2DoubleMap.Entry from) {
                        return new SubmapEntryIterator(from.getCharKey());
                    }

                    @Override
                    public Comparator<? super Char2DoubleMap.Entry> comparator() {
                        return Char2DoubleAVLTreeMap.this.char2DoubleEntrySet().comparator();
                    }

                    @Override
                    public boolean contains(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getKey() == null || !(e.getKey() instanceof Character)) {
                            return false;
                        }
                        if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                            return false;
                        }
                        Entry f = Char2DoubleAVLTreeMap.this.findKey(((Character)e.getKey()).charValue());
                        return f != null && Submap.this.in(f.key) && e.equals(f);
                    }

                    @Override
                    public boolean remove(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getKey() == null || !(e.getKey() instanceof Character)) {
                            return false;
                        }
                        if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                            return false;
                        }
                        Entry f = Char2DoubleAVLTreeMap.this.findKey(((Character)e.getKey()).charValue());
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
                    public Char2DoubleMap.Entry first() {
                        return Submap.this.firstEntry();
                    }

                    @Override
                    public Char2DoubleMap.Entry last() {
                        return Submap.this.lastEntry();
                    }

                    @Override
                    public ObjectSortedSet<Char2DoubleMap.Entry> subSet(Char2DoubleMap.Entry from, Char2DoubleMap.Entry to) {
                        return Submap.this.subMap(from.getCharKey(), to.getCharKey()).char2DoubleEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Char2DoubleMap.Entry> headSet(Char2DoubleMap.Entry to) {
                        return Submap.this.headMap(to.getCharKey()).char2DoubleEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Char2DoubleMap.Entry> tailSet(Char2DoubleMap.Entry from) {
                        return Submap.this.tailMap(from.getCharKey()).char2DoubleEntrySet();
                    }
                };
            }
            return this.entries;
        }

        @Override
        public CharSortedSet keySet() {
            if (this.keys == null) {
                this.keys = new KeySet();
            }
            return this.keys;
        }

        @Override
        public DoubleCollection values() {
            if (this.values == null) {
                this.values = new AbstractDoubleCollection(){

                    @Override
                    public DoubleIterator iterator() {
                        return new SubmapValueIterator();
                    }

                    @Override
                    public boolean contains(double k) {
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
        public boolean containsKey(char k) {
            return this.in(k) && Char2DoubleAVLTreeMap.this.containsKey(k);
        }

        @Override
        public boolean containsValue(double v) {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                double ev = i.nextEntry().value;
                if (Double.doubleToLongBits(ev) != Double.doubleToLongBits(v)) continue;
                return true;
            }
            return false;
        }

        @Override
        public double get(char k) {
            Entry e;
            char kk = k;
            return this.in(kk) && (e = Char2DoubleAVLTreeMap.this.findKey(kk)) != null ? e.value : this.defRetValue;
        }

        @Override
        public double put(char k, double v) {
            Char2DoubleAVLTreeMap.this.modified = false;
            if (!this.in(k)) {
                throw new IllegalArgumentException("Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            double oldValue = Char2DoubleAVLTreeMap.this.put(k, v);
            return Char2DoubleAVLTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        public double remove(char k) {
            Char2DoubleAVLTreeMap.this.modified = false;
            if (!this.in(k)) {
                return this.defRetValue;
            }
            double oldValue = Char2DoubleAVLTreeMap.this.remove(k);
            return Char2DoubleAVLTreeMap.this.modified ? oldValue : this.defRetValue;
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
        public CharComparator comparator() {
            return Char2DoubleAVLTreeMap.this.actualComparator;
        }

        @Override
        public Char2DoubleSortedMap headMap(char to) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to, false);
            }
            return Char2DoubleAVLTreeMap.this.compare(to, this.to) < 0 ? new Submap(this.from, this.bottom, to, false) : this;
        }

        @Override
        public Char2DoubleSortedMap tailMap(char from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return Char2DoubleAVLTreeMap.this.compare(from, this.from) > 0 ? new Submap(from, false, this.to, this.top) : this;
        }

        @Override
        public Char2DoubleSortedMap subMap(char from, char to) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to, false);
            }
            if (!this.top) {
                char c = to = Char2DoubleAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }
            if (!this.bottom) {
                char c = from = Char2DoubleAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to == this.to) {
                return this;
            }
            return new Submap(from, false, to, false);
        }

        public Entry firstEntry() {
            Entry e;
            if (Char2DoubleAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e = Char2DoubleAVLTreeMap.this.firstEntry;
            } else {
                e = Char2DoubleAVLTreeMap.this.locateKey(this.from);
                if (Char2DoubleAVLTreeMap.this.compare(e.key, this.from) < 0) {
                    e = e.next();
                }
            }
            if (e == null || !this.top && Char2DoubleAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                return null;
            }
            return e;
        }

        public Entry lastEntry() {
            Entry e;
            if (Char2DoubleAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e = Char2DoubleAVLTreeMap.this.lastEntry;
            } else {
                e = Char2DoubleAVLTreeMap.this.locateKey(this.to);
                if (Char2DoubleAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                    e = e.prev();
                }
            }
            if (e == null || !this.bottom && Char2DoubleAVLTreeMap.this.compare(e.key, this.from) < 0) {
                return null;
            }
            return e;
        }

        @Override
        public char firstCharKey() {
            Entry e = this.firstEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        @Override
        public char lastCharKey() {
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
            SubmapIterator(char k) {
                this();
                if (this.next == null) return;
                if (!submap.bottom && submap.Char2DoubleAVLTreeMap.this.compare(k, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!submap.top) {
                    this.prev = submap.lastEntry();
                    if (submap.Char2DoubleAVLTreeMap.this.compare(k, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = submap.Char2DoubleAVLTreeMap.this.locateKey(k);
                if (submap.Char2DoubleAVLTreeMap.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Char2DoubleAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Char2DoubleAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

        private class KeySet
        extends AbstractChar2DoubleSortedMap.KeySet {
            private KeySet() {
            }

            @Override
            public CharBidirectionalIterator iterator() {
                return new SubmapKeyIterator();
            }

            @Override
            public CharBidirectionalIterator iterator(char from) {
                return new SubmapKeyIterator(from);
            }
        }

        private final class SubmapValueIterator
        extends SubmapIterator
        implements DoubleListIterator {
            private SubmapValueIterator() {
            }

            @Override
            public double nextDouble() {
                return this.nextEntry().value;
            }

            @Override
            public double previousDouble() {
                return this.previousEntry().value;
            }
        }

        private final class SubmapKeyIterator
        extends SubmapIterator
        implements CharListIterator {
            public SubmapKeyIterator() {
            }

            public SubmapKeyIterator(char from) {
                super(from);
            }

            @Override
            public char nextChar() {
                return this.nextEntry().key;
            }

            @Override
            public char previousChar() {
                return this.previousEntry().key;
            }
        }

        private class SubmapEntryIterator
        extends SubmapIterator
        implements ObjectListIterator<Char2DoubleMap.Entry> {
            SubmapEntryIterator() {
            }

            SubmapEntryIterator(char k) {
                super(k);
            }

            @Override
            public Char2DoubleMap.Entry next() {
                return this.nextEntry();
            }

            @Override
            public Char2DoubleMap.Entry previous() {
                return this.previousEntry();
            }
        }
    }

    private class EntryIterator
    extends TreeIterator
    implements ObjectListIterator<Char2DoubleMap.Entry> {
        EntryIterator() {
        }

        EntryIterator(char k) {
            super(k);
        }

        @Override
        public Char2DoubleMap.Entry next() {
            return this.nextEntry();
        }

        @Override
        public Char2DoubleMap.Entry previous() {
            return this.previousEntry();
        }

        @Override
        public void set(Char2DoubleMap.Entry ok) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Char2DoubleMap.Entry ok) {
            throw new UnsupportedOperationException();
        }
    }

    private final class KeyIterator
    extends TreeIterator
    implements CharListIterator {
        public KeyIterator() {
        }

        public KeyIterator(char k) {
            super(k);
        }

        @Override
        public char nextChar() {
            return this.nextEntry().key;
        }

        @Override
        public char previousChar() {
            return this.previousEntry().key;
        }
    }

    private class TreeIterator {
        Entry prev;
        Entry next;
        Entry curr;
        int index = 0;

        TreeIterator() {
            this.next = Char2DoubleAVLTreeMap.this.firstEntry;
        }

        TreeIterator(char k) {
            this.next = Char2DoubleAVLTreeMap.this.locateKey(k);
            if (this.next != null) {
                if (Char2DoubleAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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

        Entry nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.prev = this.next;
            ++this.index;
            this.updateNext();
            return this.curr;
        }

        void updatePrevious() {
            this.prev = this.prev.prev();
        }

        Entry previousEntry() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = this.next = this.prev;
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
            this.next = this.prev = this.curr;
            this.updatePrevious();
            this.updateNext();
            Char2DoubleAVLTreeMap.this.remove(this.curr.key);
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

