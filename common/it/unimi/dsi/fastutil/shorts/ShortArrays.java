/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public final class ShortArrays {
    public static final short[] EMPTY_ARRAY = new short[0];
    public static final short[] DEFAULT_EMPTY_ARRAY = new short[0];
    private static final int QUICKSORT_NO_REC = 16;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int QUICKSORT_MEDIAN_OF_9 = 128;
    private static final int MERGESORT_NO_REC = 16;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 2;
    private static final int RADIXSORT_NO_REC = 1024;
    private static final int RADIXSORT_NO_REC_SMALL = 64;
    private static final int PARALLEL_RADIXSORT_NO_FORK = 1024;
    static final int RADIX_SORT_MIN_THRESHOLD = 1000;
    protected static final Segment POISON_PILL = new Segment(-1, -1, -1);
    public static final Hash.Strategy<short[]> HASH_STRATEGY = new ArrayHashStrategy();

    private ShortArrays() {
    }

    public static short[] forceCapacity(short[] array, int length, int preserve) {
        short[] t = new short[length];
        System.arraycopy(array, 0, t, 0, preserve);
        return t;
    }

    public static short[] ensureCapacity(short[] array, int length) {
        return ShortArrays.ensureCapacity(array, length, array.length);
    }

    public static short[] ensureCapacity(short[] array, int length, int preserve) {
        return length > array.length ? ShortArrays.forceCapacity(array, length, preserve) : array;
    }

    public static short[] grow(short[] array, int length) {
        return ShortArrays.grow(array, length, array.length);
    }

    public static short[] grow(short[] array, int length, int preserve) {
        if (length > array.length) {
            int newLength = (int)Math.max(Math.min((long)array.length + (long)(array.length >> 1), 0x7FFFFFF7L), (long)length);
            short[] t = new short[newLength];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }

    public static short[] trim(short[] array, int length) {
        if (length >= array.length) {
            return array;
        }
        short[] t = length == 0 ? EMPTY_ARRAY : new short[length];
        System.arraycopy(array, 0, t, 0, length);
        return t;
    }

    public static short[] setLength(short[] array, int length) {
        if (length == array.length) {
            return array;
        }
        if (length < array.length) {
            return ShortArrays.trim(array, length);
        }
        return ShortArrays.ensureCapacity(array, length);
    }

    public static short[] copy(short[] array, int offset, int length) {
        ShortArrays.ensureOffsetLength(array, offset, length);
        short[] a = length == 0 ? EMPTY_ARRAY : new short[length];
        System.arraycopy(array, offset, a, 0, length);
        return a;
    }

    public static short[] copy(short[] array) {
        return (short[])array.clone();
    }

    @Deprecated
    public static void fill(short[] array, short value) {
        int i = array.length;
        while (i-- != 0) {
            array[i] = value;
        }
    }

    @Deprecated
    public static void fill(short[] array, int from, int to, short value) {
        ShortArrays.ensureFromTo(array, from, to);
        if (from == 0) {
            while (to-- != 0) {
                array[to] = value;
            }
        } else {
            for (int i = from; i < to; ++i) {
                array[i] = value;
            }
        }
    }

    @Deprecated
    public static boolean equals(short[] a1, short[] a2) {
        int i = a1.length;
        if (i != a2.length) {
            return false;
        }
        while (i-- != 0) {
            if (a1[i] == a2[i]) continue;
            return false;
        }
        return true;
    }

    public static void ensureFromTo(short[] a, int from, int to) {
        Arrays.ensureFromTo(a.length, from, to);
    }

    public static void ensureOffsetLength(short[] a, int offset, int length) {
        Arrays.ensureOffsetLength(a.length, offset, length);
    }

    public static void ensureSameLength(short[] a, short[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch: " + a.length + " != " + b.length);
        }
    }

    private static ForkJoinPool getPool() {
        ForkJoinPool current = ForkJoinTask.getPool();
        return current == null ? ForkJoinPool.commonPool() : current;
    }

    public static void swap(short[] x, int a, int b) {
        short t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    public static void swap(short[] x, int a, int b, int n) {
        int i = 0;
        while (i < n) {
            ShortArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static int med3(short[] x, int a, int b, int c, ShortComparator comp) {
        int ab = comp.compare(x[a], x[b]);
        int ac = comp.compare(x[a], x[c]);
        int bc = comp.compare(x[b], x[c]);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(short[] a, int from, int to, ShortComparator comp) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (comp.compare(a[j], a[m]) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            short u = a[i];
            a[i] = a[m];
            a[m] = u;
        }
    }

    private static void insertionSort(short[] a, int from, int to, ShortComparator comp) {
        int i = from;
        while (++i < to) {
            short t = a[i];
            int j = i;
            short u = a[j - 1];
            while (comp.compare(t, u) < 0) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = a[--j - 1];
            }
            a[j] = t;
        }
    }

    public static void quickSort(short[] x, int from, int to, ShortComparator comp) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            ShortArrays.selectionSort(x, from, to, comp);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ShortArrays.med3(x, l, l + s, l + 2 * s, comp);
            m = ShortArrays.med3(x, m - s, m, m + s, comp);
            n = ShortArrays.med3(x, n - 2 * s, n - s, n, comp);
        }
        m = ShortArrays.med3(x, l, m, n, comp);
        short v = x[m];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = comp.compare(x[b], v)) <= 0) {
                if (comparison == 0) {
                    ShortArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(x[c], v)) >= 0) {
                if (comparison == 0) {
                    ShortArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ShortArrays.swap(x, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        ShortArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        ShortArrays.swap(x, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ShortArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1) {
            ShortArrays.quickSort(x, to - s, to, comp);
        }
    }

    public static void quickSort(short[] x, ShortComparator comp) {
        ShortArrays.quickSort(x, 0, x.length, comp);
    }

    public static void parallelQuickSort(short[] x, int from, int to, ShortComparator comp) {
        ForkJoinPool pool = ShortArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            ShortArrays.quickSort(x, from, to, comp);
        } else {
            pool.invoke(new ForkJoinQuickSortComp(x, from, to, comp));
        }
    }

    public static void parallelQuickSort(short[] x, ShortComparator comp) {
        ShortArrays.parallelQuickSort(x, 0, x.length, comp);
    }

    private static int med3(short[] x, int a, int b, int c) {
        int ab = Short.compare(x[a], x[b]);
        int ac = Short.compare(x[a], x[c]);
        int bc = Short.compare(x[b], x[c]);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(short[] a, int from, int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (a[j] >= a[m]) continue;
                m = j;
            }
            if (m == i) continue;
            short u = a[i];
            a[i] = a[m];
            a[m] = u;
        }
    }

    private static void insertionSort(short[] a, int from, int to) {
        int i = from;
        while (++i < to) {
            short t = a[i];
            int j = i;
            short u = a[j - 1];
            while (t < u) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = a[--j - 1];
            }
            a[j] = t;
        }
    }

    public static void quickSort(short[] x, int from, int to) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            ShortArrays.selectionSort(x, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ShortArrays.med3(x, l, l + s, l + 2 * s);
            m = ShortArrays.med3(x, m - s, m, m + s);
            n = ShortArrays.med3(x, n - 2 * s, n - s, n);
        }
        m = ShortArrays.med3(x, l, m, n);
        short v = x[m];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = Short.compare(x[b], v)) <= 0) {
                if (comparison == 0) {
                    ShortArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Short.compare(x[c], v)) >= 0) {
                if (comparison == 0) {
                    ShortArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ShortArrays.swap(x, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        ShortArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        ShortArrays.swap(x, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ShortArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1) {
            ShortArrays.quickSort(x, to - s, to);
        }
    }

    public static void quickSort(short[] x) {
        ShortArrays.quickSort(x, 0, x.length);
    }

    public static void parallelQuickSort(short[] x, int from, int to) {
        ForkJoinPool pool = ShortArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            ShortArrays.quickSort(x, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSort(x, from, to));
        }
    }

    public static void parallelQuickSort(short[] x) {
        ShortArrays.parallelQuickSort(x, 0, x.length);
    }

    private static int med3Indirect(int[] perm, short[] x, int a, int b, int c) {
        short aa = x[perm[a]];
        short bb = x[perm[b]];
        short cc = x[perm[c]];
        int ab = Short.compare(aa, bb);
        int ac = Short.compare(aa, cc);
        int bc = Short.compare(bb, cc);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void insertionSortIndirect(int[] perm, short[] a, int from, int to) {
        int i = from;
        while (++i < to) {
            int t = perm[i];
            int j = i;
            int u = perm[j - 1];
            while (a[t] < a[u]) {
                perm[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = perm[--j - 1];
            }
            perm[j] = t;
        }
    }

    public static void quickSortIndirect(int[] perm, short[] x, int from, int to) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            ShortArrays.insertionSortIndirect(perm, x, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ShortArrays.med3Indirect(perm, x, l, l + s, l + 2 * s);
            m = ShortArrays.med3Indirect(perm, x, m - s, m, m + s);
            n = ShortArrays.med3Indirect(perm, x, n - 2 * s, n - s, n);
        }
        m = ShortArrays.med3Indirect(perm, x, l, m, n);
        short v = x[perm[m]];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = Short.compare(x[perm[b]], v)) <= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Short.compare(x[perm[c]], v)) >= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, c, d--);
                }
                --c;
            }
            if (b > c) break;
            IntArrays.swap(perm, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        IntArrays.swap(perm, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        IntArrays.swap(perm, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ShortArrays.quickSortIndirect(perm, x, from, from + s);
        }
        if ((s = d - c) > 1) {
            ShortArrays.quickSortIndirect(perm, x, to - s, to);
        }
    }

    public static void quickSortIndirect(int[] perm, short[] x) {
        ShortArrays.quickSortIndirect(perm, x, 0, x.length);
    }

    public static void parallelQuickSortIndirect(int[] perm, short[] x, int from, int to) {
        ForkJoinPool pool = ShortArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            ShortArrays.quickSortIndirect(perm, x, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSortIndirect(perm, x, from, to));
        }
    }

    public static void parallelQuickSortIndirect(int[] perm, short[] x) {
        ShortArrays.parallelQuickSortIndirect(perm, x, 0, x.length);
    }

    public static void stabilize(int[] perm, short[] x, int from, int to) {
        int curr = from;
        for (int i = from + 1; i < to; ++i) {
            if (x[perm[i]] == x[perm[curr]]) continue;
            if (i - curr > 1) {
                IntArrays.parallelQuickSort(perm, curr, i);
            }
            curr = i;
        }
        if (to - curr > 1) {
            IntArrays.parallelQuickSort(perm, curr, to);
        }
    }

    public static void stabilize(int[] perm, short[] x) {
        ShortArrays.stabilize(perm, x, 0, perm.length);
    }

    private static int med3(short[] x, short[] y, int a, int b, int c) {
        int bc;
        int t = Short.compare(x[a], x[b]);
        int ab = t == 0 ? Short.compare(y[a], y[b]) : t;
        t = Short.compare(x[a], x[c]);
        int ac = t == 0 ? Short.compare(y[a], y[c]) : t;
        t = Short.compare(x[b], x[c]);
        int n = bc = t == 0 ? Short.compare(y[b], y[c]) : t;
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void swap(short[] x, short[] y, int a, int b) {
        short t = x[a];
        short u = y[a];
        x[a] = x[b];
        y[a] = y[b];
        x[b] = t;
        y[b] = u;
    }

    private static void swap(short[] x, short[] y, int a, int b, int n) {
        int i = 0;
        while (i < n) {
            ShortArrays.swap(x, y, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static void selectionSort(short[] a, short[] b, int from, int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                int u = Short.compare(a[j], a[m]);
                if (u >= 0 && (u != 0 || b[j] >= b[m])) continue;
                m = j;
            }
            if (m == i) continue;
            short t = a[i];
            a[i] = a[m];
            a[m] = t;
            t = b[i];
            b[i] = b[m];
            b[m] = t;
        }
    }

    public static void quickSort(short[] x, short[] y, int from, int to) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            ShortArrays.selectionSort(x, y, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ShortArrays.med3(x, y, l, l + s, l + 2 * s);
            m = ShortArrays.med3(x, y, m - s, m, m + s);
            n = ShortArrays.med3(x, y, n - 2 * s, n - s, n);
        }
        m = ShortArrays.med3(x, y, l, m, n);
        short v = x[m];
        short w = y[m];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int t;
            int comparison;
            if (b <= c && (comparison = (t = Short.compare(x[b], v)) == 0 ? Short.compare(y[b], w) : t) <= 0) {
                if (comparison == 0) {
                    ShortArrays.swap(x, y, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = (t = Short.compare(x[c], v)) == 0 ? Short.compare(y[c], w) : t) >= 0) {
                if (comparison == 0) {
                    ShortArrays.swap(x, y, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ShortArrays.swap(x, y, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        ShortArrays.swap(x, y, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        ShortArrays.swap(x, y, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ShortArrays.quickSort(x, y, from, from + s);
        }
        if ((s = d - c) > 1) {
            ShortArrays.quickSort(x, y, to - s, to);
        }
    }

    public static void quickSort(short[] x, short[] y) {
        ShortArrays.ensureSameLength(x, y);
        ShortArrays.quickSort(x, y, 0, x.length);
    }

    public static void parallelQuickSort(short[] x, short[] y, int from, int to) {
        ForkJoinPool pool = ShortArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            ShortArrays.quickSort(x, y, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSort2(x, y, from, to));
        }
    }

    public static void parallelQuickSort(short[] x, short[] y) {
        ShortArrays.ensureSameLength(x, y);
        ShortArrays.parallelQuickSort(x, y, 0, x.length);
    }

    public static void unstableSort(short[] a, int from, int to) {
        if (to - from >= 1000) {
            ShortArrays.radixSort(a, from, to);
        } else {
            ShortArrays.quickSort(a, from, to);
        }
    }

    public static void unstableSort(short[] a) {
        ShortArrays.unstableSort(a, 0, a.length);
    }

    public static void unstableSort(short[] a, int from, int to, ShortComparator comp) {
        ShortArrays.quickSort(a, from, to, comp);
    }

    public static void unstableSort(short[] a, ShortComparator comp) {
        ShortArrays.unstableSort(a, 0, a.length, comp);
    }

    public static void mergeSort(short[] a, int from, int to, short[] supp) {
        int len = to - from;
        if (len < 16) {
            ShortArrays.insertionSort(a, from, to);
            return;
        }
        if (supp == null) {
            supp = java.util.Arrays.copyOf(a, to);
        }
        int mid = from + to >>> 1;
        ShortArrays.mergeSort(supp, from, mid, a);
        ShortArrays.mergeSort(supp, mid, to, a);
        if (supp[mid - 1] <= supp[mid]) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int p = from;
        int q = mid;
        for (int i = from; i < to; ++i) {
            a[i] = q >= to || p < mid && supp[p] <= supp[q] ? supp[p++] : supp[q++];
        }
    }

    public static void mergeSort(short[] a, int from, int to) {
        ShortArrays.mergeSort(a, from, to, (short[])null);
    }

    public static void mergeSort(short[] a) {
        ShortArrays.mergeSort(a, 0, a.length);
    }

    public static void mergeSort(short[] a, int from, int to, ShortComparator comp, short[] supp) {
        int len = to - from;
        if (len < 16) {
            ShortArrays.insertionSort(a, from, to, comp);
            return;
        }
        if (supp == null) {
            supp = java.util.Arrays.copyOf(a, to);
        }
        int mid = from + to >>> 1;
        ShortArrays.mergeSort(supp, from, mid, comp, a);
        ShortArrays.mergeSort(supp, mid, to, comp, a);
        if (comp.compare(supp[mid - 1], supp[mid]) <= 0) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int p = from;
        int q = mid;
        for (int i = from; i < to; ++i) {
            a[i] = q >= to || p < mid && comp.compare(supp[p], supp[q]) <= 0 ? supp[p++] : supp[q++];
        }
    }

    public static void mergeSort(short[] a, int from, int to, ShortComparator comp) {
        ShortArrays.mergeSort(a, from, to, comp, null);
    }

    public static void mergeSort(short[] a, ShortComparator comp) {
        ShortArrays.mergeSort(a, 0, a.length, comp);
    }

    public static void stableSort(short[] a, int from, int to) {
        ShortArrays.unstableSort(a, from, to);
    }

    public static void stableSort(short[] a) {
        ShortArrays.stableSort(a, 0, a.length);
    }

    public static void stableSort(short[] a, int from, int to, ShortComparator comp) {
        ShortArrays.mergeSort(a, from, to, comp);
    }

    public static void stableSort(short[] a, ShortComparator comp) {
        ShortArrays.stableSort(a, 0, a.length, comp);
    }

    public static int binarySearch(short[] a, int from, int to, short key) {
        --to;
        while (from <= to) {
            int mid = from + to >>> 1;
            short midVal = a[mid];
            if (midVal < key) {
                from = mid + 1;
                continue;
            }
            if (midVal > key) {
                to = mid - 1;
                continue;
            }
            return mid;
        }
        return -(from + 1);
    }

    public static int binarySearch(short[] a, short key) {
        return ShortArrays.binarySearch(a, 0, a.length, key);
    }

    public static int binarySearch(short[] a, int from, int to, short key, ShortComparator c) {
        --to;
        while (from <= to) {
            int mid = from + to >>> 1;
            short midVal = a[mid];
            int cmp = c.compare(midVal, key);
            if (cmp < 0) {
                from = mid + 1;
                continue;
            }
            if (cmp > 0) {
                to = mid - 1;
                continue;
            }
            return mid;
        }
        return -(from + 1);
    }

    public static int binarySearch(short[] a, short key, ShortComparator c) {
        return ShortArrays.binarySearch(a, 0, a.length, key, c);
    }

    public static void radixSort(short[] a) {
        ShortArrays.radixSort(a, 0, a.length);
    }

    public static void radixSort(short[] a, int from, int to) {
        if (to - from < 1024) {
            ShortArrays.quickSort(a, from, to);
            return;
        }
        boolean maxLevel = true;
        int stackSize = 256;
        int stackPos = 0;
        int[] offsetStack = new int[256];
        int[] lengthStack = new int[256];
        int[] levelStack = new int[256];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 2 == 0 ? 128 : 0;
            int shift = (1 - level % 2) * 8;
            int i = first + length;
            while (i-- != first) {
                int n = a[i] >>> shift & 0xFF ^ signMask;
                count[n] = count[n] + 1;
            }
            int lastUsed = -1;
            int p = first;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0) {
                    lastUsed = i2;
                }
                pos[i2] = p += count[i2];
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                short t = a[i3];
                c = t >>> shift & 0xFF ^ signMask;
                if (i3 < end) {
                    while (true) {
                        int n = c;
                        int n2 = pos[n] - 1;
                        pos[n] = n2;
                        int d = n2;
                        if (n2 <= i3) break;
                        short z = t;
                        t = a[d];
                        a[d] = z;
                        c = t >>> shift & 0xFF ^ signMask;
                    }
                    a[i3] = t;
                }
                if (level < 1 && count[c] > 1) {
                    if (count[c] < 1024) {
                        ShortArrays.quickSort(a, i3, i3 + count[c]);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static void parallelRadixSort(short[] a, int from, int to) {
        ForkJoinPool pool = ShortArrays.getPool();
        if (to - from < 1024 || pool.getParallelism() == 1) {
            ShortArrays.quickSort(a, from, to);
            return;
        }
        boolean maxLevel = true;
        LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to - from, 0));
        AtomicInteger queueSize = new AtomicInteger(1);
        int numberOfThreads = pool.getParallelism();
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(pool);
        int j = numberOfThreads;
        while (j-- != 0) {
            executorCompletionService.submit(() -> {
                int[] count = new int[256];
                int[] pos = new int[256];
                while (true) {
                    Segment segment;
                    if (queueSize.get() == 0) {
                        int i = numberOfThreads;
                        while (i-- != 0) {
                            queue.add(POISON_PILL);
                        }
                    }
                    if ((segment = (Segment)queue.take()) == POISON_PILL) {
                        return null;
                    }
                    int first = segment.offset;
                    int length = segment.length;
                    int level = segment.level;
                    int signMask = level % 2 == 0 ? 128 : 0;
                    int shift = (1 - level % 2) * 8;
                    int i = first + length;
                    while (i-- != first) {
                        int n = a[i] >>> shift & 0xFF ^ signMask;
                        count[n] = count[n] + 1;
                    }
                    int lastUsed = -1;
                    int p = first;
                    for (int i2 = 0; i2 < 256; ++i2) {
                        if (count[i2] != 0) {
                            lastUsed = i2;
                        }
                        pos[i2] = p += count[i2];
                    }
                    int end = first + length - count[lastUsed];
                    int c = -1;
                    for (int i3 = first; i3 <= end; i3 += count[c]) {
                        short t = a[i3];
                        c = t >>> shift & 0xFF ^ signMask;
                        if (i3 < end) {
                            while (true) {
                                int n = c;
                                int n2 = pos[n] - 1;
                                pos[n] = n2;
                                int d = n2;
                                if (n2 <= i3) break;
                                short z = t;
                                t = a[d];
                                a[d] = z;
                                c = t >>> shift & 0xFF ^ signMask;
                            }
                            a[i3] = t;
                        }
                        if (level < 1 && count[c] > 1) {
                            if (count[c] < 1024) {
                                ShortArrays.quickSort(a, i3, i3 + count[c]);
                            } else {
                                queueSize.incrementAndGet();
                                queue.add(new Segment(i3, count[c], level + 1));
                            }
                        }
                        count[c] = 0;
                    }
                    queueSize.decrementAndGet();
                }
            });
        }
        Throwable problem = null;
        int i = numberOfThreads;
        while (i-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e) {
                problem = e.getCause();
            }
        }
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSort(short[] a) {
        ShortArrays.parallelRadixSort(a, 0, a.length);
    }

    public static void radixSortIndirect(int[] perm, short[] a, boolean stable) {
        ShortArrays.radixSortIndirect(perm, a, 0, perm.length, stable);
    }

    public static void radixSortIndirect(int[] perm, short[] a, int from, int to, boolean stable) {
        int[] support;
        if (to - from < 1024) {
            ShortArrays.quickSortIndirect(perm, a, from, to);
            if (stable) {
                ShortArrays.stabilize(perm, a, from, to);
            }
            return;
        }
        boolean maxLevel = true;
        int stackSize = 256;
        int stackPos = 0;
        int[] offsetStack = new int[256];
        int[] lengthStack = new int[256];
        int[] levelStack = new int[256];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        int[] arrn = support = stable ? new int[perm.length] : null;
        while (stackPos > 0) {
            int i;
            int p;
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 2 == 0 ? 128 : 0;
            int shift = (1 - level % 2) * 8;
            int i2 = first + length;
            while (i2-- != first) {
                int n = a[perm[i2]] >>> shift & 0xFF ^ signMask;
                count[n] = count[n] + 1;
            }
            int lastUsed = -1;
            int n = p = stable ? 0 : first;
            for (i = 0; i < 256; ++i) {
                if (count[i] != 0) {
                    lastUsed = i;
                }
                pos[i] = p += count[i];
            }
            if (stable) {
                i = first + length;
                while (i-- != first) {
                    int n2 = a[perm[i]] >>> shift & 0xFF ^ signMask;
                    int n3 = pos[n2] - 1;
                    pos[n2] = n3;
                    support[n3] = perm[i];
                }
                System.arraycopy(support, 0, perm, first, length);
                p = first;
                for (i = 0; i <= lastUsed; ++i) {
                    if (level < 1 && count[i] > 1) {
                        if (count[i] < 1024) {
                            ShortArrays.quickSortIndirect(perm, a, p, p + count[i]);
                            if (stable) {
                                ShortArrays.stabilize(perm, a, p, p + count[i]);
                            }
                        } else {
                            offsetStack[stackPos] = p;
                            lengthStack[stackPos] = count[i];
                            levelStack[stackPos++] = level + 1;
                        }
                    }
                    p += count[i];
                }
                java.util.Arrays.fill(count, 0);
                continue;
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                int t = perm[i3];
                c = a[t] >>> shift & 0xFF ^ signMask;
                if (i3 < end) {
                    while (true) {
                        int n4 = c;
                        int n5 = pos[n4] - 1;
                        pos[n4] = n5;
                        int d = n5;
                        if (n5 <= i3) break;
                        int z = t;
                        t = perm[d];
                        perm[d] = z;
                        c = a[t] >>> shift & 0xFF ^ signMask;
                    }
                    perm[i3] = t;
                }
                if (level < 1 && count[c] > 1) {
                    if (count[c] < 1024) {
                        ShortArrays.quickSortIndirect(perm, a, i3, i3 + count[c]);
                        if (stable) {
                            ShortArrays.stabilize(perm, a, i3, i3 + count[c]);
                        }
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static void parallelRadixSortIndirect(int[] perm, short[] a, int from, int to, boolean stable) {
        ForkJoinPool pool = ShortArrays.getPool();
        if (to - from < 1024 || pool.getParallelism() == 1) {
            ShortArrays.radixSortIndirect(perm, a, from, to, stable);
            return;
        }
        boolean maxLevel = true;
        LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to - from, 0));
        AtomicInteger queueSize = new AtomicInteger(1);
        int numberOfThreads = pool.getParallelism();
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(pool);
        int[] support = stable ? new int[perm.length] : null;
        int j = numberOfThreads;
        while (j-- != 0) {
            executorCompletionService.submit(() -> {
                int[] count = new int[256];
                int[] pos = new int[256];
                while (true) {
                    int i;
                    Segment segment;
                    if (queueSize.get() == 0) {
                        int i2 = numberOfThreads;
                        while (i2-- != 0) {
                            queue.add(POISON_PILL);
                        }
                    }
                    if ((segment = (Segment)queue.take()) == POISON_PILL) {
                        return null;
                    }
                    int first = segment.offset;
                    int length = segment.length;
                    int level = segment.level;
                    int signMask = level % 2 == 0 ? 128 : 0;
                    int shift = (1 - level % 2) * 8;
                    int i3 = first + length;
                    while (i3-- != first) {
                        int n = a[perm[i3]] >>> shift & 0xFF ^ signMask;
                        count[n] = count[n] + 1;
                    }
                    int lastUsed = -1;
                    int p = first;
                    for (i = 0; i < 256; ++i) {
                        if (count[i] != 0) {
                            lastUsed = i;
                        }
                        pos[i] = p += count[i];
                    }
                    if (stable) {
                        i = first + length;
                        while (i-- != first) {
                            int n = a[perm[i]] >>> shift & 0xFF ^ signMask;
                            int n2 = pos[n] - 1;
                            pos[n] = n2;
                            support[n2] = perm[i];
                        }
                        System.arraycopy(support, first, perm, first, length);
                        p = first;
                        for (i = 0; i <= lastUsed; ++i) {
                            if (level < 1 && count[i] > 1) {
                                if (count[i] < 1024) {
                                    ShortArrays.radixSortIndirect(perm, a, p, p + count[i], stable);
                                } else {
                                    queueSize.incrementAndGet();
                                    queue.add(new Segment(p, count[i], level + 1));
                                }
                            }
                            p += count[i];
                        }
                        java.util.Arrays.fill(count, 0);
                    } else {
                        int end = first + length - count[lastUsed];
                        int c = -1;
                        for (int i4 = first; i4 <= end; i4 += count[c]) {
                            int t = perm[i4];
                            c = a[t] >>> shift & 0xFF ^ signMask;
                            if (i4 < end) {
                                while (true) {
                                    int n = c;
                                    int n3 = pos[n] - 1;
                                    pos[n] = n3;
                                    int d = n3;
                                    if (n3 <= i4) break;
                                    int z = t;
                                    t = perm[d];
                                    perm[d] = z;
                                    c = a[t] >>> shift & 0xFF ^ signMask;
                                }
                                perm[i4] = t;
                            }
                            if (level < 1 && count[c] > 1) {
                                if (count[c] < 1024) {
                                    ShortArrays.radixSortIndirect(perm, a, i4, i4 + count[c], stable);
                                } else {
                                    queueSize.incrementAndGet();
                                    queue.add(new Segment(i4, count[c], level + 1));
                                }
                            }
                            count[c] = 0;
                        }
                    }
                    queueSize.decrementAndGet();
                }
            });
        }
        Throwable problem = null;
        int i = numberOfThreads;
        while (i-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e) {
                problem = e.getCause();
            }
        }
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSortIndirect(int[] perm, short[] a, boolean stable) {
        ShortArrays.parallelRadixSortIndirect(perm, a, 0, a.length, stable);
    }

    public static void radixSort(short[] a, short[] b) {
        ShortArrays.ensureSameLength(a, b);
        ShortArrays.radixSort(a, b, 0, a.length);
    }

    public static void radixSort(short[] a, short[] b, int from, int to) {
        if (to - from < 1024) {
            ShortArrays.quickSort(a, b, from, to);
            return;
        }
        int layers = 2;
        int maxLevel = 3;
        int stackSize = 766;
        int stackPos = 0;
        int[] offsetStack = new int[766];
        int[] lengthStack = new int[766];
        int[] levelStack = new int[766];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 2 == 0 ? 128 : 0;
            short[] k = level < 2 ? a : b;
            int shift = (1 - level % 2) * 8;
            int i = first + length;
            while (i-- != first) {
                int n = k[i] >>> shift & 0xFF ^ signMask;
                count[n] = count[n] + 1;
            }
            int lastUsed = -1;
            int p = first;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0) {
                    lastUsed = i2;
                }
                pos[i2] = p += count[i2];
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                short t = a[i3];
                short u = b[i3];
                c = k[i3] >>> shift & 0xFF ^ signMask;
                if (i3 < end) {
                    while (true) {
                        int n = c;
                        int n2 = pos[n] - 1;
                        pos[n] = n2;
                        int d = n2;
                        if (n2 <= i3) break;
                        c = k[d] >>> shift & 0xFF ^ signMask;
                        short z = t;
                        t = a[d];
                        a[d] = z;
                        z = u;
                        u = b[d];
                        b[d] = z;
                    }
                    a[i3] = t;
                    b[i3] = u;
                }
                if (level < 3 && count[c] > 1) {
                    if (count[c] < 1024) {
                        ShortArrays.quickSort(a, b, i3, i3 + count[c]);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static void parallelRadixSort(short[] a, short[] b, int from, int to) {
        ForkJoinPool pool = ShortArrays.getPool();
        if (to - from < 1024 || pool.getParallelism() == 1) {
            ShortArrays.quickSort(a, b, from, to);
            return;
        }
        int layers = 2;
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        int maxLevel = 3;
        LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to - from, 0));
        AtomicInteger queueSize = new AtomicInteger(1);
        int numberOfThreads = pool.getParallelism();
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(pool);
        int j = numberOfThreads;
        while (j-- != 0) {
            executorCompletionService.submit(() -> {
                int[] count = new int[256];
                int[] pos = new int[256];
                while (true) {
                    Segment segment;
                    if (queueSize.get() == 0) {
                        int i = numberOfThreads;
                        while (i-- != 0) {
                            queue.add(POISON_PILL);
                        }
                    }
                    if ((segment = (Segment)queue.take()) == POISON_PILL) {
                        return null;
                    }
                    int first = segment.offset;
                    int length = segment.length;
                    int level = segment.level;
                    int signMask = level % 2 == 0 ? 128 : 0;
                    short[] k = level < 2 ? a : b;
                    int shift = (1 - level % 2) * 8;
                    int i = first + length;
                    while (i-- != first) {
                        int n = k[i] >>> shift & 0xFF ^ signMask;
                        count[n] = count[n] + 1;
                    }
                    int lastUsed = -1;
                    int p = first;
                    for (int i2 = 0; i2 < 256; ++i2) {
                        if (count[i2] != 0) {
                            lastUsed = i2;
                        }
                        pos[i2] = p += count[i2];
                    }
                    int end = first + length - count[lastUsed];
                    int c = -1;
                    for (int i3 = first; i3 <= end; i3 += count[c]) {
                        short t = a[i3];
                        short u = b[i3];
                        c = k[i3] >>> shift & 0xFF ^ signMask;
                        if (i3 < end) {
                            while (true) {
                                int n = c;
                                int n2 = pos[n] - 1;
                                pos[n] = n2;
                                int d = n2;
                                if (n2 <= i3) break;
                                c = k[d] >>> shift & 0xFF ^ signMask;
                                short z = t;
                                short w = u;
                                t = a[d];
                                u = b[d];
                                a[d] = z;
                                b[d] = w;
                            }
                            a[i3] = t;
                            b[i3] = u;
                        }
                        if (level < 3 && count[c] > 1) {
                            if (count[c] < 1024) {
                                ShortArrays.quickSort(a, b, i3, i3 + count[c]);
                            } else {
                                queueSize.incrementAndGet();
                                queue.add(new Segment(i3, count[c], level + 1));
                            }
                        }
                        count[c] = 0;
                    }
                    queueSize.decrementAndGet();
                }
            });
        }
        Throwable problem = null;
        int i = numberOfThreads;
        while (i-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e) {
                problem = e.getCause();
            }
        }
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSort(short[] a, short[] b) {
        ShortArrays.ensureSameLength(a, b);
        ShortArrays.parallelRadixSort(a, b, 0, a.length);
    }

    private static void insertionSortIndirect(int[] perm, short[] a, short[] b, int from, int to) {
        int i = from;
        while (++i < to) {
            int t = perm[i];
            int j = i;
            int u = perm[j - 1];
            while (a[t] < a[u] || a[t] == a[u] && b[t] < b[u]) {
                perm[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = perm[--j - 1];
            }
            perm[j] = t;
        }
    }

    public static void radixSortIndirect(int[] perm, short[] a, short[] b, boolean stable) {
        ShortArrays.ensureSameLength(a, b);
        ShortArrays.radixSortIndirect(perm, a, b, 0, a.length, stable);
    }

    public static void radixSortIndirect(int[] perm, short[] a, short[] b, int from, int to, boolean stable) {
        int[] support;
        if (to - from < 64) {
            ShortArrays.insertionSortIndirect(perm, a, b, from, to);
            return;
        }
        int layers = 2;
        int maxLevel = 3;
        int stackSize = 766;
        int stackPos = 0;
        int[] offsetStack = new int[766];
        int[] lengthStack = new int[766];
        int[] levelStack = new int[766];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        int[] arrn = support = stable ? new int[perm.length] : null;
        while (stackPos > 0) {
            int i;
            int p;
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 2 == 0 ? 128 : 0;
            short[] k = level < 2 ? a : b;
            int shift = (1 - level % 2) * 8;
            int i2 = first + length;
            while (i2-- != first) {
                int n = k[perm[i2]] >>> shift & 0xFF ^ signMask;
                count[n] = count[n] + 1;
            }
            int lastUsed = -1;
            int n = p = stable ? 0 : first;
            for (i = 0; i < 256; ++i) {
                if (count[i] != 0) {
                    lastUsed = i;
                }
                pos[i] = p += count[i];
            }
            if (stable) {
                i = first + length;
                while (i-- != first) {
                    int n2 = k[perm[i]] >>> shift & 0xFF ^ signMask;
                    int n3 = pos[n2] - 1;
                    pos[n2] = n3;
                    support[n3] = perm[i];
                }
                System.arraycopy(support, 0, perm, first, length);
                p = first;
                for (i = 0; i < 256; ++i) {
                    if (level < 3 && count[i] > 1) {
                        if (count[i] < 64) {
                            ShortArrays.insertionSortIndirect(perm, a, b, p, p + count[i]);
                        } else {
                            offsetStack[stackPos] = p;
                            lengthStack[stackPos] = count[i];
                            levelStack[stackPos++] = level + 1;
                        }
                    }
                    p += count[i];
                }
                java.util.Arrays.fill(count, 0);
                continue;
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                int t = perm[i3];
                c = k[t] >>> shift & 0xFF ^ signMask;
                if (i3 < end) {
                    while (true) {
                        int n4 = c;
                        int n5 = pos[n4] - 1;
                        pos[n4] = n5;
                        int d = n5;
                        if (n5 <= i3) break;
                        int z = t;
                        t = perm[d];
                        perm[d] = z;
                        c = k[t] >>> shift & 0xFF ^ signMask;
                    }
                    perm[i3] = t;
                }
                if (level < 3 && count[c] > 1) {
                    if (count[c] < 64) {
                        ShortArrays.insertionSortIndirect(perm, a, b, i3, i3 + count[c]);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    private static void selectionSort(short[][] a, int from, int to, int level) {
        int layers = a.length;
        int firstLayer = level / 2;
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            block1: for (int j = i + 1; j < to; ++j) {
                for (int p = firstLayer; p < layers; ++p) {
                    if (a[p][j] < a[p][m]) {
                        m = j;
                        continue block1;
                    }
                    if (a[p][j] > a[p][m]) continue block1;
                }
            }
            if (m == i) continue;
            int p = layers;
            while (p-- != 0) {
                short u = a[p][i];
                a[p][i] = a[p][m];
                a[p][m] = u;
            }
        }
    }

    public static void radixSort(short[][] a) {
        ShortArrays.radixSort(a, 0, a[0].length);
    }

    public static void radixSort(short[][] a, int from, int to) {
        if (to - from < 64) {
            ShortArrays.selectionSort(a, from, to, 0);
            return;
        }
        int layers = a.length;
        int maxLevel = 2 * layers - 1;
        int p = layers;
        int l = a[0].length;
        while (p-- != 0) {
            if (a[p].length == l) continue;
            throw new IllegalArgumentException("The array of index " + p + " has not the same length of the array of index 0.");
        }
        int stackSize = 255 * (layers * 2 - 1) + 1;
        int stackPos = 0;
        int[] offsetStack = new int[stackSize];
        int[] lengthStack = new int[stackSize];
        int[] levelStack = new int[stackSize];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        short[] t = new short[layers];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 2 == 0 ? 128 : 0;
            short[] k = a[level / 2];
            int shift = (1 - level % 2) * 8;
            int i = first + length;
            while (i-- != first) {
                int n = k[i] >>> shift & 0xFF ^ signMask;
                count[n] = count[n] + 1;
            }
            int lastUsed = -1;
            int p2 = first;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0) {
                    lastUsed = i2;
                }
                pos[i2] = p2 += count[i2];
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                int p3 = layers;
                while (p3-- != 0) {
                    t[p3] = a[p3][i3];
                }
                c = k[i3] >>> shift & 0xFF ^ signMask;
                if (i3 < end) {
                    block6: while (true) {
                        int n = c;
                        int n2 = pos[n] - 1;
                        pos[n] = n2;
                        int d = n2;
                        if (n2 <= i3) break;
                        c = k[d] >>> shift & 0xFF ^ signMask;
                        p3 = layers;
                        while (true) {
                            if (p3-- == 0) continue block6;
                            short u = t[p3];
                            t[p3] = a[p3][d];
                            a[p3][d] = u;
                        }
                        break;
                    }
                    p3 = layers;
                    while (p3-- != 0) {
                        a[p3][i3] = t[p3];
                    }
                }
                if (level < maxLevel && count[c] > 1) {
                    if (count[c] < 64) {
                        ShortArrays.selectionSort(a, i3, i3 + count[c], level + 1);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static short[] shuffle(short[] a, int from, int to, Random random) {
        int i = to - from;
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            short t = a[from + i];
            a[from + i] = a[from + p];
            a[from + p] = t;
        }
        return a;
    }

    public static short[] shuffle(short[] a, Random random) {
        int i = a.length;
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            short t = a[i];
            a[i] = a[p];
            a[p] = t;
        }
        return a;
    }

    public static short[] reverse(short[] a) {
        int length = a.length;
        int i = length / 2;
        while (i-- != 0) {
            short t = a[length - i - 1];
            a[length - i - 1] = a[i];
            a[i] = t;
        }
        return a;
    }

    public static short[] reverse(short[] a, int from, int to) {
        int length = to - from;
        int i = length / 2;
        while (i-- != 0) {
            short t = a[from + length - i - 1];
            a[from + length - i - 1] = a[from + i];
            a[from + i] = t;
        }
        return a;
    }

    protected static class ForkJoinQuickSortComp
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final short[] x;
        private final ShortComparator comp;

        public ForkJoinQuickSortComp(short[] x, int from, int to, ShortComparator comp) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.comp = comp;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            short[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ShortArrays.quickSort(x, this.from, this.to, this.comp);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ShortArrays.med3(x, l, l + s, l + 2 * s, this.comp);
            m = ShortArrays.med3(x, m - s, m, m + s, this.comp);
            n = ShortArrays.med3(x, n - 2 * s, n - s, n, this.comp);
            m = ShortArrays.med3(x, l, m, n, this.comp);
            short v = x[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int comparison;
                if (b <= c && (comparison = this.comp.compare(x[b], v)) <= 0) {
                    if (comparison == 0) {
                        ShortArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = this.comp.compare(x[c], v)) >= 0) {
                    if (comparison == 0) {
                        ShortArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                ShortArrays.swap(x, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            ShortArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            ShortArrays.swap(x, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp), new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp));
            } else if (s > 1) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp));
            } else {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp));
            }
        }
    }

    protected static class ForkJoinQuickSort
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final short[] x;

        public ForkJoinQuickSort(short[] x, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            short[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ShortArrays.quickSort(x, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ShortArrays.med3(x, l, l + s, l + 2 * s);
            m = ShortArrays.med3(x, m - s, m, m + s);
            n = ShortArrays.med3(x, n - 2 * s, n - s, n);
            m = ShortArrays.med3(x, l, m, n);
            short v = x[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int comparison;
                if (b <= c && (comparison = Short.compare(x[b], v)) <= 0) {
                    if (comparison == 0) {
                        ShortArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = Short.compare(x[c], v)) >= 0) {
                    if (comparison == 0) {
                        ShortArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                ShortArrays.swap(x, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            ShortArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            ShortArrays.swap(x, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.from, this.from + s), new ForkJoinQuickSort(x, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.from, this.from + s));
            } else {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.to - t, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSortIndirect
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final int[] perm;
        private final short[] x;

        public ForkJoinQuickSortIndirect(int[] perm, short[] x, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.perm = perm;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            short[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ShortArrays.quickSortIndirect(this.perm, x, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ShortArrays.med3Indirect(this.perm, x, l, l + s, l + 2 * s);
            m = ShortArrays.med3Indirect(this.perm, x, m - s, m, m + s);
            n = ShortArrays.med3Indirect(this.perm, x, n - 2 * s, n - s, n);
            m = ShortArrays.med3Indirect(this.perm, x, l, m, n);
            short v = x[this.perm[m]];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int comparison;
                if (b <= c && (comparison = Short.compare(x[this.perm[b]], v)) <= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = Short.compare(x[this.perm[c]], v)) >= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                IntArrays.swap(this.perm, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            IntArrays.swap(this.perm, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            IntArrays.swap(this.perm, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s), new ForkJoinQuickSortIndirect(this.perm, x, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s));
            } else {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x, this.to - t, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSort2
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final short[] x;
        private final short[] y;

        public ForkJoinQuickSort2(short[] x, short[] y, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.y = y;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            short[] x = this.x;
            short[] y = this.y;
            int len = this.to - this.from;
            if (len < 8192) {
                ShortArrays.quickSort(x, y, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ShortArrays.med3(x, y, l, l + s, l + 2 * s);
            m = ShortArrays.med3(x, y, m - s, m, m + s);
            n = ShortArrays.med3(x, y, n - 2 * s, n - s, n);
            m = ShortArrays.med3(x, y, l, m, n);
            short v = x[m];
            short w = y[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int t;
                int comparison;
                if (b <= c && (comparison = (t = Short.compare(x[b], v)) == 0 ? Short.compare(y[b], w) : t) <= 0) {
                    if (comparison == 0) {
                        ShortArrays.swap(x, y, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = (t = Short.compare(x[c], v)) == 0 ? Short.compare(y[c], w) : t) >= 0) {
                    if (comparison == 0) {
                        ShortArrays.swap(x, y, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                ShortArrays.swap(x, y, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            ShortArrays.swap(x, y, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            ShortArrays.swap(x, y, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x, y, this.from, this.from + s), new ForkJoinQuickSort2(x, y, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x, y, this.from, this.from + s));
            } else {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x, y, this.to - t, this.to));
            }
        }
    }

    protected static final class Segment {
        protected final int offset;
        protected final int length;
        protected final int level;

        protected Segment(int offset, int length, int level) {
            this.offset = offset;
            this.length = length;
            this.level = level;
        }

        public String toString() {
            return "Segment [offset=" + this.offset + ", length=" + this.length + ", level=" + this.level + "]";
        }
    }

    private static final class ArrayHashStrategy
    implements Hash.Strategy<short[]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private ArrayHashStrategy() {
        }

        @Override
        public int hashCode(short[] o) {
            return java.util.Arrays.hashCode(o);
        }

        @Override
        public boolean equals(short[] a, short[] b) {
            return java.util.Arrays.equals(a, b);
        }
    }
}

