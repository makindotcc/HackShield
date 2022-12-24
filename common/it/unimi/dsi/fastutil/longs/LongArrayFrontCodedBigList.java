/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.longs.LongArrayFrontCodedList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import it.unimi.dsi.fastutil.objects.AbstractObjectBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterator;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class LongArrayFrontCodedBigList
extends AbstractObjectBigList<long[]>
implements Serializable,
Cloneable,
RandomAccess {
    private static final long serialVersionUID = 1L;
    protected final long n;
    protected final int ratio;
    protected final long[][] array;
    protected transient long[][] p;

    public LongArrayFrontCodedBigList(Iterator<long[]> arrays, int ratio) {
        if (ratio < 1) {
            throw new IllegalArgumentException("Illegal ratio (" + ratio + ")");
        }
        long[][] array = LongBigArrays.EMPTY_BIG_ARRAY;
        long[][] p = LongBigArrays.EMPTY_BIG_ARRAY;
        long[][] a = new long[2][];
        long curSize = 0L;
        long n = 0L;
        int b = 0;
        while (arrays.hasNext()) {
            a[b] = arrays.next();
            int length = a[b].length;
            if (n % (long)ratio == 0L) {
                p = BigArrays.grow(p, n / (long)ratio + 1L);
                BigArrays.set(p, n / (long)ratio, curSize);
                array = BigArrays.grow(array, curSize + (long)LongArrayFrontCodedList.count(length) + (long)length, curSize);
                curSize += (long)LongArrayFrontCodedList.writeInt(array, length, curSize);
                BigArrays.copyToBig(a[b], 0, array, curSize, (long)length);
                curSize += (long)length;
            } else {
                int common;
                int minLength = Math.min(a[1 - b].length, length);
                for (common = 0; common < minLength && a[0][common] == a[1][common]; ++common) {
                }
                array = BigArrays.grow(array, curSize + (long)LongArrayFrontCodedList.count(length -= common) + (long)LongArrayFrontCodedList.count(common) + (long)length, curSize);
                curSize += (long)LongArrayFrontCodedList.writeInt(array, length, curSize);
                curSize += (long)LongArrayFrontCodedList.writeInt(array, common, curSize);
                BigArrays.copyToBig(a[b], common, array, curSize, (long)length);
                curSize += (long)length;
            }
            b = 1 - b;
            ++n;
        }
        this.n = n;
        this.ratio = ratio;
        this.array = BigArrays.trim(array, curSize);
        this.p = BigArrays.trim(p, (n + (long)ratio - 1L) / (long)ratio);
    }

    public LongArrayFrontCodedBigList(Collection<long[]> c, int ratio) {
        this(c.iterator(), ratio);
    }

    public int ratio() {
        return this.ratio;
    }

    private int length(long index) {
        long[][] array = this.array;
        int delta = (int)(index % (long)this.ratio);
        long pos = BigArrays.get(this.p, index / (long)this.ratio);
        int length = LongArrayFrontCodedList.readInt(array, pos);
        if (delta == 0) {
            return length;
        }
        pos += (long)(LongArrayFrontCodedList.count(length) + length);
        length = LongArrayFrontCodedList.readInt(array, pos);
        int common = LongArrayFrontCodedList.readInt(array, pos + (long)LongArrayFrontCodedList.count(length));
        for (int i = 0; i < delta - 1; ++i) {
            length = LongArrayFrontCodedList.readInt(array, pos += (long)(LongArrayFrontCodedList.count(length) + LongArrayFrontCodedList.count(common) + length));
            common = LongArrayFrontCodedList.readInt(array, pos + (long)LongArrayFrontCodedList.count(length));
        }
        return length + common;
    }

    public int arrayLength(long index) {
        this.ensureRestrictedIndex(index);
        return this.length(index);
    }

    private int extract(long index, long[] a, int offset, int length) {
        long startPos;
        int delta = (int)(index % (long)this.ratio);
        long pos = startPos = BigArrays.get(this.p, index / (long)this.ratio);
        int arrayLength = LongArrayFrontCodedList.readInt(this.array, pos);
        int currLen = 0;
        if (delta == 0) {
            pos = BigArrays.get(this.p, index / (long)this.ratio) + (long)LongArrayFrontCodedList.count(arrayLength);
            BigArrays.copyFromBig(this.array, pos, a, offset, Math.min(length, arrayLength));
            return arrayLength;
        }
        int common = 0;
        for (int i = 0; i < delta; ++i) {
            long prevArrayPos = pos + (long)LongArrayFrontCodedList.count(arrayLength) + (long)(i != 0 ? LongArrayFrontCodedList.count(common) : 0);
            common = LongArrayFrontCodedList.readInt(this.array, (pos = prevArrayPos + (long)arrayLength) + (long)LongArrayFrontCodedList.count(arrayLength = LongArrayFrontCodedList.readInt(this.array, pos)));
            int actualCommon = Math.min(common, length);
            if (actualCommon <= currLen) {
                currLen = actualCommon;
                continue;
            }
            BigArrays.copyFromBig(this.array, prevArrayPos, a, currLen + offset, actualCommon - currLen);
            currLen = actualCommon;
        }
        if (currLen < length) {
            BigArrays.copyFromBig(this.array, pos + (long)LongArrayFrontCodedList.count(arrayLength) + (long)LongArrayFrontCodedList.count(common), a, currLen + offset, Math.min(arrayLength, length - currLen));
        }
        return arrayLength + common;
    }

    @Override
    public long[] get(long index) {
        return this.getArray(index);
    }

    public long[] getArray(long index) {
        this.ensureRestrictedIndex(index);
        int length = this.length(index);
        long[] a = new long[length];
        this.extract(index, a, 0, length);
        return a;
    }

    public int get(long index, long[] a, int offset, int length) {
        this.ensureRestrictedIndex(index);
        LongArrays.ensureOffsetLength(a, offset, length);
        int arrayLength = this.extract(index, a, offset, length);
        if (length >= arrayLength) {
            return arrayLength;
        }
        return length - arrayLength;
    }

    public int get(long index, long[] a) {
        return this.get(index, a, 0, a.length);
    }

    @Override
    public long size64() {
        return this.n;
    }

    @Override
    public ObjectBigListIterator<long[]> listIterator(final long start) {
        this.ensureIndex(start);
        return new ObjectBigListIterator<long[]>(){
            long[] s = LongArrays.EMPTY_ARRAY;
            long i = 0L;
            long pos = 0L;
            boolean inSync;
            {
                if (start != 0L) {
                    if (start == LongArrayFrontCodedBigList.this.n) {
                        this.i = start;
                    } else {
                        this.pos = BigArrays.get(LongArrayFrontCodedBigList.this.p, start / (long)LongArrayFrontCodedBigList.this.ratio);
                        int j = (int)(start % (long)LongArrayFrontCodedBigList.this.ratio);
                        this.i = start - (long)j;
                        while (j-- != 0) {
                            this.next();
                        }
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return this.i < LongArrayFrontCodedBigList.this.n;
            }

            @Override
            public boolean hasPrevious() {
                return this.i > 0L;
            }

            @Override
            public long previousIndex() {
                return this.i - 1L;
            }

            @Override
            public long nextIndex() {
                return this.i;
            }

            @Override
            public long[] next() {
                int length;
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                if (this.i % (long)LongArrayFrontCodedBigList.this.ratio == 0L) {
                    this.pos = BigArrays.get(LongArrayFrontCodedBigList.this.p, this.i / (long)LongArrayFrontCodedBigList.this.ratio);
                    length = LongArrayFrontCodedList.readInt(LongArrayFrontCodedBigList.this.array, this.pos);
                    this.s = LongArrays.ensureCapacity(this.s, length, 0);
                    BigArrays.copyFromBig(LongArrayFrontCodedBigList.this.array, this.pos + (long)LongArrayFrontCodedList.count(length), this.s, 0, length);
                    this.pos += (long)(length + LongArrayFrontCodedList.count(length));
                    this.inSync = true;
                } else if (this.inSync) {
                    length = LongArrayFrontCodedList.readInt(LongArrayFrontCodedBigList.this.array, this.pos);
                    int common = LongArrayFrontCodedList.readInt(LongArrayFrontCodedBigList.this.array, this.pos + (long)LongArrayFrontCodedList.count(length));
                    this.s = LongArrays.ensureCapacity(this.s, length + common, common);
                    BigArrays.copyFromBig(LongArrayFrontCodedBigList.this.array, this.pos + (long)LongArrayFrontCodedList.count(length) + (long)LongArrayFrontCodedList.count(common), this.s, common, length);
                    this.pos += (long)(LongArrayFrontCodedList.count(length) + LongArrayFrontCodedList.count(common) + length);
                    length += common;
                } else {
                    length = LongArrayFrontCodedBigList.this.length(this.i);
                    this.s = LongArrays.ensureCapacity(this.s, length, 0);
                    LongArrayFrontCodedBigList.this.extract(this.i, this.s, 0, length);
                }
                ++this.i;
                return LongArrays.copy(this.s, 0, length);
            }

            @Override
            public long[] previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.inSync = false;
                return LongArrayFrontCodedBigList.this.getArray(--this.i);
            }
        };
    }

    public LongArrayFrontCodedBigList clone() {
        return this;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("[");
        for (long i = 0L; i < this.n; ++i) {
            if (i != 0L) {
                s.append(", ");
            }
            s.append(LongArrayList.wrap(this.getArray(i)).toString());
        }
        s.append("]");
        return s.toString();
    }

    protected long[][] rebuildPointerArray() {
        long[][] p = LongBigArrays.newBigArray((this.n + (long)this.ratio - 1L) / (long)this.ratio);
        long[][] a = this.array;
        long pos = 0L;
        int skip = this.ratio - 1;
        long j = 0L;
        for (long i = 0L; i < this.n; ++i) {
            int length = LongArrayFrontCodedList.readInt(a, pos);
            int count = LongArrayFrontCodedList.count(length);
            if (++skip == this.ratio) {
                skip = 0;
                BigArrays.set(p, j++, pos);
                pos += (long)(count + length);
                continue;
            }
            pos += (long)(count + LongArrayFrontCodedList.count(LongArrayFrontCodedList.readInt(a, pos + (long)count)) + length);
        }
        return p;
    }

    public void dump(DataOutputStream array, DataOutputStream pointers) throws IOException {
        long[] s;
        int n;
        long[][] arrl = this.array;
        int n2 = arrl.length;
        for (n = 0; n < n2; ++n) {
            for (long e : s = arrl[n]) {
                array.writeLong(e);
            }
        }
        arrl = this.p;
        n2 = arrl.length;
        for (n = 0; n < n2; ++n) {
            for (long e : s = arrl[n]) {
                pointers.writeLong(e);
            }
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.p = this.rebuildPointerArray();
    }
}

